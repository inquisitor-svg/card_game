package com.example.germanlearning.controller;

import com.example.germanlearning.model.ShadowingRequest;
import com.example.germanlearning.model.ShadowingResult;
import com.example.germanlearning.model.TrainingModule;
import com.example.germanlearning.service.TrainingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/training")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/modules")
    public List<TrainingModule> modules() {
        return trainingService.modules();
    }

    @PostMapping("/shadowing/evaluate")
    public ShadowingResult evaluateShadowing(@RequestBody ShadowingRequest request) {
        return trainingService.evaluateShadowing(request);
    }
}
