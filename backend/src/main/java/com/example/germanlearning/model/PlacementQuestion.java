package com.example.germanlearning.model;

import java.util.List;

public class PlacementQuestion {
    private String id;
    private String direction;
    private String prompt;
    private String level;
    private List<String> acceptedMeanings;
    private List<String> keywords;

    public PlacementQuestion() {
    }

    public PlacementQuestion(String id, String direction, String prompt, String level, List<String> acceptedMeanings, List<String> keywords) {
        this.id = id;
        this.direction = direction;
        this.prompt = prompt;
        this.level = level;
        this.acceptedMeanings = acceptedMeanings;
        this.keywords = keywords;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public List<String> getAcceptedMeanings() { return acceptedMeanings; }
    public void setAcceptedMeanings(List<String> acceptedMeanings) { this.acceptedMeanings = acceptedMeanings; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}
