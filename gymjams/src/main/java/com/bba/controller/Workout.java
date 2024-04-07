package com.bba.controller;

import java.util.ArrayList;
import java.util.Date;

public class Workout {

    public ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    public Date date;
    public String playlistLink;
    public String userName;

    public Workout(String link, String user) {
        playlistLink = link;
        date = new Date();
        userName = user;
    }

    public void addExercise(Exercise e) {
        exercises.add(e);
    }
}
