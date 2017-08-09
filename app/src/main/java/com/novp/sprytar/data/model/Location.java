package com.novp.sprytar.data.model;

import android.support.annotation.IntDef;

import com.google.android.gms.maps.model.*;
import com.google.common.collect.ComparisonChain;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.R;
import com.novp.sprytar.domain.AmenityListParcelConverter;
import com.novp.sprytar.domain.LocationBoundaryListParcelConverter;
import com.novp.sprytar.domain.PointOfInterestListParcelConverter;
import com.novp.sprytar.domain.VenueActivityDetailListParcelConverter;
import com.novp.sprytar.domain.VenueActivityListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.realm.LocationRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { LocationRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = { Location.class })
public class Location extends RealmObject implements Comparable<Location> {

    @IntDef({PARK, STATELY_HOME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int PARK = 1;

    public static final int STATELY_HOME = 2;

    @PrimaryKey
    @SerializedName(value = "venue_id", alternate = {"id"})
    int id;

    @SerializedName(value = "venue_name", alternate = {"name"})
    String name;

    @SerializedName("venue_address")
    String address;

    String snippet;

    @SerializedName("venue_type_id")
    int type;

    @SerializedName("venue_lat")
    double latitude;

    @SerializedName("venue_lng")
    double longitude;

    @SerializedName("ioi_image")
    String ioiImage;

    RealmList<Amenity> amenities;

    @Ignore
    LatLng latLng;

    @SerializedName("venue_boundries")
    RealmList<LocationBoundary> boundaries;

    @SerializedName("venue_games")
    RealmList<VenueActivity> venueActivities;

    int photoResource;

    @SerializedName("venue_iois")
    RealmList<PointOfInterest> poiList;

    String jsonPoiFilename;

    boolean offlineAccess;

    double distance;

    @SerializedName("venue_image_link")
    String imageLink;

    public Location() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    @ParcelPropertyConverter(AmenityListParcelConverter.class)
    public void setAmenities(RealmList<Amenity> amenities) {
        this.amenities = amenities;
    }

    @Override
    public int compareTo(Location o) {
        return id < o.id ? -1 : (id == o.id ? 0 : 1);
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

    public void setLatLng() {
        latLng = new LatLng(latitude, longitude);
    }

    public MarkerOptions getMarkerOptions(){

        if (latLng == null){
            setLatLng();
        }

        return new MarkerOptions()
                .position(latLng)
                .title(name);
        //.snippet("Population: 2,074,200")
    }

    public String getTypeName() {
        switch (type) {
            case PARK:
                return "Park";
            case STATELY_HOME:
                return "Stately home";
            default:
                return "";
        }
    }

    public int getIcon() {
        switch (type) {
            case PARK:
                return R.drawable.ic_park_180dp;
            case STATELY_HOME:
                return R.drawable.ic_stately_home_180dp;
            default:
                return R.drawable.ic_park_180dp;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VenueActivity> getVenueActivities() {
        return venueActivities;
    }

    @ParcelPropertyConverter(VenueActivityListParcelConverter.class)
    public void setVenueActivities(RealmList<VenueActivity> venueActivities) {
        this.venueActivities = venueActivities;
    }

    public int getPhotoResource() {
        return photoResource;
    }

    public void setPhotoResource(int photoResource) {
        this.photoResource = photoResource;
    }

    public List<PointOfInterest> getPoiList() {
        return poiList;
    }

    @ParcelPropertyConverter(PointOfInterestListParcelConverter.class)
    public void setPoiList(RealmList<PointOfInterest> poiList) {
        this.poiList = poiList;
    }

    public String getJsonPoiFilename() {
        return jsonPoiFilename;
    }

    public void setJsonPoiFilename(String jsonPoiFilename) {
        this.jsonPoiFilename = jsonPoiFilename;
    }

    public List<LocationBoundary> getBoundaries() {
        return boundaries;
    }

    @ParcelPropertyConverter(LocationBoundaryListParcelConverter.class)
    public void setBoundaries(RealmList<LocationBoundary> boundaries) {
        this.boundaries = boundaries;
    }

    public boolean isOfflineAccess() {
        return offlineAccess;
    }

    public void setOfflineAccess(boolean offlineAccess) {
        this.offlineAccess = offlineAccess;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return this.id == other.getId();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getIoiImage() {
        return ioiImage;
    }

    public void setIoiImage(String ioiImage) {
        this.ioiImage = ioiImage;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
