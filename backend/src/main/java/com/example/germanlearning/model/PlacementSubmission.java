package com.example.germanlearning.model;

import java.util.ArrayList;
import java.util.List;

public class PlacementSubmission {
    private List<PlacementAnswer> answers = new ArrayList<PlacementAnswer>();

    public List<PlacementAnswer> getAnswers() { return answers; }
    public void setAnswers(List<PlacementAnswer> answers) { this.answers = answers; }
}
