package com.sprytar.android.data.model;


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel(value = Parcel.Serialization.BEAN, analyze = { EarnedBadge.class })
public class EarnedBadge {

    @SerializedName("user_id")
    long userId;

    @SerializedName("venue_id")
    long venueId;

    @SerializedName("badge_id")
    long badgeId;

   // @SerializedName("question_status")
    int questionStatus;

    @SerializedName("badge_icon")
    String badgeIcon;

    @SerializedName("badge_name")
    String badgeName;

    @SerializedName("badge_description")
    String badgeDescription;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getVenueId() {
        return venueId;
    }

    public void setVenueId(long venueId) {
        this.venueId = venueId;
    }

    public long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(long badgeId) {
        this.badgeId = badgeId;
    }

    public int getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(int questionStatus) {
        this.questionStatus = questionStatus;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeDescription() {
        return badgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }

    @Override
    public String toString() {
        return "EarnedBadge{" +
                "userId=" + userId +
                ", venueId=" + venueId +
                ", badgeId=" + badgeId +
                ", questionStatus=" + questionStatus +
                ", badgeIcon='" + badgeIcon + '\'' +
                ", badgeName='" + badgeName + '\'' +
                ", badgeDescription='" + badgeDescription + '\'' +
                '}';
    }
}
