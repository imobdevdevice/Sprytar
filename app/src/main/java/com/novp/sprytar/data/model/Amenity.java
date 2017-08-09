package com.novp.sprytar.data.model;

import android.net.Uri;
import android.support.annotation.IntDef;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.R;

import org.parceler.Parcel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.AmenityRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

@Parcel(implementations = { AmenityRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze
        = { Amenity.class })
public class Amenity extends RealmObject{

    @IntDef({CAFE, PARKING, TOILET, PICNIC, PLAYGROUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int CAFE = 1;

    public static final int PARKING = 2;

    public static final int TOILET = 3;

    public static final int PICNIC = 7;

    public static final int PLAYGROUND = 8;

    public static final int MUSEUM = 6;

    public static final int GIFT_SHOP = 5;

    @SerializedName("amenity_name")
    String name;

    @Type @SerializedName("amenity_type_id")
    int type;

    @SerializedName("amenity_lat")
    double latitude;

    @SerializedName("amenity_lng")
    double longitude;

    @SerializedName("description")
    String description;

    @SerializedName("image")
    String image;

    @Ignore
    LatLng latLng;

    @Ignore
    MarkerOptions markerOptions;

    public Amenity() {
    }

    public Amenity(String name, int type, double latitude, double longitude, String description, String image) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type
    public int getType() {
        return type;
    }

    public void setType(@Type int type) {
        this.type = type;
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

    public int getIcon() {
        int icon = -1;
        switch (type) {
            case CAFE:
                icon = R.drawable.ic_coffee_24dp;
                break;
            case TOILET:
                icon = R.drawable.ic_toilet_24dp;
                break;
            case PARKING:
                icon = R.drawable.ic_parking_24dp;
                break;
            case PICNIC:
                icon = R.drawable.ic_picnic_24dp;
                break;
            case GIFT_SHOP:
                icon = R.drawable.ic_gifshop_24dp;
                break;
            case MUSEUM:
                icon = R.drawable.ic_museum_24dp;
                break;
            case PLAYGROUND:
                icon = R.drawable.ic_playground_24dp;
                break;
            default:
                icon = R.drawable.ic_coffee_24dp;
                break;
        }
        return icon;
    }

    public Uri getIconUri() {
        return Uri.parse("res:///" + String.valueOf(getIcon()));
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

    public MarkerOptions getMarkerOptions() {
        if (markerOptions != null) {
            return markerOptions;
        }

        if (getLatLng() != null) {
            markerOptions = new MarkerOptions()
                    .position(getLatLng())
                    .title(name);
            return markerOptions;
        } else {
            return null;
        }
        //.snippet("Population: 2,074,200")
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
