package com.sprytar.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Trail implements Serializable{

    @SerializedName("trails_id")
    public long trailsId;

    @SerializedName("title")
    public String title;

    @SerializedName("min_age")
    public int minAge;

    @SerializedName("max_age")
    public int maxAge;

    @SerializedName("game_type_id")
    public int gameTypeId;

    @SerializedName("game_type_name")
    public String gameTypeName;

    @SerializedName("game_type_icon")
    public String gameTypeIcon;

    @SerializedName("venue_id")
    public long venueId;

    @SerializedName("sub_trails")
    List<SubTrail> subTrails;

    public long getTrailsId() {
        return trailsId;
    }

    public void setTrailsId(long trailsId) {
        this.trailsId = trailsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameTypeName() {
        return gameTypeName;
    }

    public void setGameTypeName(String gameTypeName) {
        this.gameTypeName = gameTypeName;
    }

    public String getGameTypeIcon() {
        return gameTypeIcon;
    }

    public void setGameTypeIcon(String gameTypeIcon) {
        this.gameTypeIcon = gameTypeIcon;
    }

    public long getVenueId() {
        return venueId;
    }

    public void setVenueId(long venueId) {
        this.venueId = venueId;
    }

    public List<SubTrail> getSubTrails() {
        return subTrails;
    }

    public void setSubTrails(List<SubTrail> subTrails) {
        this.subTrails = subTrails;
    }
}
