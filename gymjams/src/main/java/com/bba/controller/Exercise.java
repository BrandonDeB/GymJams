package com.bba.controller;

import java.util.Date;

public class Exercise {
    private String name;
    private int weight;
    private int reps;
    private int sets;
    private Date date;
    private String playlistLink;

    public Exercise(String name, int weight, int reps, int sets, Date date, String playlistLink) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.date = date;
        this.playlistLink = playlistLink;
    }

    String getName() {
        return name;
    }
    int getWeight() {
        return weight;
    }
    int getReps() {
        return reps;
    }
    int getsets() {
        return sets;
    }

    Date getDate() {
        return date;
    }

    String getPlayListLink() {
        return playlistLink;
    }
}
