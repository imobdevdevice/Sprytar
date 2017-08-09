package com.novp.sprytar.domain;

import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.data.model.realm.VisitedLocation;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class RealmService {

    private final Realm realm;

    public RealmService(Realm realm) {
        this.realm = realm;
    }

    public void closeRealm() {
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    public Observable<List<Faq>> getFaqList() {
       List<Faq> result = realm.where(Faq.class).findAll();
       return Observable.just(result);
    }

    public Observable<List<Location>> getLocationList() {
        List<Location> result = realm.copyFromRealm(realm.where(Location.class).findAll());
        return Observable.just(result);
    }

    public Location getLocation(final int locationId) {
        return realm.where(Location.class).equalTo("id", locationId).findFirst();

    }

    public boolean hasLocation(final int locationId) {
        return (realm.where(Location.class).equalTo("id", locationId).count() > 0);
    }

    public void addReplaceLocation(final Location location, final OnTransactionCallback
            onTransactionCallback) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(location);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmSuccess();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmError(error);
                }
            }
        });
    }

    public void removeLocation(final int locationId, final OnTransactionCallback
            onTransactionCallback) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Location.class).equalTo("id", locationId).findAll()
                        .deleteFirstFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmSuccess();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmError(error);
                }
            }
        });
    }

    public void addReplaceVenueActivity(final VenueActivity venueActivity) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(venueActivity);
            }
        });
    }

    public void addReplaceVenueActivityList(final List<VenueActivity> venueActivity, final OnTransactionCallback
            onTransactionCallback) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(venueActivity);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmSuccess();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (onTransactionCallback != null) {
                    onTransactionCallback.onRealmError(error);
                }
            }
        });
    }

    public boolean todayLocationUpdated(int locationId, long todayMillis) {
        return realm.where(VisitedLocation.class).equalTo("locationId", locationId).equalTo("lastUpdate",
                todayMillis).findFirst() != null;
    }

    public void addReplaceVisitedLocation(final VisitedLocation visitedLocation) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(visitedLocation);
            }
        });
    }

    public interface OnTransactionCallback {
        void onRealmSuccess();
        void onRealmError(final Throwable e);
    }

}
