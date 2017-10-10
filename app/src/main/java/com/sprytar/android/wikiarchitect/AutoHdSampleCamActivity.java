package com.sprytar.android.wikiarchitect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wikitude.common.camera.CameraSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class AutoHdSampleCamActivity extends SampleCamActivity {
    private static final String LATITUDE_EXTRA = "com.sprytar.android.game.latitudeExtra";
    private static final String LONGITUDE_EXTRA = "com.sprytar.android.game.longtitudeExtra";

    private double latitude;
    private double longitude;

    public static void start(Context context, double latitude, double longitude) {

        Intent starter = new Intent(context, AutoHdSampleCamActivity.class);
        starter.putExtra(LATITUDE_EXTRA, latitude);
        starter.putExtra(LONGITUDE_EXTRA, longitude);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latitude = getIntent().getDoubleExtra(LATITUDE_EXTRA, 0.0f);
        longitude = getIntent().getDoubleExtra(LONGITUDE_EXTRA, 0.0f);

    }

    @Override
    public CameraSettings.CameraResolution getCameraResolution() {
        return CameraSettings.CameraResolution.AUTO;
    }

    public JSONArray getPoiInformation() {

        if (latitude == 0.0f || longitude == 0.0f) {
            return null;
        }

        final JSONArray pois = new JSONArray();

        // ensure these attributes are also used in JavaScript when extracting POI data
        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";

        final HashMap<String, String> poiInformation = new HashMap<String, String>();
        poiInformation.put(ATTR_ID, String.valueOf(1));
        poiInformation.put(ATTR_NAME, "Hint");
        poiInformation.put(ATTR_DESCRIPTION, "Hint");
        poiInformation.put(ATTR_LATITUDE, String.valueOf(latitude));
        poiInformation.put(ATTR_LONGITUDE, String.valueOf(longitude));
        final float UNKNOWN_ALTITUDE = -32768f;  // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)
        // Use "AR.CONST.UNKNOWN_ALTITUDE" to tell ARchitect that altitude of places should be on user level. Be aware to handle altitude properly in locationManager in case you use valid POI altitude value (e.g. pass altitude only if GPS accuracy is <7m).
        poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
        pois.put(new JSONObject(poiInformation));

        return pois;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.injectData(getPoiInformation());
    }
}
