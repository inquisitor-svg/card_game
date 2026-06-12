package com.example.germanlearning.controller;

import com.example.germanlearning.model.StudySessionRequest;
import com.example.germanlearning.model.StudySessionResponse;
import com.example.germanlearning.service.StudySessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-sessions")
public class StudyController {
    private final StudySessionService studySessionService;

    public StudyController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping
    public StudySessionResponse record(@RequestBody StudySessionRequest request) {
        return studySessionService.record(request);
    }
}
