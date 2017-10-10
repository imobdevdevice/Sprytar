package com.sprytar.android.game;

import com.sprytar.android.network.SpService;

import dagger.Module;
import dagger.Provides;

@Module
public class GameModule {

    @Provides
    @GameScope
    GameRepository provideGameRepository(SpService spService) {
        return new CachedGameRepository(spService);
    }

}
