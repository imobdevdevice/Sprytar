package com.novp.sprytar.data.model.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VisitedLocation extends RealmObject {

    @PrimaryKey
    private int locationId;

    private long lastUpdate;

    public VisitedLocation() {
    }

    public VisitedLocation(int locationId, long lastUpdate) {
        this.locationId = locationId;
        this.lastUpdate = lastUpdate;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
