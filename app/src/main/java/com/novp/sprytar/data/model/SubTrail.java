package com.novp.sprytar.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubTrail implements Serializable{

    @SerializedName("id")
    public long id;

    @SerializedName("trails_id")
    public long trailsId;

    @SerializedName("name")
    public String name;

    @SerializedName("distance")
    public int distance;

    @SerializedName("typical_time")
    public String typicalTime;

    @SerializedName("difficulty")
    public String difficulty;

    @SerializedName("colour_code")
    public String colorCode;

    @SerializedName("wheelchair_access")
    public int wheelchairAccess;

    @SerializedName("filepath")
    public String filePath;

    @SerializedName("trails_points")
    List<TrailPoint> trailsPoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTrailsId() {
        return trailsId;
    }

    public void setTrailsId(long trailsId) {
        this.trailsId = trailsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTypicalTime() {
        return typicalTime;
    }

    public void setTypicalTime(String typicalTime) {
        this.typicalTime = typicalTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public int getWheelchairAccess() {
        return wheelchairAccess;
    }

    public void setWheelchairAccess(int wheelchairAccess) {
        this.wheelchairAccess = wheelchairAccess;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<TrailPoint> getTrailsPoints() {
        return trailsPoints;
    }

    public void setTrailsPoints(List<TrailPoint> trailsPoints) {
        this.trailsPoints = trailsPoints;
    }
}
