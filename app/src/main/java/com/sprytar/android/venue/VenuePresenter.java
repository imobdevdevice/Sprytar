package com.sprytar.android.venue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Amenity;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.PoiTrigger;
import com.sprytar.android.data.model.PointOfInterest;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.data.model.realm.VisitedLocation;
import com.sprytar.android.domain.RealmService;
import com.sprytar.android.events.GeofenceTransitionsEvent;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.services.GeofenceErrorMessages;
import com.sprytar.android.services.GeofenceTransitionsIntentService;
import com.sprytar.android.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class VenuePresenter extends BasePresenter<VenueView> implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, OnMapReadyCallback, GoogleMap
        .OnMarkerClickListener, GoogleMap.OnMapClickListener, OfflineAccessDialog.ValueListener,
        GoogleApiClient.ConnectionCallbacks, LocationSource, LocationListener, GoogleApiClient
        .OnConnectionFailedListener, ResultCallback<Status>, GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = "venue_presenter";

    private static final String TAG_AMENITY = "amenity";
    private static final String TAG_POI = "poi";

    private static final int PATTERN_DASH_LENGTH_PX = 30;
    private static final int PATTERN_GAP_LENGTH_PX = 10;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    public static final float GEOFENCE_RADIUS_IN_METERS = 7;

    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);

    private final Context context;
    private final SpService spService;
    private final RealmService realmService;
    private final SpSession spSession;
    private final GoogleApiClient googleApiClient;
    private final EventBus eventBus;

    private LocationRequest locationRequest;
    private Map<Geofence, PointOfInterest> geofencePoiMap;
    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private boolean geofencesAdded;
    private OnLocationChangedListener mapLocationListener = null;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private List<VenueActivity> venueActivities = Collections.emptyList();
    private Location location;

    PolygonOptions polygonOptions;
    private PolylineOptions polylineOptions;
    private LatLngBounds latLngBounds;
    private GoogleMap mMap = null;
    private Marker mSelectedMarker;
    private Map<Marker, PointOfInterest> itemPoiMap;
    private ArrayMap itemAmenities;

    private boolean permissionMyLocationEnabled;

    private String query;

    boolean updatedVisitedInfo;

    @Inject
    VenuePresenter(Context context, SpService spService, RealmService realmService, SpSession
            spSession, GoogleApiClient googleApiClient, EventBus eventBus) {
        this.context = context;
        this.spSession = spSession;
        this.spService = spService;
        this.realmService = realmService;
        this.googleApiClient = googleApiClient;
        this.eventBus = eventBus;

        query = "";

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setSmallestDisplacement(10); //10 meters

    }

    @Override
    public void attachView(VenueView mvpView) {
        super.attachView(mvpView);
        if (googleApiClient != null) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
        }

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        eventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        realmService.closeRealm();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        eventBus.unregister(this);
    }

    public void setLocation(Location location) {
        this.location = location;
        checkVisitedLocationUpdate();
        obtainLocationDetails();
    }

    private void obtainLocationDetails() {

        if (!Utils.isNetworkAvailable(context)) {
            getMvpView().setTitle(location.getName());
            getMvpView().showItems(location.getVenueActivities());
            createMapData();
        } else {
            getMvpView().showLoadingIndicator();

            compositeSubscription.add(spService
                    .getLocationDetails(String.valueOf(location.getId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<Location>>() {
                        @Override
                        public void call(SpResult<Location> locationSpResult) {
                            getMvpView().hideLoadingIndicator();
                            location = locationSpResult.getData();
                            venueActivities = location.getVenueActivities();
                            // addPoiAndTriggersToMap();
                            addMarkersToMap();
                            getMvpView().setTitle(location.getName());
                            getMvpView().showItems(venueActivities);
                            createMapData();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().hideLoadingIndicator();
                            if(throwable instanceof SocketTimeoutException){
                                getMvpView().showErrorDialog(true);
                            }else{
                                getMvpView().showErrorDialog(false);
                            }

                        }
                    }));
        }
    }

    public void setMyLocationEnabled(boolean permissionMyLocationEnabled) {
        this.permissionMyLocationEnabled = permissionMyLocationEnabled;
    }

    private void createMapData() {
        List<LocationBoundary> boundaries = location.getBoundaries();
        if (boundaries == null) {
            return;
        }
        polylineOptions = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        polygonOptions = new PolygonOptions();

        for (int i = 0; i < boundaries.size(); i++) {
            LatLng latLng = boundaries.get(i).getLatLng();
            polylineOptions.add(latLng);

            builder.include(latLng);

            polygonOptions.add(latLng);
        }
        polylineOptions.add(boundaries.get(0).getLatLng());
        polylineOptions.color(Color.argb(100, 59, 89, 166));

        latLngBounds = builder.build();

        polygonOptions.strokeColor(Color.argb(100, 59, 89, 166)).strokePattern(PATTERN_DASHED);

        updateMapObjects();
    }

    private void updateMapObjects() {
        if (mMap == null) {
            return;
        }
        if (polygonOptions != null) {
            Polygon polygon = mMap.addPolygon(polygonOptions);
        }

        if (latLngBounds != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getLatLng(),
                    15.5f));
        }
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
        getMvpView().showItems(location.getVenueActivities());
        return true;
    }

    public void filterLocations() {
        List<VenueActivity> venueActivitiesList;
        if (!query.isEmpty()) {
            VenueFilter filter = new VenueFilter(query);
            venueActivitiesList = Lists.newArrayList(Collections2.filter(location
                    .getVenueActivities(), filter));
            getMvpView().showItems(venueActivitiesList);
        } else {
            getMvpView().showItems(location.getVenueActivities());
        }
    }

    @Override
    public void onDestroyed() {
        realmService.closeRealm();
    }

    public void loadItems() {
        Log.d("LocationListPresenter", "loadItems " + Thread.currentThread().getName());
        getMvpView().setTitle(location.getName());
        getMvpView().showLoadingIndicator();
        getMvpView().showItems(location.getVenueActivities());
        getMvpView().hideLoadingIndicator();
    }

    public void onVenueActivityClick(VenueActivity venueActivity) {
        LatLng currentLatLng = new LatLng(Double.valueOf(spSession.getCurrentLocationLatitude()),
                Double.valueOf(spSession.getCurrentLocationLongitude()));

        boolean insideBoundaries = latLngBounds.contains(currentLatLng);

        switch (venueActivity.getGameTypeId()) {
            case VenueActivity.TREASURE_HUNT:
                getMvpView().showTreasureHuntActivity(venueActivity, insideBoundaries, location.getId(),
                        location.getName(), location.getImageLink(), location.getBoundaries());
                break;
            case VenueActivity.FITNESS:
                getMvpView().showFitnessActivity(venueActivity, location.getBoundaries());
                break;
            case VenueActivity.GUIDED_TOURS:
                getMvpView().showGuidedToursActivity(venueActivity, location.getBoundaries());
                break;
            case VenueActivity.QUIZ_GAME:
                getMvpView().showQuizGameActivity(venueActivity, insideBoundaries, location);
                break;
            case VenueActivity.TRAILS_GAME:
                getMvpView().showTrailsGameActivity(venueActivity, location.getBoundaries(),
                        insideBoundaries, location.getId());
                break;

        }
    }

    private void sendVisitedVenueResult() {

        if (!spSession.isLoggedIn()) {
            return;
        }

        LatLng currentLatLng = new LatLng(Double.valueOf(spSession.getCurrentLocationLatitude()),
                Double.valueOf(spSession.getCurrentLocationLongitude()));

        if (latLngBounds == null) {
            return;
        }

        boolean insideBoundaries = latLngBounds.contains(currentLatLng);

        if (!insideBoundaries) {
            return;
        }

        compositeSubscription.add(spService
                .sendVisitedVenue(spSession.getUserId(), location.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult response) {
                        if (!response.isSuccess()) {
                            getMvpView().showError("Error: " + response.getMessage());
                        } else {
                            setVisitedLocationUpdate();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getMvpView() != null) {
                            // getMvpView().showError(throwable.getLocalizedMessage());
                            Log.e(TAG, throwable.getMessage());
                        }
                    }
                }));
    }

    public void onOfflineClick() {
        if (realmService.hasLocation(location.getId())) {
            getMvpView().showOfflineDialog(false);
        } else {
            getMvpView().showOfflineDialog(true);
        }
    }

    @Override
    public void onValue(boolean saveDialog) {

        if (saveDialog) {
            realmService.addReplaceLocation(location, new LocationOfflineTransactionCallBack());
            compositeSubscription.add(spService
                    .getOfflineGames(location.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<List<VenueActivity>>>() {
                        @Override
                        public void call(SpResult<List<VenueActivity>> result) {
                            realmService.addReplaceVenueActivityList(result.getData(), new
                                    GamesOfflineTransactionCallBack());
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().showError(throwable.getLocalizedMessage());
                        }
                    }));
        } else {
            realmService.removeLocation(location.getId(), new LocationRemoveTransactionCallBack());
        }

    }

    private void checkVisitedLocationUpdate() {
        long todayMillis = new DateTime().withTimeAtStartOfDay().getMillis();
        updatedVisitedInfo = realmService.todayLocationUpdated(location.getId(), todayMillis);
    }

    private void setVisitedLocationUpdate() {
        updatedVisitedInfo = true;
        long todayMillis = new DateTime().withTimeAtStartOfDay().getMillis();
        realmService.addReplaceVisitedLocation(new VisitedLocation(location.getId(), todayMillis));
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.setLocationSource(this);
        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);

        mMap.setPadding(0, 0, 0, 50);

        //   addMarkersToMap();

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
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    updateMapObjects();
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(mSelectedMarker)) {
            mSelectedMarker = null;
            return true;
        }
        mSelectedMarker = marker;

        if (marker.getTag().equals(TAG_AMENITY)) {
            Amenity amenity = (Amenity) itemAmenities.get(mSelectedMarker);
            getMvpView().showAmenityDetails(amenity);
        } else if (marker.getTag().equals(TAG_POI)) {
            PointOfInterest poi = itemPoiMap.get(mSelectedMarker);
            if (poi != null) {
                getMvpView().showPoiDetails(poi);
            }
        }

        return true;
    }

    private void addMarkersToMap() {
//        itemLocationMap = new ArrayMap<>();
//        itemCameraMap = new ArrayMap<>();

        itemAmenities = new ArrayMap<>();

        List<Amenity> amenities = location.getAmenities();
        for (int i = 0; i < amenities.size(); i++) {
            Amenity amenity = amenities.get(i);
            MarkerOptions markerOptions = amenity.getMarkerOptions();
            if (markerOptions != null) {
                markerOptions.icon(getMarkerIconFromDrawable(amenity.getIcon()));
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(TAG_AMENITY);
                itemAmenities.put(marker, amenity);
            }
        }

        addPoiAndTriggersToMap();

//        CameraPosition position = CameraPosition.builder().target(location.getLatLng()).zoom
//                (15.5f).bearing(0).tilt(0).build();
    }

    private void addPoiAndTriggersToMap() {
        itemPoiMap = new ArrayMap<>();
        geofencePoiMap = new ArrayMap<>();
        geofenceList = new ArrayList<Geofence>();

        List<PointOfInterest> poiList = location.getPoiList();
        if (poiList != null) {
            for (int j = 0; j < poiList.size(); j++) {
                PointOfInterest poi = poiList.get(j);

                MarkerOptions markerOptions = poi.getMarkerOptions();
                if (markerOptions != null) {
                    markerOptions.icon(getMarkerIconFromDrawable(poi.getIcon()));
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(TAG_POI);
                    itemPoiMap.put(marker, poi);
                }

                if (spSession.isLoggedIn()) {
                    int age = spSession.getAge();
                    List<PoiTrigger> triggers = poi.getPoiTriggers();
                    for (int i = 0; i < triggers.size(); i++) {
                        PoiTrigger trigger = triggers.get(i);
                        if (age >= trigger.getMinAge() && age <= trigger.getMaxAge()) {

                            Geofence geofence = new Geofence.Builder()
                                    .setRequestId(trigger.toString())
                                    .setCircularRegion(poi.getLatitude(), poi.getLongitude(), GEOFENCE_RADIUS_IN_METERS)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .build();

                            geofencePoiMap.put(geofence, poi);
                            geofenceList.add(geofence);
                        }
                    }
                }
            }
        }

        if (geofenceList.size() > 0) {
            addGeofences();
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    public void addGeofences() {
        if (!googleApiClient.isConnected()) {
            getMvpView().showError(context.getString(R.string.not_connected_google_api));
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            getMvpView().showError(securityException.getMessage());
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            geofencesAdded = true;

        } else {
            geofencesAdded = false;
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
                    status.getStatusCode());
            Timber.e(errorMessage);
        }
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

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.moveCamera(update);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (mapLocationListener != null) {
            mapLocationListener.onLocationChanged(location);
        }
        if (!updatedVisitedInfo) {
            sendVisitedVenueResult();
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

    public final class VenueFilter implements Predicate<VenueActivity> {
        private final Pattern pattern;


        public VenueFilter(final String query) {
            this.pattern = Pattern.compile(query);
        }

        @Override
        public boolean apply(VenueActivity input) {
            return pattern.matcher(input.getName().toLowerCase()).find();
        }
    }

    private class LocationOfflineTransactionCallBack implements RealmService.OnTransactionCallback {
        @Override
        public void onRealmSuccess() {
            getMvpView().showDialogMessage(context.getString(R.string.message_save_succes));
        }

        @Override
        public void onRealmError(Throwable e) {
            getMvpView().showMessage(context.getString(R.string.message_save_error) + " " + e.getMessage());
        }
    }

    private class LocationRemoveTransactionCallBack implements RealmService
            .OnTransactionCallback {
        @Override
        public void onRealmSuccess() {
            getMvpView().showDialogMessage(context.getString(R.string.message_remove_success));
        }

        @Override
        public void onRealmError(Throwable e) {
            getMvpView().showMessage(context.getString(R.string.message_remove_error) + " " + e.getMessage());
        }
    }

    private class GamesOfflineTransactionCallBack implements RealmService.OnTransactionCallback {
        @Override
        public void onRealmSuccess() {
        }

        @Override
        public void onRealmError(Throwable e) {
            getMvpView().showMessage(context.getString(R.string.message_save_games_error) + " " + e.getMessage());
        }
    }

    @Subscribe
    public void onEvent(GeofenceTransitionsEvent event) {

        List<Geofence> geofences = event.getGeofences();
        if (geofences.size() > 0) {
            PointOfInterest poi = geofencePoiMap.get(geofences.get(0));
            if (poi != null) {
                getMvpView().showPoiDetails(poi);
            }

        }
    }

}
