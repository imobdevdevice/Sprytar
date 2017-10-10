package com.sprytar.android.game;

import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CachedGameRepository implements GameRepository {

    private final SpService service;

    @Inject
    public CachedGameRepository(SpService service) {
        this.service = service;
    }

    public Observable<VenueActivity> getGameInfo(String gameId) {
        return service.getGameInfo(gameId).flatMap(new Func1<SpResult<VenueActivity>,
                Observable<VenueActivity>>() {
            @Override
            public Observable<VenueActivity> call(SpResult<VenueActivity> response) {
                if (response.isSuccess()) {
                    return Observable.just(response.getData());
                } else {
                    return Observable.just(new VenueActivity());
                }
            }
        });
    }


    public Observable<TreasureHuntIntroMessage> getGameRules() {
        return service.getGameRule().flatMap(new Func1<SpResult<TreasureHuntIntroMessage>,
                Observable<TreasureHuntIntroMessage>>() {
            @Override
            public Observable<TreasureHuntIntroMessage> call(SpResult<TreasureHuntIntroMessage> response) {
                if (response.isSuccess()) {
                    return Observable.just(response.getData());
                } else {
                    return Observable.just(new TreasureHuntIntroMessage());
                }
            }
        });
    }

}
