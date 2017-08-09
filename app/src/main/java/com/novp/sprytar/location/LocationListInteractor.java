package com.novp.sprytar.location;

import android.provider.ContactsContract;

import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.domain.RealmService;
import com.novp.sprytar.injection.module.ApplicationModule;
import com.novp.sprytar.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class LocationListInteractor extends Interactor {

    private final CloudLocationRepository dataRepository;
    private final RealmService realmService;
    private final DummyRepository dummyRepository;

    @Inject
    public LocationListInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                  @Named(RxModule.MAIN) Scheduler mainScheduler,
                                  CloudLocationRepository repository, DummyRepository dummyRepository, RealmService realmService) {
        super(workScheduler, mainScheduler);
        this.dataRepository = repository;
        this.realmService = realmService;
        this.dummyRepository = dummyRepository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        boolean isNetworkAvailable = Boolean.valueOf(params[0]);
        double currentLatitude = Double.valueOf(params[1]);
        double currentLongitude = Double.valueOf(params[2]);
        if (isNetworkAvailable) {
            return dataRepository.getLocationList(currentLatitude, currentLongitude);
        } else {
            return realmService.getLocationList();
        }
        //return dummyRepository.getLocations();
    }

    @Override
    public void unsubscribe() {
        realmService.closeRealm();
        super.unsubscribe();
    }


}
