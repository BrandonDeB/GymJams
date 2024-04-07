package com.bba.controller;

import java.util.ArrayList;

public class WorkoutUser {

    public ArrayList<Exercise> exercises;

    public WorkoutUser(ArrayList<Exercise> e) {
        exercises = e;
    }

    public ArrayList<Exercise> addExercise(Exercise e) {
        exercises.add(e);
        return exercises;
    }
}
