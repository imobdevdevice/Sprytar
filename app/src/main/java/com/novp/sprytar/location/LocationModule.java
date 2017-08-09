package com.novp.sprytar.location;

import com.novp.sprytar.network.SpService;

import dagger.Module;
import dagger.Provides;
import okhttp3.ResponseBody;
import retrofit2.Converter;

@Module
public class LocationModule {

    @Provides
    @LocationScope
    LocationRepository provideLocationRepository(SpService spService) {
        return new CloudLocationRepository(spService);
    }

}
