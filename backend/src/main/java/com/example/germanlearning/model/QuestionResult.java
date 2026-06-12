package com.example.germanlearning.model;

public class QuestionResult {
    private String questionId;
    private double score;
    private boolean passed;
    private String feedback;

    public QuestionResult() {
    }

    public QuestionResult(String questionId, double score, boolean passed, String feedback) {
        this.questionId = questionId;
        this.score = score;
        this.passed = passed;
        this.feedback = feedback;
    }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
