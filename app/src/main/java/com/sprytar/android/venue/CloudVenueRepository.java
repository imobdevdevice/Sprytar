package com.sprytar.android.venue;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CloudVenueRepository implements VenueRepository {

    private final SpService service;

    @Inject
    public CloudVenueRepository(SpService service) {
        this.service = service;
    }

    public Observable<List<Location>> getLocationList(double latitude, double longitude) {
        return service.getLocationList(latitude, longitude,1).flatMap(new Func1<SpResult<List<Location>>,
                        Observable<List<Location>>>() {
            @Override
            public Observable<List<Location>> call(SpResult<List<Location>> listResponse) {
                if (listResponse.isSuccess()) {
                    return Observable.just(listResponse.getData());
                } else {
                    return Observable.just(Collections.<Location>emptyList());
                }
            }
        });
    }
}
