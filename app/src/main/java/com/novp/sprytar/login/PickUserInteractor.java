package com.novp.sprytar.login;


import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.injection.module.ApplicationModule;
import com.novp.sprytar.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class PickUserInteractor extends Interactor {

    private final CloudLoginRepository dataRepository;

    @Inject
    public PickUserInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                              @Named(RxModule.MAIN) Scheduler mainScheduler,
                              CloudLoginRepository repository) {
        super(workScheduler, mainScheduler);

        this.dataRepository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        return dataRepository.getFamilyMembers();
    }
}
