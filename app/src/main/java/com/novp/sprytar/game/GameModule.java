package com.novp.sprytar.game;

import com.novp.sprytar.network.SpService;

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
