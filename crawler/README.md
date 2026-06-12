# CEFR 德语单词爬虫

这个目录放项目的德语单词爬虫。它会按照欧标等级：

- `A1`
- `A2`
- `B1`
- `B2`
- `C1`
- `C2`

从配置的网页或本地 HTML 文件中提取德语单词，并输出为 JSON 和 CSV。

## 文件说明

### `cefr_word_crawler.py`

爬虫主程序。

特点：

- 只使用 Python 标准库，不需要额外安装第三方包。
- 支持网页 URL。
- 支持本地 HTML 文件，方便测试。
- 按 CEFR 等级分组保存。
- 自动去重。
- 自动过滤太短、太长、明显不是单词的内容。
- 支持请求间隔，避免过快访问网站。

### `sources.example.json`

爬虫来源配置示例。

你可以复制成：

```text
sources.json
```

然后把里面的 URL 换成你想爬取的词表页面。

### `examples/sample_a1.html`

本地测试用的 A1 示例 HTML。

不联网也可以用它验证爬虫是否正常。

## 快速测试

在项目根目录运行：

```powershell
python crawler\cefr_word_crawler.py --sources crawler\sources.example.json --output data\vocabulary
```

如果你的 `python` 命令不可用，可以用：

```powershell
py crawler\cefr_word_crawler.py --sources crawler\sources.example.json --output data\vocabulary
```

运行后会生成：

```text
data/vocabulary/A1.json
data/vocabulary/all_words.csv
data/vocabulary/crawl_summary.json
```

## 配置真实网页

复制配置文件：

```powershell
copy crawler\sources.example.json crawler\sources.json
```

编辑 `crawler/sources.json`：

```json
{
  "A1": [
    {
      "url": "https://example.com/a1-german-words",
      "note": "A1 word list"
    }
  ],
  "A2": [
    {
      "url": "https://example.com/a2-german-words",
      "note": "A2 word list"
    }
  ]
}
```

然后运行：

```powershell
python crawler\cefr_word_crawler.py --sources crawler\sources.json --output data\vocabulary
```

## 输出格式

每个等级会生成一个 JSON 文件，例如 `A1.json`：

```json
[
  {
    "word": "hallo",
    "level": "A1",
    "source": "crawler/examples/sample_a1.html"
  }
]
```

也会生成一个总表：

```text
all_words.csv
```

字段：

- `level`：欧标等级
- `word`：德语单词
- `source`：来源页面或文件

## 注意事项

爬真实网站时，请确认目标网站允许抓取，并控制访问频率。默认每个来源之间会等待 `1` 秒。

如果目标网站是 PDF、图片、登录后页面或强 JavaScript 渲染页面，这个标准库爬虫可能抓不到，需要改成专门的解析器。
