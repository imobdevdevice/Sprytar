package com.sprytar.android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.PoiTriggerRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { PoiTriggerRealmProxy.class }, value = Parcel.Serialization
        .BEAN, analyze = { PoiTrigger.class })
public class PoiTrigger extends RealmObject {

    @SerializedName("ioi_trigger_distance")
    double distance;

    @SerializedName("ioi_trigger_min_age")
    int minAge;

    @SerializedName("ioi_trigger_max_age")
    int maxAge;

    public PoiTrigger() {
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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
}
