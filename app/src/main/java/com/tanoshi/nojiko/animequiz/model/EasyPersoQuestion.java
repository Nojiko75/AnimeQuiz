package com.tanoshi.nojiko.animequiz.model;

/**
 * Created by nojiko on 17/02/2017.
 */
public class EasyPersoQuestion {
    private int id;
    private String goodAnswer;
    private String image;
    private String answerB;
    private String answerC;
    private String answerD;

    public EasyPersoQuestion(int id, String goodAnswer, String image, String answerB, String answerC, String answerD) {
        this.id = id;
        this.goodAnswer = goodAnswer;
        this.image = image;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
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

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }
}
