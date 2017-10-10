package com.sprytar.android.data.model;

import android.support.annotation.IntDef;

import org.parceler.Parcel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmObject;
import io.realm.VenueActivityDetailRealmProxy;

@Parcel(implementations = { VenueActivityDetailRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = {
        VenueActivityDetail.class })
public class VenueActivityDetail extends RealmObject {

    @IntDef({DISTANCE, GAME, HOUSE, LEAF, FRUIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int DISTANCE = 0;

    public static final int GAME = 1;

    public static final int HOUSE = 2;

    public static final int LEAF = 3;

    public static final int FRUIT = 4;

    int type;

    boolean active;

    public VenueActivityDetail() {
    }

    public VenueActivityDetail(int type, boolean active) {
        this.type = type;
        this.active = active;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
