package com.tanoshi.nojiko.animequiz.model;

/**
 * Created by nojiko on 14/01/2017.
 */
public class Ranking {
    private int id;
    private int score;

    public Ranking(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
