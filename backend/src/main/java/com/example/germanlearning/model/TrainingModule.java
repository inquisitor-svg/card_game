package com.example.germanlearning.model;

public class TrainingModule {
    private String id;
    private String type;
    private String title;
    private String level;
    private String content;
    private String shadowText;
    private String tip;

    public TrainingModule() {
    }

    public TrainingModule(String id, String type, String title, String level, String content, String shadowText, String tip) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.level = level;
        this.content = content;
        this.shadowText = shadowText;
        this.tip = tip;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getShadowText() { return shadowText; }
    public void setShadowText(String shadowText) { this.shadowText = shadowText; }
    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
}
