package com.example.germanlearning.service;

import com.example.germanlearning.model.PlacementAnswer;
import com.example.germanlearning.model.PlacementQuestion;
import com.example.germanlearning.model.PlacementResult;
import com.example.germanlearning.model.PlacementSubmission;
import com.example.germanlearning.model.QuestionResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlacementService {
    private final TextSimilarityService similarityService;
    private final Map<String, PlacementQuestion> questions = new LinkedHashMap<String, PlacementQuestion>();

    public PlacementService(TextSimilarityService similarityService) {
        this.similarityService = similarityService;
        seedQuestions();
    }

    public List<PlacementQuestion> getQuestions() {
        return new ArrayList<PlacementQuestion>(questions.values());
    }

    public PlacementResult evaluate(PlacementSubmission submission) {
        List<QuestionResult> results = new ArrayList<QuestionResult>();
        double total = 0.0;

        if (submission == null || submission.getAnswers() == null || submission.getAnswers().isEmpty()) {
            return new PlacementResult(0, "A0", "先完成测试，再开始每日 20 分钟的基础输入练习。", results);
        }

        for (PlacementAnswer answer : submission.getAnswers()) {
            PlacementQuestion question = questions.get(answer.getQuestionId());
            if (question == null) {
                continue;
            }
            double score = scoreAnswer(question, answer.getAnswer());
            total += score;
            String feedback = score >= 0.75 ? "意思基本到位。" : score >= 0.45 ? "抓住了一部分意思，可以再补关键词。" : "建议复习这个表达的核心含义。";
            results.add(new QuestionResult(question.getId(), round(score * 100), score >= 0.6, feedback));
        }

        double overall = results.isEmpty() ? 0.0 : round((total / results.size()) * 100);
        String level = estimateLevel(overall);
        return new PlacementResult(overall, level, recommendationFor(level), results);
    }

    private double scoreAnswer(PlacementQuestion question, String answer) {
        double bestMeaningScore = 0.0;
        for (String accepted : question.getAcceptedMeanings()) {
            double overlap = similarityService.tokenOverlap(answer, accepted);
            double chars = similarityService.characterSimilarity(answer, accepted);
            bestMeaningScore = Math.max(bestMeaningScore, Math.max(overlap, chars * 0.85));
        }

        int keywordHits = 0;
        String normalizedAnswer = similarityService.normalize(answer);
        for (String keyword : question.getKeywords()) {
            if (normalizedAnswer.contains(similarityService.normalize(keyword))) {
                keywordHits++;
            }
        }
        double keywordScore = question.getKeywords().isEmpty() ? 0.0 : (double) keywordHits / question.getKeywords().size();
        return Math.min(1.0, bestMeaningScore * 0.7 + keywordScore * 0.35);
    }

    private String estimateLevel(double score) {
        if (score >= 86) {
            return "B1";
        }
        if (score >= 68) {
            return "A2";
        }
        if (score >= 45) {
            return "A1";
        }
        return "A0";
    }

    private String recommendationFor(String level) {
        if ("B1".equals(level)) {
            return "可以进入主题阅读和影子跟读，重点提升表达自然度。";
        }
        if ("A2".equals(level)) {
            return "建议每天 30-40 分钟，听读结合，开始复述短文本。";
        }
        if ("A1".equals(level)) {
            return "建议每天 25 分钟，先稳住常用句型、词性和基础语序。";
        }
        return "建议从发音、问候、数字和高频动词开始，每天 15-20 分钟。";
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void seedQuestions() {
        add(new PlacementQuestion("q1", "DE_TO_ZH", "Ich hätte gern einen Kaffee.", "A1",
                Arrays.asList("我想要一杯咖啡", "我想喝咖啡", "请给我一杯咖啡"),
                Arrays.asList("咖啡", "想", "一杯")));
        add(new PlacementQuestion("q2", "ZH_TO_DE", "我今天没有时间。", "A1",
                Arrays.asList("ich habe heute keine zeit", "heute habe ich keine zeit"),
                Arrays.asList("ich", "heute", "keine", "zeit")));
        add(new PlacementQuestion("q3", "DE_TO_ZH", "Der Zug kommt wegen des Wetters später an.", "A2",
                Arrays.asList("火车因为天气晚点到达", "由于天气原因火车会晚到", "列车因天气较晚抵达"),
                Arrays.asList("火车", "天气", "晚")));
        add(new PlacementQuestion("q4", "ZH_TO_DE", "如果明天下雨，我们就待在家里。", "A2",
                Arrays.asList("wenn es morgen regnet bleiben wir zu hause", "falls es morgen regnet bleiben wir zuhause"),
                Arrays.asList("wenn", "morgen", "regnet", "bleiben", "hause")));
        add(new PlacementQuestion("q5", "DE_TO_ZH", "Obwohl die Aufgabe schwer war, hat sie sie gut gelöst.", "B1",
                Arrays.asList("虽然任务很难她还是很好地解决了", "尽管这个任务困难她完成得很好"),
                Arrays.asList("虽然", "任务", "难", "解决", "好")));
    }

    private void add(PlacementQuestion question) {
        questions.put(question.getId(), question);
    }
}
