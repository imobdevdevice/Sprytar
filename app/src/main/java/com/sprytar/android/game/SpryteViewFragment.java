package com.sprytar.android.game;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.R;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.presentation.BaseFragmentUpdateable;
import com.sprytar.android.util.ui.UpdateableFragment;
import com.sprytar.android.wikiarchitect.AbstractArchitectCamFragmentV4;
import com.sprytar.android.wikiarchitect.LocationProvider;
import com.sprytar.android.wikiarchitect.WikitudeSDKConstants;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;


public class SpryteViewFragment extends AbstractArchitectCamFragmentV4 implements
        UpdateableFragment,GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    public static final String SHAPE_PARAM = "com.sprytar.android.game.SpryteViewFragment" +
            ".shapeParam";
    public static final String CURRENT_LANLNG_PARAM = "com.sprytar.android.game.SpryteViewFragment" +
            ".currentParam";
    public static final String CURRENT_LOCATION="current_location";
    public static final String SPRYTE_DISTANCE="spryte_zone";
    private static final String TAG = "SampleCamFragment";
    public static final String SHOW_RANGE_FINDER = "show_range_finder";
    private double latitude;
    private double longitude;
    private View view;
    private TextView tvCompass;
    private RelativeLayout relCompass;
    protected Location mCurrentLocation;
    protected Location mQuestionLocation;
    protected LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private boolean showRange = false;
    private  double distance = 10;
    private double spryteDistance=10;
    private Callback callback;
    private List<LocationBoundary> boundaries;
    private LocationProvider locationProvider;
    private LatLng currentLatLng;
    protected GoogleApiClient mGoogleApiClient;
    /**
     *
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        startGoogleLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public static SpryteViewFragment newInstance(List<LocationBoundary> boundaries, LatLng
            currentLatLn, boolean showRange, Location currentLocation, double spryteDistance) {
        Bundle args = new Bundle();

        //args.putDouble(LATITUDE_EXTRA, latitude);
        args.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));
        args.putParcelable(CURRENT_LOCATION,Parcels.wrap(currentLocation));
        args.putParcelable(SHOW_RANGE_FINDER, Parcels.wrap(showRange));
        args.putParcelable(SPRYTE_DISTANCE,Parcels.wrap(spryteDistance));
        SpryteViewFragment fragment = new SpryteViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        if (arguments != null) {
            currentLatLng = Parcels.unwrap(arguments.getParcelable(CURRENT_LANLNG_PARAM));
            mCurrentLocation = Parcels.unwrap(arguments.getParcelable(CURRENT_LOCATION));
            if(arguments.containsKey(SPRYTE_DISTANCE)){
                spryteDistance =Parcels.unwrap(arguments.getParcelable(SPRYTE_DISTANCE));
                if(spryteDistance <=0)spryteDistance=10;
                showRange = Parcels.unwrap(arguments.getParcelable(SHOW_RANGE_FINDER));
                buildGoogleApiClient();
            }
            latitude = currentLatLng.latitude;
            longitude = currentLatLng.longitude;
            mQuestionLocation = new Location("");
            mQuestionLocation.setLatitude(currentLatLng.latitude);
            mQuestionLocation.setLongitude(currentLatLng.longitude);
            boundaries = Parcels.unwrap(arguments.getParcelable
                    (SHAPE_PARAM));

          }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initUI() {
        if (showRange) {

            buildGoogleApiClient();
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            distance = mCurrentLocation.distanceTo(mQuestionLocation);
            String distanceStr = "";
            if(distance>999){
                distanceStr+="999+m";
            }else{
                distanceStr=Math.round(distance)+"m";
            }
            updateRange(distanceStr);
            checkSpryteZOne();
            relCompass.setVisibility(View.VISIBLE);
        }else{
            relCompass.setVisibility(View.GONE);
        }
    }

    private void checkSpryteZOne(){

        if (distance <= spryteDistance) {
            //mLocationManager.removeUpdates(this);
             stopLocationUpdates();
            if (callback != null) {
                callback.onInSpryteZone();
            }
        }
       else if(distance > 20){
            //mLocationManager.removeUpdates(this);
            stopLocationUpdates();
            if (callback != null) {
                callback.onGameMapZone(boundaries,currentLatLng,spryteDistance);


            }
        }
}


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mGoogleApiClient !=null)
        mGoogleApiClient.connect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mGoogleApiClient !=null)
        mGoogleApiClient.disconnect();
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_spryte_view, null);
        relCompass = (RelativeLayout) view.findViewById(R.id.relCompass);
        tvCompass = (TextView) view.findViewById(R.id.tvCompass);
        initUI();
        return view;
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
          locationProvider =new LocationProvider(this.getActivity(), locationListener);
          this.locationListener =locationListener;
          return locationProvider;
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

    private void updateRange(String distance){
        tvCompass.setText(distance);
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGoogleLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient !=null)
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void startGoogleLocationUpdates() {

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }else {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                    .ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest,this);
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        distance = mCurrentLocation.distanceTo(mQuestionLocation);
        updateRange(String.valueOf(Math.round(distance)) + "m");
        checkSpryteZOne();
    }


    public interface Callback{
        void onGameMapZone(List<LocationBoundary> boundaries, LatLng currentLatLn, double distance);
        void onInSpryteZone();

    }

}
