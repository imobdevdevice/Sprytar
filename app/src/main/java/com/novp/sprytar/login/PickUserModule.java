package com.novp.sprytar.login;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class PickUserModule {

    @Provides
    @PickUserScope
    LoginRepository provideLoginRepository(SpService spService, SpSession session) {
        return new CloudLoginRepository(spService,session);
    }

}
