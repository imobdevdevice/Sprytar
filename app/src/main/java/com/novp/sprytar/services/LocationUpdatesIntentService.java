package com.novp.sprytar.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.events.LocationUpdateEvent;
import com.novp.sprytar.injection.component.DaggerServiceComponent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class LocationUpdatesIntentService extends IntentService{

    public static final String ACTION_PROCESS_UPDATES =
            "com.novp.sprytar.services.locationupdatesintentservice" +
                    ".PROCESS_UPDATES";

    private static final String TAG = LocationUpdatesIntentService.class.getSimpleName();

    @Inject
    SpSession spSession;

    @Inject
    EventBus eventBus;

    public LocationUpdatesIntentService() {
        // Name the worker thread.
        super(TAG);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerServiceComponent.builder()
                .applicationComponent(SprytarApplication.get(this).getComponent())
                .build().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location lastLocation = result.getLastLocation();
                    Location storedLocation = new Location(lastLocation);
                    storedLocation.reset();
                    storedLocation.setLatitude(Double.valueOf(spSession.getCurrentLocationLatitude()));
                    storedLocation.setLongitude(Double.valueOf(spSession.getCurrentLocationLongitude()));
                    spSession.setCurrentLocation(lastLocation);
                    if (lastLocation.distanceTo(storedLocation) >= 100) {
                        eventBus.post(new LocationUpdateEvent());
                    }
                }
            }
        }
    }
}
