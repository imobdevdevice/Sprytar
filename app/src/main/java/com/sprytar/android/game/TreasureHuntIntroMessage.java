package com.sprytar.android.game;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TreasureHuntIntroMessage {

    public TreasureHuntIntroMessage() {

    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("intro_text")
    @Expose
    private String introText;
    @SerializedName("start_location_text")
    @Expose
    private String startLocationText;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getStartLocationText() {
        return startLocationText;
    }

    public void setStartLocationText(String startLocationText) {
        this.startLocationText = startLocationText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
