package com.tanoshi.nojiko.animequiz.model;

/**
 * Created by nojiko on 14/01/2017.
 */
public class PersoQuestion {
    private int id;
    private String image;
    private String lastName;
    private String firstName;
    private String anime;
    private String type;
    private String themes;
    private String kind;

    public PersoQuestion(int id, String image, String lastName, String firstName, String anime, String type, String themes, String kind) {
        this.id = id;
        this.image = image;
        this.lastName = lastName;
        this.firstName = firstName;
        this.anime = anime;
        this.type = type;
        this.themes = themes;
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
