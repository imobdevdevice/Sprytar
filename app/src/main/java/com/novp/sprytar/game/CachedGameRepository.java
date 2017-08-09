package com.novp.sprytar.game;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CachedGameRepository implements GameRepository{

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

}
