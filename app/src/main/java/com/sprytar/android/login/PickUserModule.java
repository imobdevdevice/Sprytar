package com.sprytar.android.login;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.network.SpService;

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
