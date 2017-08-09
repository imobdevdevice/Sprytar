package com.novp.sprytar.data.model;

import com.google.gson.annotations.SerializedName;

public class ProfilePlayedGame {

    int id;

    String title;

    @SerializedName("times_played")
    int timesPlayed;

    public ProfilePlayedGame() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }
}
