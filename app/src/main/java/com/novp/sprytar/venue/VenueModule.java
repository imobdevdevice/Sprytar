package com.novp.sprytar.venue;

import com.novp.sprytar.location.LocationRepository;
import com.novp.sprytar.location.LocationScope;
import com.novp.sprytar.network.SpService;

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
