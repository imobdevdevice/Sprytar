package com.novp.sprytar.location;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

public class CloudLocationRepository implements LocationRepository {

    private final SpService service;

    @Inject
    public CloudLocationRepository(SpService service) {
        this.service = service;
    }

    public Observable<List<Location>> getLocationList(double latitude, double longitude) {
        return service.getLocationList(latitude, longitude).flatMap(new Func1<SpResult<List<Location>>,
                Observable<List<Location>>>() {
            @Override
            public Observable<List<Location>> call(SpResult<List<Location>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<Location> locations = listResponse.getData();
                    updateLocationsWithOfflineMarkers(locations);

                    return Observable.just(locations);
                } else {
                    return Observable.just(Collections.<Location>emptyList());
                }
            }
        });
    }

    private void updateLocationsWithOfflineMarkers(List<Location> locations) {
        Realm realm = Realm.getDefaultInstance();
        try {
            List<Location> storedItems = realm.copyFromRealm(realm.where(Location.class).findAll());

            for (int i = 0; i < storedItems.size(); i++) {
                int id = locations.indexOf(storedItems.get(i));
                if ( id != -1) {
                    locations.get(id).setOfflineAccess(true);
                }
            }
        } finally {
            realm.close();
        }
    }

    public Observable<List<Location>> getSetupLocationList() {
        return service.getSetupLocationList().flatMap(new Func1<SpResult<List<Location>>,
                Observable<List<Location>>>() {
            @Override
            public Observable<List<Location>> call(SpResult<List<Location>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<Location> locations = listResponse.getData();

                    return Observable.just(locations);
                } else {
                    return Observable.just(Collections.<Location>emptyList());
                }
            }
        });
    }

}
