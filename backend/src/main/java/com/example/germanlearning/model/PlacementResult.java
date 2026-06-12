package com.example.germanlearning.model;

import java.util.List;

public class PlacementResult {
    private double overallScore;
    private String estimatedLevel;
    private String recommendation;
    private List<QuestionResult> results;

    public PlacementResult() {
    }

    public PlacementResult(double overallScore, String estimatedLevel, String recommendation, List<QuestionResult> results) {
        this.overallScore = overallScore;
        this.estimatedLevel = estimatedLevel;
        this.recommendation = recommendation;
        this.results = results;
    }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public String getEstimatedLevel() { return estimatedLevel; }
    public void setEstimatedLevel(String estimatedLevel) { this.estimatedLevel = estimatedLevel; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public List<QuestionResult> getResults() { return results; }
    public void setResults(List<QuestionResult> results) { this.results = results; }
}
