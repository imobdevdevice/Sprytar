package com.sprytar.android.login;


import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

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
