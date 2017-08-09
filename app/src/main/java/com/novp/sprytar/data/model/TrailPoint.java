package com.novp.sprytar.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrailPoint implements Serializable{

    @SerializedName("id")
    public long id;

    @SerializedName("sub_trails_id")
    public long subTrailsId;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("message")
    public String message;

    @SerializedName("direction")
    public String direction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSubTrailsId() {
        return subTrailsId;
    }

    public void setSubTrailsId(long subTrailsId) {
        this.subTrailsId = subTrailsId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
