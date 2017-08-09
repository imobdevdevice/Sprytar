package com.novp.sprytar.data.model;

import android.support.annotation.IntDef;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;
import com.novp.sprytar.R;
import com.novp.sprytar.domain.PoiFileListParcelConverter;
import com.novp.sprytar.domain.PoiTriggerListParcelConverter;
import com.novp.sprytar.domain.UniversalRealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.realm.PointOfInterestRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

import static com.novp.sprytar.data.model.Amenity.CAFE;
import static com.novp.sprytar.data.model.Amenity.MUSEUM;

@Parcel(implementations = { PointOfInterestRealmProxy.class }, value = Parcel.Serialization
        .BEAN, analyze = { PointOfInterest.class })
public class PointOfInterest extends RealmObject{

    @IntDef({ARCHITECTURE, AMENITY, TOILET, PLAYGROUND, GARDEN_DESIGN, NATURE, HISTORIC, OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int ARCHITECTURE = 0;

    public static final int HISTORIC = 1;

    public static final int NATURE = 2;

    public static final int OTHER = 5;

  //  public static final int CAFE = 4;

 //   public static final int MUSEUM = 5;

    public static final int TOILET = 6;

    public static final int AMENITY = 7;

    public static final int PLAYGROUND = 8;

    public static final int GARDEN_DESIGN = 4;

    @Type
    @SerializedName("ioi_type_id")
    int type;

    @PrimaryKey
    @SerializedName(value = "ioi_id", alternate = {"id"})
    int id;

    @SerializedName(value = "ioi_name", alternate = {"name"})
    String title;

    @SerializedName("ioi_description")
    String description;

    @SerializedName("ioi_latitude")
    double latitude;

    @SerializedName("ioi_longitude")
    double longitude;

    @Ignore
    LatLng latLng;

    String photoPath;

    @SerializedName("ioi_image")
    public String image;

    @SerializedName("ioi_action")
    int action;

    @SerializedName("ioi_files")
    RealmList<PoiFile> poiFiles;

    @SerializedName("ioi_triggers")
    RealmList<PoiTrigger> poiTriggers;

    @Ignore
    MarkerOptions markerOptions;

    public PointOfInterest() {

    }

    public PointOfInterest(@Type int type, String title, double latitude, double longitude) {
        this.type = type;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                    .title(title);
            return markerOptions;
        } else {
            return null;
        }
        //.snippet("Population: 2,074,200")
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<PoiFile> getPoiFiles() {
        return poiFiles;
    }

    @ParcelPropertyConverter(PoiFileListParcelConverter.class)
    public void setPoiFiles(RealmList<PoiFile> poiFiles) {
        this.poiFiles = poiFiles;
    }

    public List<PoiTrigger> getPoiTriggers() {
        return poiTriggers;
    }

    @ParcelPropertyConverter(PoiTriggerListParcelConverter.class)
    public void setPoiTriggers(RealmList<PoiTrigger> poiTriggers) {
        this.poiTriggers = poiTriggers;
    }

    public int getIcon() {
        switch (type) {
            case TOILET:
                return R.drawable.ic_toilet_24dp;
            case PLAYGROUND:
                return R.drawable.ic_playground_24dp;
            case GARDEN_DESIGN:
                return R.drawable.ic_garden_design_24dp;
            case ARCHITECTURE:
                return R.drawable.ic_architecture_24dp;
     //       case MUSEUM:
     //           return R.drawable.ic_museum_24dp;
            case NATURE:
                return R.drawable.ic_nature_24dp;
            case HISTORIC:
                return R.drawable.ic_historic_24dp;
            case OTHER:
                return R.drawable.ic_other_24dp;
            default:
                return R.drawable.ic_coffee_24dp;
        }
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
