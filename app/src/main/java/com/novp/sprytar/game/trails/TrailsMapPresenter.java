package com.novp.sprytar.game.trails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.TrailPoint;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class TrailsMapPresenter extends BasePresenter<TrailsMapView> implements OnMapReadyCallback,
        GoogleApiClient
                .ConnectionCallbacks, GoogleMap
                .OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private android.location.Location mCurrentLocation;
    private LatLngBounds latLngBounds;
    private boolean permissionMyLocationEnabled;
    private PolylineOptions polylineOptions;
    private GoogleMap mMap = null;
    private List<Marker> markers;
    private boolean[] visitedMarkers;

    private final Context context;
    private List<LocationBoundary> boundaries;
    private SubTrail subTrail;
    final float maxZoom = 20.0f;

    @Inject
    TrailsMapPresenter(Context context) {
        this.context = context;
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
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void attachView(TrailsMapView mvpView) {
        super.attachView(mvpView);
        mGoogleApiClient.connect();
    }

    @Override
    public void detachView() {
        super.detachView();
        mGoogleApiClient.disconnect();
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string
                    .open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface
                    .OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
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

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void createMapData(List<LocationBoundary> boundaries, SubTrail subTrail) {
        this.boundaries = boundaries;
        this.subTrail = subTrail;

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

    @Override
    public void onDestroyed() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startGoogleLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        moveCameraToPosition();

        try {
            checkInZonePosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void addMarkersToMap() {
        if (subTrail.getTrailsPoints().size() > 0) {
            markers = new ArrayList<>();
            visitedMarkers = new boolean[subTrail.getTrailsPoints().size()];

            //     BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.spytar_marker);
            final PatternItem DOT = new Dot();
            List<PatternItem> pattern = Arrays.asList(DOT);

            for (int i = 0; i < subTrail.getTrailsPoints().size(); i++) {
                TrailPoint point = subTrail.getTrailsPoints().get(i);
                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Sprytar");
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("trail_not_completed", 100, 100)));

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(i);
                markers.add(marker);
                visitedMarkers[i] = false;

                if (i > 0) {
                    TrailPoint prevPoint = subTrail.getTrailsPoints().get(i - 1);
                    LatLng previous = new LatLng(prevPoint.getLatitude(), prevPoint.getLongitude());
                    // trails line changed to 20
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(previous, latLng)
                            .width(20)
                            .pattern(pattern)
                            .color(Color.CYAN));
                }
            }
        }
    }

    public void moveCameraToPosition() {


        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng
                (mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).zoom(maxZoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private Marker addMarkerCompleted(android.location.Location location, int position) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Sprytar");
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("trail_completed", 100, 100)));

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(position);
        return marker;
    }

    private Marker addMarkerNotCompleted(android.location.Location location, int position) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        // .title("Sprytar");
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("trail_not_completed", 100, 100)));

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(position);
        return marker;
    }

    public void clearMap() {
        for (int i = 0; i < subTrail.getTrailsPoints().size(); i++) {
            visitedMarkers[i] = false;

            android.location.Location locToCheck = new Location("loc");
            locToCheck.setLatitude(subTrail.getTrailsPoints().get(i).getLatitude());
            locToCheck.setLongitude(subTrail.getTrailsPoints().get(i).getLongitude());
            markers.get(i).remove();
            markers.set(i, addMarkerNotCompleted(locToCheck, i));
        }
    }

    public void updateVisitedTrails(boolean[] visitedTrails) {
        this.visitedMarkers = visitedTrails;
    }

    private void checkInZonePosition() {
        boolean newVisitHappened = false;
        int positionVisited = -1;
        for (int i = 0; i < subTrail.getTrailsPoints().size(); i++) {
            android.location.Location locToCheck = new Location("loc");
            locToCheck.setLatitude(subTrail.getTrailsPoints().get(i).getLatitude());
            locToCheck.setLongitude(subTrail.getTrailsPoints().get(i).getLongitude());
            //first check if near a location
            if (mCurrentLocation.distanceTo(locToCheck) < 5) {
                // next check if the location is already visited
                if (visitedMarkers[i] == false) {
                    visitedMarkers[i] = true;
                    markers.get(i).remove();
                    markers.set(i, addMarkerCompleted(locToCheck, i));
                    positionVisited = i;
                    newVisitHappened = true;
                }
            }
        }

        if (newVisitHappened) {
            getMvpView().onTrailVisited(visitedMarkers, positionVisited);
            boolean allVisited = true;
            for (int i = 0; i < visitedMarkers.length; i++) {
                if (visitedMarkers[i] == false) {
                    allVisited = false;
                }
            }

            // check if all locations are visited
            if (allVisited) {
                stopLocationUpdates();
                getMvpView().onGameCompleted();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);

        mMap.setPadding(0, 0, 0, 50);
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        addMarkersToMap();

        mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(permissionMyLocationEnabled);
        }

        if (polylineOptions != null) {
            googleMap.addPolyline(polylineOptions.color(R.color.colorAccent));
        }

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localized.
        googleMap.setContentDescription("Trails view");

        final View mapView = getMvpView().getChildFragment();
//        final View mapView = getChildFragmentManager().findFragmentById(R.id.map).getView();
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        // marker click implemented for future work.
//        Log.v("test_tag", "position " + (int) marker.getTag());
        int clickedMarkerPos = (int) marker.getTag();
        if (!visitedMarkers[clickedMarkerPos])
            getMvpView().onTrailVisited(visitedMarkers, clickedMarkerPos);

        return true;
    }
}
