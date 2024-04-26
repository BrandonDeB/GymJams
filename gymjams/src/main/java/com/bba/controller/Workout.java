package com.bba.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Workout {

    public ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    public String date;
    public String playlistLink;
    public String userName;

    public Workout(String link, String user) {
        playlistLink = link;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        date = formatter.format(new Date());
        userName = user;
    }

    public void addExercise(Exercise e) {
        exercises.add(e);
    }
}
