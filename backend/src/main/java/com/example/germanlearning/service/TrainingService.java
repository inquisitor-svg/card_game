package com.example.germanlearning.service;

import com.example.germanlearning.model.ShadowingRequest;
import com.example.germanlearning.model.ShadowingResult;
import com.example.germanlearning.model.TrainingModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainingService {
    private final TextSimilarityService similarityService;
    private final String externalEvaluationUrl;

    public TrainingService(TextSimilarityService similarityService,
                           @Value("${app.external-evaluation-url:}") String externalEvaluationUrl) {
        this.similarityService = similarityService;
        this.externalEvaluationUrl = externalEvaluationUrl;
    }

    public List<TrainingModule> modules() {
        return Arrays.asList(
                new TrainingModule("listen-a1", "listening", "听力：咖啡馆点单", "A1",
                        "Guten Morgen. Ich hätte gern einen Kaffee und ein Stück Kuchen.",
                        "Ich hätte gern einen Kaffee und ein Stück Kuchen.",
                        "先听完整句，再跟读重音：hätte gern / Kaffee / Kuchen。"),
                new TrainingModule("speak-a1", "speaking", "口语：介绍今天安排", "A1",
                        "Heute lerne ich Deutsch. Danach gehe ich einkaufen.",
                        "Heute lerne ich Deutsch. Danach gehe ich einkaufen.",
                        "注意 ch、r 和句尾语调，不要逐词停顿。"),
                new TrainingModule("read-a2", "reading", "阅读：周末计划", "A2",
                        "Am Wochenende besucht Lena ihre Freundin in Hamburg. Sie fahren zusammen ans Wasser und sprechen ueber ihre Arbeit.",
                        "Am Wochenende besucht Lena ihre Freundin in Hamburg.",
                        "读完后用中文概括，再用德语说出时间、人物、地点。")
        );
    }

    public ShadowingResult evaluateShadowing(ShadowingRequest request) {
        String target = request == null ? "" : request.getTargetText();
        String transcript = request == null ? "" : request.getTranscript();
        double similarity = similarityService.characterSimilarity(transcript, target);
        double rhythm = Math.min(1.0, Math.max(0.2, similarity + lengthRhythmBonus(transcript, target)));

        String source = externalEvaluationUrl == null || externalEvaluationUrl.trim().isEmpty()
                ? "本地启发式评分"
                : "已配置外部评分接口，可在此处接入真实语音/AI 评分";
        String feedback = similarity >= 0.8
                ? source + "：跟读内容很接近原句，下一步练语速和连读。"
                : source + "：先保证关键词完整，再追求语音节奏。";
        return new ShadowingResult(round(similarity * 100), round(rhythm * 100), feedback);
    }

    private double lengthRhythmBonus(String transcript, String target) {
        int targetLength = similarityService.normalize(target).length();
        if (targetLength == 0) {
            return 0.0;
        }
        int diff = Math.abs(similarityService.normalize(transcript).length() - targetLength);
        return 0.15 - Math.min(0.25, (double) diff / targetLength);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
