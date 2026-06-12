package com.example.germanlearning.service;

import com.example.germanlearning.model.StudySessionRequest;
import com.example.germanlearning.model.StudySessionResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudySessionService {
    private final Map<LocalDate, Integer> minutesByDate = new HashMap<LocalDate, Integer>();

    public StudySessionResponse record(StudySessionRequest request) {
        int minutes = Math.max(0, request == null ? 0 : request.getMinutes());
        LocalDate today = LocalDate.now();
        minutesByDate.put(today, minutesByDate.containsKey(today) ? minutesByDate.get(today) + minutes : minutes);

        int weekly = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            if (minutesByDate.containsKey(date)) {
                weekly += minutesByDate.get(date);
            }
        }

        return new StudySessionResponse(minutesByDate.get(today), weekly, recommendation(minutesByDate.get(today), weekly), nextAction(minutesByDate.get(today)));
    }

    private String recommendation(int today, int weekly) {
        if (today < 15) {
            return "今天可以再补 10-15 分钟，优先做听力输入或影子跟读。";
        }
        if (weekly < 150) {
            return "本周建议累计到 150 分钟以上，短时高频比周末突击更稳。";
        }
        if (weekly < 300) {
            return "节奏不错。可以把 20% 时间分给主动输出，比如复述和造句。";
        }
        return "学习量很充足，注意安排复盘和休息，避免只追求时长。";
    }

    private String nextAction(int today) {
        if (today < 20) {
            return "完成一组 5 分钟听读，再录一次影子跟读。";
        }
        if (today < 45) {
            return "做一段阅读并写 3 个德语句子。";
        }
        return "结束前复盘 5 个新词，并标记明天要重复的材料。";
    }
}
