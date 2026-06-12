package com.example.germanlearning.model;

public class ShadowingResult {
    private double pronunciationScore;
    private double rhythmScore;
    private String feedback;

    public ShadowingResult() {
    }

    public ShadowingResult(double pronunciationScore, double rhythmScore, String feedback) {
        this.pronunciationScore = pronunciationScore;
        this.rhythmScore = rhythmScore;
        this.feedback = feedback;
    }

    public double getPronunciationScore() { return pronunciationScore; }
    public void setPronunciationScore(double pronunciationScore) { this.pronunciationScore = pronunciationScore; }
    public double getRhythmScore() { return rhythmScore; }
    public void setRhythmScore(double rhythmScore) { this.rhythmScore = rhythmScore; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
