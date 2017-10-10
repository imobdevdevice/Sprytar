package com.sprytar.android.events;

import com.google.android.gms.location.Geofence;

import java.util.List;

public class GeofenceTransitionsEvent {

    private List<Geofence> geofences;

    public GeofenceTransitionsEvent(List<Geofence> geofences) {
        this.geofences = geofences;
    }

    public List<Geofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(List<Geofence> geofences) {
        this.geofences = geofences;
    }
}
