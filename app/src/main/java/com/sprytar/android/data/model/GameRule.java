package com.sprytar.android.data.model;

import org.parceler.Parcel;

import io.realm.GameRuleRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {GameRuleRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze
        = {GameRule.class})
public class GameRule extends RealmObject {

    int id;

    String name;

    String details;

    public GameRule() {
    }

    public GameRule(int id, String name, String details) {
        this.id = id;
        this.name = name;
        this.details = details;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
