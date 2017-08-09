package com.novp.sprytar.setup;

import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.domain.RealmService;
import com.novp.sprytar.injection.module.RxModule;
import com.novp.sprytar.location.CloudLocationRepository;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class LocationListSetupInteractor extends Interactor {

    private final CloudLocationRepository dataRepository;

    @Inject
    public LocationListSetupInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                       @Named(RxModule.MAIN) Scheduler mainScheduler,
                                       CloudLocationRepository repository) {
        super(workScheduler, mainScheduler);
        this.dataRepository = repository;

    }

    @Override
    public Observable buildInteractorObservable(String... params) {
            return dataRepository.getSetupLocationList();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }


}
