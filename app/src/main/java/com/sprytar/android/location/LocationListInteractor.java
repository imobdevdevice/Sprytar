package com.sprytar.android.location;

import com.sprytar.android.data.DummyRepository;
import com.sprytar.android.domain.Interactor;
import com.sprytar.android.domain.RealmService;
import com.sprytar.android.injection.module.RxModule;

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
        int isSuperUser = Integer.valueOf(params[3]);

        if (isNetworkAvailable) {
            return dataRepository.getLocationList(currentLatitude, currentLongitude,isSuperUser);
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
