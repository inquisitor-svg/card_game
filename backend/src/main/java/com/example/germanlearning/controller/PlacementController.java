package com.example.germanlearning.controller;

import com.example.germanlearning.model.PlacementQuestion;
import com.example.germanlearning.model.PlacementResult;
import com.example.germanlearning.model.PlacementSubmission;
import com.example.germanlearning.service.PlacementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/placement")
public class PlacementController {
    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping("/questions")
    public List<PlacementQuestion> questions() {
        return placementService.getQuestions();
    }

    @PostMapping("/evaluate")
    public PlacementResult evaluate(@RequestBody PlacementSubmission submission) {
        return placementService.evaluate(submission);
    }
}
