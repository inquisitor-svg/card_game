package com.example.germanlearning.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class TextSimilarityService {
    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replace("ß", "ss")
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("Ä", "ae")
                .replace("Ö", "oe")
                .replace("Ü", "ue")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}，。！？；：“”‘’、]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public double tokenOverlap(String answer, String expected) {
        Set<String> answerTokens = tokens(answer);
        Set<String> expectedTokens = tokens(expected);
        if (answerTokens.isEmpty() || expectedTokens.isEmpty()) {
            return 0.0;
        }
        int hits = 0;
        for (String token : expectedTokens) {
            if (answerTokens.contains(token)) {
                hits++;
            }
        }
        return (double) hits / expectedTokens.size();
    }

    public double characterSimilarity(String left, String right) {
        String a = normalize(left);
        String b = normalize(right);
        if (a.isEmpty() && b.isEmpty()) {
            return 1.0;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        int distance = levenshtein(a, b);
        int max = Math.max(a.length(), b.length());
        return Math.max(0.0, 1.0 - ((double) distance / max));
    }

    private Set<String> tokens(String value) {
        Set<String> tokens = new HashSet<String>();
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return tokens;
        }
        for (String token : normalized.split(" ")) {
            if (token.length() > 1) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
