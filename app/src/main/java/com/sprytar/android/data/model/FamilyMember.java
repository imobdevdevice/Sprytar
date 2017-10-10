package com.sprytar.android.data.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class FamilyMember {

    public int id;

    @SerializedName("username")
    public String name;

    @SerializedName("date_of_birth")
    public long birthday;

    public String role;

    String avatar;

    Uri avatarUri;

    boolean isCurrentUser;

    public FamilyMember() {
    }

    public FamilyMember(int id) {
        this.id = id;
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

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Uri getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }
}

