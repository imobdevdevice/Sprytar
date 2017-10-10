package com.sprytar.android.setup;

import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;
import com.sprytar.android.location.CloudLocationRepository;

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
