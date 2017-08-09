package com.novp.sprytar.data.model;

import com.google.gson.annotations.SerializedName;

public class ProfileVisitedVenue {

    int id;

    String name;

    @SerializedName("image")
    String imagePath;

    @SerializedName("times_visited")
    int timesVisited;

    public ProfileVisitedVenue() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getTimesVisited() {
        return timesVisited;
    }

    public void setTimesVisited(int timesVisited) {
        this.timesVisited = timesVisited;
    }

}
