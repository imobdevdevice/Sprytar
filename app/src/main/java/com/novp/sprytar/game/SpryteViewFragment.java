package com.novp.sprytar.game;

import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.presentation.BaseFragmentUpdateable;
import com.novp.sprytar.util.ui.UpdateableFragment;
import com.novp.sprytar.wikiarchitect.AbstractArchitectCamFragmentV4;
import com.novp.sprytar.wikiarchitect.LocationProvider;
import com.novp.sprytar.wikiarchitect.WikitudeSDKConstants;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;


public class SpryteViewFragment extends AbstractArchitectCamFragmentV4 implements
        UpdateableFragment {

    public static final String SHAPE_PARAM = "com.novp.sprytar.game.SpryteViewFragment" +
            ".shapeParam";
    public static final String CURRENT_LANLNG_PARAM = "com.novp.sprytar.game.SpryteViewFragment" +
            ".currentParam";
    private static final String TAG = "SampleCamFragment";
    private double latitude;
    private double longitude;

    /**
     * last time the calibration toast was shown, this avoids too many toast shown when compass
     * needs calibration
     */
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    public static SpryteViewFragment newInstance(List<LocationBoundary> boundaries, LatLng
			currentLatLn) {
        Bundle args = new Bundle();

        //args.putDouble(LATITUDE_EXTRA, latitude);
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));

        SpryteViewFragment fragment = new SpryteViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        if (arguments != null) {
            LatLng currentLatLng = Parcels.unwrap(arguments.getParcelable(CURRENT_LANLNG_PARAM));
            latitude = currentLatLng.latitude;
            longitude = currentLatLng.longitude;
        }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.injectData(getPoiInformation());
    }

    @Override
    public void updateContent(Bundle bundle) {
        LatLng currentLatLng = Parcels.unwrap(bundle.getParcelable(BaseFragmentUpdateable
				.UPDATE_PARAM_1));
        latitude = currentLatLng.latitude;
        longitude = currentLatLng.longitude;
        this.injectData(getPoiInformation());
    }

    @Override
    public String getARchitectWorldPath() {
        return "05_3d$Models_6_3d$Model$At$Geo$Location/index.html";
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_spryte_view;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return WikitudeSDKConstants.WIKITUDE_SDK_KEY;
    }

    @Override
    public SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged(int accuracy) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && getActivity() !=
						null && !getActivity().isFinishing() && System.currentTimeMillis() -
						SpryteViewFragment.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText(getActivity(), R.string.compass_accuracy_low, Toast
							.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public ArchitectView.ArchitectWorldLoadedListener getWorldLoadedListener() {
        return new ArchitectView.ArchitectWorldLoadedListener() {
            @Override
            public void worldWasLoaded(String url) {
                Log.i(TAG, "worldWasLoaded: url: " + url);
            }

            @Override
            public void worldLoadFailed(int errorCode, String description, String failingUrl) {
                Log.e(TAG, "worldLoadFailed: url: " + failingUrl + " " + description);
            }
        };
    }

    @Override
    public ArchitectUrlListener getUrlListener() {
        return new ArchitectUrlListener() {

            @Override
            public boolean urlWasInvoked(String uriString) {
                Uri invokedUri = Uri.parse(uriString);
                if ("showlookscreen".equalsIgnoreCase(invokedUri.getHost())) {
                    Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return true;
            }
        };
    }

    @Override
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this.getActivity(), locationListener);
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


}
