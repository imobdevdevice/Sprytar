package com.novp.sprytar.profile;

import com.novp.sprytar.injection.SessionScope;
import com.novp.sprytar.network.SpService;

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
