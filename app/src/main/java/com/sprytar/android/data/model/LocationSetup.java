package com.sprytar.android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class LocationSetup {

    @SerializedName("question")
    List<Question> questions;

    @SerializedName("ioi")
    List<PointOfInterest> pois;

    public LocationSetup() {
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<PointOfInterest> getPois() {
        return pois;
    }

    public void setPois(List<PointOfInterest> pois) {
        this.pois = pois;
    }

}


