package com.sprytar.android.profile;

import com.sprytar.android.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileModule {

    @Provides
    @ProfileScope
    Repository provideRepository(SpService spService) {
        return new ProfileRepository(spService);
    }

}
