package com.sprytar.android.venue;

import com.sprytar.android.location.LocationScope;
import com.sprytar.android.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class VenueModule {

    @Provides
    @LocationScope
    VenueRepository provideLocationRepository(SpService spService) {
        return new CloudVenueRepository(spService);
    }

}
