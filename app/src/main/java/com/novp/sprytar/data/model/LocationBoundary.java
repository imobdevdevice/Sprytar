package com.novp.sprytar.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.domain.LocationBoundaryListParcelConverter;

import org.parceler.Parcel;

import io.realm.LocationBoundaryRealmProxy;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

@Parcel(implementations = { LocationBoundaryRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze
        = { LocationBoundary.class })
public class LocationBoundary extends RealmObject {

    @SerializedName("lat")
    double latitude;

    @SerializedName("lng")
    double longitude;

    @Ignore
    LatLng latLng;

    public LocationBoundary() {
    }

    public LocationBoundary(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        if (latitude == 0.0f || longitude == 0.0f ) {
            return null;
        }
        if (latLng == null) {
            latLng = new LatLng(latitude, longitude);
        }
        return latLng;
    }

}
