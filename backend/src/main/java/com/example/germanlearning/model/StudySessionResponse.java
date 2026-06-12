package com.example.germanlearning.model;

public class StudySessionResponse {
    private int minutesToday;
    private int weeklyMinutes;
    private String recommendation;
    private String nextBestAction;

    public StudySessionResponse() {
    }

    public StudySessionResponse(int minutesToday, int weeklyMinutes, String recommendation, String nextBestAction) {
        this.minutesToday = minutesToday;
        this.weeklyMinutes = weeklyMinutes;
        this.recommendation = recommendation;
        this.nextBestAction = nextBestAction;
    }

    public int getMinutesToday() { return minutesToday; }
    public void setMinutesToday(int minutesToday) { this.minutesToday = minutesToday; }
    public int getWeeklyMinutes() { return weeklyMinutes; }
    public void setWeeklyMinutes(int weeklyMinutes) { this.weeklyMinutes = weeklyMinutes; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getNextBestAction() { return nextBestAction; }
    public void setNextBestAction(String nextBestAction) { this.nextBestAction = nextBestAction; }
}
