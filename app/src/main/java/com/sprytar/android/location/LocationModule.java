package com.sprytar.android.location;

import com.sprytar.android.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    @LocationScope
    LocationRepository provideLocationRepository(SpService spService) {
        return new CloudLocationRepository(spService);
    }

}
