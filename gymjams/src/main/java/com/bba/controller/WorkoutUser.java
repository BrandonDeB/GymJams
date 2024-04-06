package com.bba.controller;

import java.util.ArrayList;

public class WorkoutUser {

    public String username;
    public ArrayList<Exercise> exercises;

    public WorkoutUser() {
        exercises = new ArrayList<Exercise>();
    }
}
