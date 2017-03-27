package com.tanoshi.nojiko.animequiz.model;

/**
 * Created by nojiko on 12/03/2017.
 */
public class MediumPersoQuestion {

    private int id;
    private String goodAnswer;
    private String image;
    private String answerA;
    private String answerB;
    private String answerC;

    public MediumPersoQuestion(int id, String goodAnswer, String image, String answerA, String answerB, String answerC) {
        this.id = id;
        this.goodAnswer = goodAnswer;
        this.image = image;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoodAnswer() {
        return goodAnswer;
    }

    public void setGoodAnswer(String goodAnswer) {
        this.goodAnswer = goodAnswer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }
}
