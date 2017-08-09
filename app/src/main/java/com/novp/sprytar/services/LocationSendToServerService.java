package com.novp.sprytar.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.injection.component.DaggerLocationServiceComponent;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LocationSendToServerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.
    private static final String LOGSERVICE_TAG = "location_send_to_server";

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    SpSession spSession;

    @Inject
    SpService spService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializeServiceComponent();

        if (isNetworkAvailable(getApplicationContext())) {
            buildGoogleApiClient();

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        } else {
            stopSelf();
        }
    }

    private void initializeServiceComponent() {
        DaggerLocationServiceComponent.builder()
                .sessionComponent(SprytarApplication.get(this).getSessionComponent())
                .build().inject(this);
        //  DaggerLocationComponent.builder()
        //          .applicationComponent(SprytarApplication.get(this).getComponent())
        //          .build().inject(this);
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    public void requestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.v("test_tag", "Error requesting location updates");
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission
                .ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
               // Log.i(LOGSERVICE_TAG, "lat " + l.getLatitude());
              //  Log.i(LOGSERVICE_TAG, "lng " + l.getLongitude());
                sendLastLocation(lastLocation);

            }else {
                stopSelf();
            }
        }else {
            stopSelf();
        }
    }

    private void sendLastLocation(Location location){
        compositeSubscription.add(spService.setCurrentLocation(spSession.getUserId(),location.getLongitude(),location.getLatitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JsonObject>() {
                    @Override
                    public void call(JsonObject response) {
                       // Log.v(LOGSERVICE_TAG,"received location code " + response.getCode());
                        Log.v("test_tag","response in location service: " + response);
                        stopSelf();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v(LOGSERVICE_TAG,"failed to send the current location to server");
                        Log.v("test_tag","error: " + throwable.getLocalizedMessage());
                        stopSelf();
                    }
                }));
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        super.onDestroy();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
