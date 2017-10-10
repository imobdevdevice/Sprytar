package com.sprytar.android.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.PointOfInterest;
import com.sprytar.android.events.LocationUpdateEvent;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class LocationPresenter extends BasePresenter<LocationView> implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener,
        LocationListInteractorSubscriber.SubscriberCallback, OnMapReadyCallback, GoogleMap
        .OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationSource, LocationListener, GoogleApiClient
        .OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener {

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    private final LocationInteractor locationInteractor;
    private final LocationListInteractor locationListInteractor;
    private final Context context;
    private final SpSession spSession;
    private final GoogleApiClient googleApiClient;
    private final EventBus bus;

    LatLngBounds latLngBounds;
    private List<Location> locations = Collections.emptyList();
    private boolean permissionMyLocationEnabled;
    private GoogleMap mMap = null;
    private Marker mSelectedMarker;
    private boolean mapLoaded;

    private LocationRequest locationRequest;
    private OnLocationChangedListener mapLocationListener = null;

    private Map<Location, Marker> itemLocationMap;
    private Map<Location, CameraPosition> itemCameraMap;

    private String currentLatitude;
    private String currentLongitude;
    private int isSuperUser;

    private String query;
    private LatLng currentLatLng;
    private int zoomLevel;

    @Inject
    LocationPresenter(LocationInteractor locationInteractor, LocationListInteractor
            locationListInteractor, Context context, SpSession spSession, GoogleApiClient
                              googleApiClient, EventBus bus) {
        this.locationInteractor = locationInteractor;
        this.locationListInteractor = locationListInteractor;
        this.spSession = spSession;
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.bus = bus;

        this.mapLoaded = false;
        query = "";
        currentLatitude = spSession.getCurrentLocationLatitude();
        currentLongitude = spSession.getCurrentLocationLongitude();
        isSuperUser = spSession.isSuperUser();
        currentLatLng = new LatLng(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setSmallestDisplacement(100); //100 meters

    }

    public void setMyLocationEnabled(boolean permissionMyLocationEnabled) {
        this.permissionMyLocationEnabled = permissionMyLocationEnabled;
    }

    @Override
    public void attachView(LocationView mvpView) {
        super.attachView(mvpView);
        if (googleApiClient != null) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
        }

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        bus.register(this);
    }

    @Override
    public void detachView() {
        Log.d("view_detached","view_detached");
        super.detachView();
        locationInteractor.unsubscribe();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        bus.unregister(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

            query = newText;
            filterLocations();

        return true;
    }

    @Override
    public boolean onClose() {
        getMvpView().showItems(locations);
        return true;
    }

    public void filterLocations() {
        List<Location> locationList;
        try {
            if (!query.isEmpty()) {
                LocationFilter filter = new LocationFilter(query);
                if(locations == null)Log.d("locationserror", "locations were null");
                locationList = Lists.newArrayList(Collections2.filter(locations, filter));
                if(locations != null && getMvpView() !=null) getMvpView().showItems(locations);

            } else {
                if(locations == null)Log.d("locationserror", "locations were null");
                if(locations != null && getMvpView() !=null) getMvpView().showItems(locations);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }}

    @Override
    public void onDestroyed() {
        locationInteractor.unsubscribe();
    }

    public void loadItems() {

        if (!isViewAttached()) {
            return;
        }

        getMvpView().showLoadingIndicator();
        locationListInteractor.execute(new LocationListInteractorSubscriber(this), String.valueOf(Utils
                .isNetworkAvailable(context)), currentLatitude, currentLongitude, String.valueOf(isSuperUser));

    }

    @Override
    public void onItemsReceived(List<Location> items) {
        this.locations = items;
        if (!isViewAttached()) {
            return;
        }
        getMvpView().showItems(locations);
        createMapData();
    }

    private void createMapData() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < locations.size(); i++) {
            builder.include(locations.get(i).getLatLng());
        }

        latLngBounds = builder.build();

        addMarkersToMap();
        moveCameraToBounds();

    }

    private void moveCameraToBounds() {
        if (latLngBounds == null || !mapLoaded) {
            return;
        }

//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setLocationSource(this);

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        mMap.setPadding(0, 0, 0, 50);
        zoomLevel = getZoomLevel(60);

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

        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Venue view");

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
                    mapLoaded = true;
                    moveCameraToBounds();
                }
            });
        }
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

    private void addMarkersToMap() {
        itemLocationMap = new ArrayMap<>();
        itemCameraMap = new ArrayMap<>();

        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location.getLatLng()).title(location.getName()).icon
                    (getMarkerIconFromDrawable(location.getIcon()));

            Marker marker = mMap.addMarker(markerOptions);

            CameraPosition position = CameraPosition.builder().target(location.getLatLng()).zoom
                    (15.5f).bearing(0).tilt(0).build();

            itemLocationMap.put(location, marker);
            itemCameraMap.put(location, position);
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel);
        mMap.animateCamera(cameraUpdate);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) context.getResources().getDrawable(id);

            vectorDrawable.setBounds(0, 0, 48, 48);

            Bitmap bm = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.moveCamera(update);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (mapLocationListener != null) {
            mapLocationListener.onLocationChanged(location);
        }
        android.location.Location lastLocation = location;
        android.location.Location storedLocation = new android.location.Location(lastLocation);
        storedLocation.reset();
        storedLocation.setLatitude(Double.valueOf(spSession.getCurrentLocationLatitude()));
        storedLocation.setLongitude(Double.valueOf(spSession.getCurrentLocationLongitude()));
        spSession.setCurrentLocation(lastLocation);
        currentLatitude = spSession.getCurrentLocationLatitude();
        currentLongitude = spSession.getCurrentLocationLongitude();
        if (lastLocation.distanceTo(storedLocation) >= 100) {
            loadItems();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mapLocationListener = onLocationChangedListener;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void deactivate() {
        mapLocationListener = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLastLocation();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this);  // LocationListener
        } catch (SecurityException securityException) {
            getMvpView().showError(securityException.getMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void requestLastLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || permissionMyLocationEnabled) {
            android.location.Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            if (lastLocation != null) {
                spSession.setCurrentLocation(lastLocation);
                currentLatitude = String.valueOf(lastLocation.getLatitude());
                currentLongitude = String.valueOf(lastLocation.getLongitude());
                loadItems();
            }
        }
    }

    private List<PointOfInterest> getQuestionsFromAssets(String filename) {
        List<PointOfInterest> result = new ArrayList<>();

        Type type = new TypeToken<List<PointOfInterest>>() {
        }.getType();

        Gson gson = new Gson();
        result = gson.fromJson(loadJSONFromAsset(filename), type);

        return result;
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public final class LocationFilter implements Predicate<Location> {
        private final Pattern pattern;

        public LocationFilter(final String query) {
            this.pattern = Pattern.compile(query);
        }

        @Override
        public boolean apply(Location input) {
            return pattern.matcher(input.getName().toLowerCase()).find();
        }
    }

    @Subscribe
    public void onEvent(LocationUpdateEvent event) {
        currentLatitude = spSession.getCurrentLocationLatitude();
        currentLongitude = spSession.getCurrentLocationLongitude();
        loadItems();
    }

    public int getZoomLevel(int radiusInKm) {
        int zoomLevel = 0;
        double radius = radiusInKm * 1000;
        double scale = radius / 500;
        zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        return zoomLevel;
    }
}
