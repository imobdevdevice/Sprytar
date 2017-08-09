package com.novp.sprytar.main;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.novp.sprytar.R;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.LocationSetup;
import com.novp.sprytar.domain.RealmService;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;
import com.novp.sprytar.services.LocationUpdatesIntentService;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivityPresenter extends BasePresenter<MainActivityView> implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final SpSession spSession;
    private final RealmService realmService;
    private final Context context;
    private final SpService spService;

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean permissionMyLocationEnabled;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    MainActivityPresenter(Context context, SpSession
            spSession, RealmService realmService, SpService spService) {
        this.context = context;
        this.spSession = spSession;
        this.realmService = realmService;
        this.spService = spService;
    }

    @Override
    public void attachView(MainActivityView mvpView) {
        super.attachView(mvpView);
//        buildGoogleApiClient();
//        if(!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
    }

    @Override
    public void detachView() {
        super.detachView();
        realmService.closeRealm();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
//        removeLocationUpdates();
//
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onDestroyed() {
    }

    public void setMyLocationEnabled(boolean permissionMyLocationEnabled) {
        this.permissionMyLocationEnabled = permissionMyLocationEnabled;
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {

        Intent intent = new Intent(context, LocationUpdatesIntentService.class);
        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public void requestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            getMvpView().showError("Error requesting location updates");
            e.printStackTrace();
        }
    }

    private void requestLastLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || permissionMyLocationEnabled) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                spSession.setCurrentLocation(lastLocation);
            }
        }
    }

    public void removeLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    getPendingIntent());
        }
    }

    public void onLogoutClick() {
        if (spSession.isLoggedIn()) {
            spSession.logout();
        }
        getMvpView().showSplashUi();
    }

    public void showDisclaimerIfNeed() {
        if (spSession.showDisclaimer()) {
            getMvpView().showDisclaimerDialog();
            spSession.setShowDisclaimerFalse();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLastLocation();
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void registerDeviceForPushNotifications(String firebaseToken, String deviceId) {
        compositeSubscription.add(spService
                .setDeviceToken(spSession.getUserId(),firebaseToken,deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult spResult) {
                        if (spResult.isSuccess()) {
                            Log.v("test_tag","code " + spResult.getMessage());
                        } else {
                            Log.v("test_tag","failed to register the device");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v("test_tag",throwable.getMessage());
                    }
                }));
    }
}
