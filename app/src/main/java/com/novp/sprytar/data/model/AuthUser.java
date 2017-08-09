package com.novp.sprytar.data.model;

import org.parceler.Parcel;

import java.util.List;

@SuppressWarnings("unused")
@Parcel
public class AuthUser extends SpUser {

    String email;

    String password;

    String token;

    boolean isAdmin;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
