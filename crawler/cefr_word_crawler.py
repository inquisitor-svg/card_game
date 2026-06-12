#!/usr/bin/env python3
"""
CEFR German vocabulary crawler.

The crawler reads a JSON source config, extracts German-looking words from each
configured page or local HTML file, and writes one JSON file per CEFR level plus
a combined CSV file.
"""

import argparse
import csv
import html
import json
import os
import re
import sys
import time
from dataclasses import dataclass
from html.parser import HTMLParser
from pathlib import Path
from typing import Dict, Iterable, List, Sequence
from urllib.parse import urlparse
from urllib.request import Request, urlopen


CEFR_LEVELS = ("A1", "A2", "B1", "B2", "C1", "C2")
WORD_PATTERN = re.compile(r"^[A-Za-zÄÖÜäöüß][A-Za-zÄÖÜäöüß-]{1,39}$")
TOKEN_PATTERN = re.compile(r"[A-Za-zÄÖÜäöüß][A-Za-zÄÖÜäöüß-]{1,39}")
SKIP_TAGS = {"script", "style", "noscript", "svg"}
TEXT_TAGS = {"a", "li", "td", "th", "p", "span", "strong", "em", "option", "div"}
DEFAULT_USER_AGENT = "GermanLearningVocabularyCrawler/1.0 (+local learning project)"


@dataclass(frozen=True)
class WordEntry:
    word: str
    level: str
    source: str


class VocabularyHTMLParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self._tag_stack: List[str] = []
        self._chunks: List[str] = []

    def handle_starttag(self, tag: str, attrs) -> None:
        self._tag_stack.append(tag.lower())

    def handle_endtag(self, tag: str) -> None:
        tag = tag.lower()
        if tag in self._tag_stack:
            index = len(self._tag_stack) - 1 - self._tag_stack[::-1].index(tag)
            self._tag_stack.pop(index)

    def handle_data(self, data: str) -> None:
        if not data or any(tag in SKIP_TAGS for tag in self._tag_stack):
            return
        if self._tag_stack and self._tag_stack[-1] not in TEXT_TAGS:
            return
        cleaned = " ".join(data.split())
        if cleaned:
            self._chunks.append(cleaned)

    def text(self) -> str:
        return "\n".join(self._chunks)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Crawl German vocabulary by CEFR level.")
    parser.add_argument("--sources", default="crawler/sources.json", help="Path to source config JSON.")
    parser.add_argument("--output", default="data/vocabulary", help="Output directory.")
    parser.add_argument("--delay", type=float, default=1.0, help="Seconds to wait between remote requests.")
    parser.add_argument("--min-length", type=int, default=2, help="Minimum word length.")
    parser.add_argument("--max-per-source", type=int, default=0, help="Limit words per source; 0 means no limit.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    source_path = Path(args.sources)
    if not source_path.exists():
        print(f"Source config not found: {source_path}", file=sys.stderr)
        print("Tip: copy crawler/sources.example.json to crawler/sources.json or pass --sources.", file=sys.stderr)
        return 2

    sources = load_sources(source_path)
    output_dir = Path(args.output)
    output_dir.mkdir(parents=True, exist_ok=True)

    grouped: Dict[str, List[WordEntry]] = {level: [] for level in CEFR_LEVELS}
    summary = {"levels": {}, "source_file": str(source_path), "generated_at": time.strftime("%Y-%m-%dT%H:%M:%S%z")}

    for level in CEFR_LEVELS:
        level_sources = sources.get(level, [])
        seen = set()
        for source in level_sources:
            url = source.get("url", "").strip()
            if not url:
                continue
            print(f"[{level}] crawling {url}")
            try:
                content = fetch_text(url)
                words = extract_words(content, min_length=args.min_length)
                if args.max_per_source > 0:
                    words = words[: args.max_per_source]
                for word in words:
                    key = normalize_word(word)
                    if key not in seen:
                        seen.add(key)
                        grouped[level].append(WordEntry(word=word, level=level, source=url))
            except Exception as exc:
                print(f"[{level}] failed {url}: {exc}", file=sys.stderr)
            if is_remote_url(url):
                time.sleep(max(0.0, args.delay))

        write_level_file(output_dir, level, grouped[level])
        summary["levels"][level] = {
            "sources": len(level_sources),
            "words": len(grouped[level]),
            "file": f"{level}.json",
        }

    write_csv(output_dir / "all_words.csv", grouped)
    write_json(output_dir / "crawl_summary.json", summary)
    print(f"Done. Output: {output_dir}")
    return 0


def load_sources(path: Path) -> Dict[str, List[dict]]:
    with path.open("r", encoding="utf-8") as file:
        data = json.load(file)
    result = {}
    for level in CEFR_LEVELS:
        values = data.get(level, [])
        if not isinstance(values, list):
            raise ValueError(f"{level} must be a list")
        result[level] = values
    return result


def fetch_text(url: str) -> str:
    if is_remote_url(url):
        request = Request(url, headers={"User-Agent": DEFAULT_USER_AGENT})
        with urlopen(request, timeout=20) as response:
            charset = response.headers.get_content_charset() or "utf-8"
            return response.read().decode(charset, errors="replace")

    path = Path(url)
    if not path.is_absolute():
        path = Path.cwd() / path
    return path.read_text(encoding="utf-8")


def is_remote_url(value: str) -> bool:
    parsed = urlparse(value)
    return parsed.scheme in {"http", "https"}


def extract_words(content: str, min_length: int = 2) -> List[str]:
    parser = VocabularyHTMLParser()
    parser.feed(content)
    text = html.unescape(parser.text())

    words = []
    seen = set()
    for token in TOKEN_PATTERN.findall(text):
        word = clean_word(token)
        key = normalize_word(word)
        if len(word) < min_length:
            continue
        if key in seen:
            continue
        if not WORD_PATTERN.match(word):
            continue
        if looks_like_noise(word):
            continue
        seen.add(key)
        words.append(word)
    return words


def clean_word(value: str) -> str:
    value = value.strip("-").strip()
    if value.isupper() and len(value) > 1:
        return value.title()
    return value


def normalize_word(value: str) -> str:
    return value.strip().lower().replace("ß", "ss")


def looks_like_noise(word: str) -> bool:
    lower = word.lower()
    if lower.startswith(("http", "www")):
        return True
    if lower in {"html", "body", "main", "title", "class", "style", "script"}:
        return True
    if "--" in word:
        return True
    return False


def write_level_file(output_dir: Path, level: str, entries: Sequence[WordEntry]) -> None:
    write_json(output_dir / f"{level}.json", [entry.__dict__ for entry in entries])


def write_json(path: Path, data) -> None:
    with path.open("w", encoding="utf-8") as file:
        json.dump(data, file, ensure_ascii=False, indent=2)
        file.write("\n")


def write_csv(path: Path, grouped: Dict[str, Sequence[WordEntry]]) -> None:
    with path.open("w", encoding="utf-8-sig", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=["level", "word", "source"])
        writer.writeheader()
        for level in CEFR_LEVELS:
            for entry in grouped[level]:
                writer.writerow(entry.__dict__)


if __name__ == "__main__":
    raise SystemExit(main())
