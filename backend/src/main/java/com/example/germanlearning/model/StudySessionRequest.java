package com.example.germanlearning.model;

import java.util.ArrayList;
import java.util.List;

public class StudySessionRequest {
    private int minutes;
    private List<String> focusAreas = new ArrayList<String>();

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = minutes; }
    public List<String> getFocusAreas() { return focusAreas; }
    public void setFocusAreas(List<String> focusAreas) { this.focusAreas = focusAreas; }
}
