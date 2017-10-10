package com.sprytar.android.venuesetup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.LocationSetup;
import com.sprytar.android.data.model.PointOfInterest;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class VenueSetupPresenter extends BasePresenter<VenueSetupView> implements GoogleApiClient.ConnectionCallbacks, LocationSource, LocationListener, GoogleApiClient
        .OnConnectionFailedListener {

    public static final String QUESTION_PARAM = "question";
    public static final String POI_PARAM = "ioi";

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    private final Context context;
    private final SpSession spSession;
    private final SpService spService;
    private final GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;
    private OnLocationChangedListener mapLocationListener = null;
    private boolean permissionMyLocationEnabled;

    private Location location;
    private LocationSetup locationSetup;
    private List<Question> questions;
    private List<PointOfInterest> pois;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private String currentLatitude;
    private String currentLongitude;

    @Inject
    VenueSetupPresenter(Context context, SpSession spSession, SpService spService, GoogleApiClient
            googleApiClient) {
        this.context = context;
        this.spSession = spSession;
        this.spService = spService;
        this.googleApiClient = googleApiClient;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

    }

    @Override
    public void attachView(VenueSetupView mvpView) {
        super.attachView(mvpView);
        if (googleApiClient != null) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
        }
        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void setLocation(Location location) {

        this.location = location;
        obtainLocationDetails();

    }

    private void obtainLocationDetails() {
        getMvpView().showLoadingIndicator();

        compositeSubscription.add(spService
                .getLocationSetup(String.valueOf(location.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<LocationSetup>>() {
                    @Override
                    public void call(SpResult<LocationSetup> locationSpResult) {
                        getMvpView().hideLoadingIndicator();
                        locationSetup = locationSpResult.getData();
                        questions = locationSetup.getQuestions();
                        pois = locationSetup.getPois();
                        getMvpView().showLocationDetails(location, locationSetup);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().hideLoadingIndicator();
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));

    }

    public void onSetLocation(String type, int position) {

        if (currentLongitude == null || currentLatitude == null) {
            getMvpView().showDialogMessage(context.getString(R.string.venue_setup_location_error));
            return;
        }

        int id = -1;
        if (type == POI_PARAM) {
            id = pois.get(position).getId();
        } else {
            id = questions.get(position).globalId;
        }

        compositeSubscription.add(spService
                .setCurrentLocationIoiQuestion(id, type, currentLongitude, currentLatitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult spResult) {
                        getMvpView().hideLoadingIndicator();
                        if (spResult.isSuccess()) {
                            getMvpView().showDialogMessage(context.getString(R.string.venue_setup_success_update));
                        } else {
                            getMvpView().showDialogMessage("Failure during update: " + spResult.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().hideLoadingIndicator();
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));


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
            }
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
//        if (mapLocationListener != null) {
//            mapLocationListener.onLocationChanged(location);
//        }
        currentLatitude = Double.toString(location.getLatitude());
        currentLongitude = Double.toString(location.getLongitude());
    }
}
