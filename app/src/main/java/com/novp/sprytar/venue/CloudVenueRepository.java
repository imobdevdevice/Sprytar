package com.novp.sprytar.venue;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.location.LocationRepository;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class CloudVenueRepository implements VenueRepository{

    private final SpService service;

    @Inject
    public CloudVenueRepository(SpService service) {
        this.service = service;
    }

    public Observable<List<Location>> getLocationList(double latitude, double longitude) {
        return service.getLocationList(latitude, longitude).flatMap(new Func1<SpResult<List<Location>>,
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
