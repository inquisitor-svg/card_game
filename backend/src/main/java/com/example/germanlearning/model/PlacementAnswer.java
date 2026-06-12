package com.example.germanlearning.model;

import javax.validation.constraints.NotBlank;

public class PlacementAnswer {
    @NotBlank
    private String questionId;
    private String answer;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
