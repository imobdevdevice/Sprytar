package com.sprytar.android.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GameMapPresenter extends BasePresenter<GameMapView> implements OnMapReadyCallback,
        GoogleMap
                .OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleApiClient
                .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String REQUESTING_LOCATION_UPDATES_KEY =
            "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private final Context context;
    private final SpSession spSession;
    private final SpService spService;
    private final float zoom = 15.5f;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Location mQuestionLocation;
    LatLngBounds latLngBounds;
    private boolean permissionMyLocationEnabled;
    private List<LocationBoundary> boundaries;
    private PolylineOptions polylineOptions;
    private LatLng currentLatLng;
    private GoogleMap mMap = null;
    private Marker mSelectedMarker;
    private Marker currentMarker;
    private Location lastLocation;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0/*, objectCurrentDegree = 0, objectDegree = 0*/;

    private LatLng targetLocation;
    private Sensor msensor;
    private float firstTimeDegree;
    private double QUESTION_DISTANCE = 10.0;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private int degree, objectDegree;
    private float currentDegree = 0f, currentObjectDegree = 0f;


    @Inject
    GameMapPresenter(Context context, SpSession spSession, SpService spService) {
        this.context = context;
        this.spSession = spSession;
        this.spService = spService;

        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
//        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        sensorManager.registerListener(this, msensor,
//                SensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(this, gsensor,
//                SensorManager.SENSOR_DELAY_GAME);
        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
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


    @Override
    public void attachView(GameMapView mvpView) {
        super.attachView(mvpView);
        mGoogleApiClient.connect();

    }

    @Override
    public void detachView() {
        super.detachView();
        mGoogleApiClient.disconnect();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {

    }

    public void createMapData(List<LocationBoundary> boundaries, LatLng currentLatLng, double distance) {

        this.boundaries = boundaries;
        this.currentLatLng = currentLatLng;
        mQuestionLocation = new Location("");
        mQuestionLocation.setLatitude(currentLatLng.latitude);
        mQuestionLocation.setLongitude(currentLatLng.longitude);
        if (distance > 0) QUESTION_DISTANCE = distance;

        polylineOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < boundaries.size(); i++) {
            LatLng latLng = boundaries.get(i).getLatLng();
            polylineOptions.add(latLng);
            builder.include(latLng);
        }
        polylineOptions.add(boundaries.get(0).getLatLng());

        latLngBounds = builder.build();

    }

    public void setDistance(LatLng currentLatLng) {
        mQuestionLocation = new Location("");
        mQuestionLocation.setLatitude(currentLatLng.latitude);
        mQuestionLocation.setLongitude(currentLatLng.longitude);
    }


    private void startGoogleLocationUpdates() {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
                getMvpView().showNoGpsMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (mGoogleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission
                    .ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } else {
                if (permissionMyLocationEnabled) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest, this);
                }
            }
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);

        mMap.setPadding(0, 0, 0, 50);

        addMarkersToMap();

        // Set listener for marker click event.  See the bottom of this class for its behavior.
        mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            map.setMyLocationEnabled(permissionMyLocationEnabled);
        }

        if (polylineOptions != null) {
            map.addPolyline(polylineOptions.color(R.color.colorAccent));
        }

        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Venue view");

        final View mapView = getMvpView().getChildFragment();
        if (mapView != null) {
            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                        .OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation") // We use the new method when supported
                    @SuppressLint("NewApi") // We check which build version we are using.
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                    }
                });
            }
        }
//        final View mapView = getChildFragmentManager().findFragmentById(R.id.map).getView();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    public void updateMapContent(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
        addMarkersToMap();
    }

    private void addMarkersToMap() {
        LatLng mQuestionLatLng = new LatLng(mQuestionLocation.getLatitude(), mQuestionLocation.getLongitude());
        firstTimeDegree =
                bearing(currentLatLng.latitude, currentLatLng.longitude,
                        mQuestionLocation.getLatitude(), mQuestionLocation.getLongitude());
        if (mQuestionLatLng != null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.spytar_marker);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mQuestionLatLng)
                    .title("Sprytar");
            markerOptions.icon(icon);
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(markerOptions);
            targetLocation = currentMarker.getPosition();
            CameraPosition position = CameraPosition.builder().target(mQuestionLatLng).zoom
                    (15.5f).bearing(0).tilt(0).build();
        }
    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.moveCamera(update);
    }

    public void moveCameraToPosition() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng
                (mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).zoom(mMap
                .getCameraPosition().zoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void checkInZonePosition() {
        double distance = mCurrentLocation.distanceTo(mQuestionLocation);
        String distanceStr = "";
        if (distance > 999) {
            distanceStr = "999+m";
        } else {
            distanceStr = String.valueOf(Math.round(distance)) + "m";
        }

        getMvpView().onChangeDistance(distanceStr);
        if (distance <= 20) {
            if (distance <= QUESTION_DISTANCE) {
                stopLocationUpdates();
                getMvpView().showInSpryteZone();
            } else {
                if (Utils.hasCompass(context)) {
                    stopLocationUpdates();
                    getMvpView().showInARZone();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        moveCameraToPosition();
        // sendCurrentLocation(location);
        try {
            checkInZonePosition();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    private void sendCurrentLocation(Location location) {
        if (lastLocation == null) {
            lastLocation = new Location(location);
            lastLocation.reset();
            lastLocation.setLongitude(location.getLongitude());
            lastLocation.setLatitude(location.getLatitude());
        }

        if (location.distanceTo(lastLocation) >= 20) {
            lastLocation.setLongitude(location.getLongitude());
            lastLocation.setLatitude(location.getLatitude());
            compositeSubscription.add(spService.setCurrentLocation(spSession.getUserId(), location.getLongitude(), location.getLatitude())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<JsonObject>() {
                        @Override
                        public void call(JsonObject response) {
                            Log.v("test_tag", "code " + response.toString());
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().showError(throwable.getLocalizedMessage());
                        }
                    }));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGoogleLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private int bearing(double startLat, double startLng, double endLat, double endLng) {
        double longitude1 = Math.toRadians(startLng);
        double longitude2 = Math.toRadians(endLng);

        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);

        double longDiff = Math.toRadians(longitude2 - longitude1);

        double y = Math.sin(longDiff) * Math.cos(latitude2);

        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
        double radiansBearing = Math.toDegrees(Math.atan2(y, x));
        radiansBearing = (360 - ((radiansBearing + 360) % 360));
        int degrees = (int) radiansBearing;
        return degrees;
    }

    private void adjustArrow() {
        Animation an = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        an.setDuration(210);
        an.setFillAfter(true);
        currentDegree = -degree;

        objectDegree = (int) (firstTimeDegree + degree);
        Animation anObject = new RotateAnimation(currentObjectDegree, -objectDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anObject.setDuration(210);
        anObject.setFillAfter(true);
        currentObjectDegree = -objectDegree;

        getMvpView().onChangeDirection(an, anObject);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        degree = Math.round(event.values[0]);
        adjustArrow();
    }
}
