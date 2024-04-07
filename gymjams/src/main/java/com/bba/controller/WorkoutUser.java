package com.bba.controller;

import java.util.ArrayList;

public class WorkoutUser {

    public ArrayList<Workout> workout;
    public ArrayList<String> friends;

    public WorkoutUser(ArrayList<Workout> e) {
        workout = e;
        friends = new ArrayList<String>();
    }

    public void addWorkout(Workout e) {
        workout.add(e);
    }

}
