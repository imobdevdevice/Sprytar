package com.novp.sprytar.support;


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

public class FaqActivityInteractor extends Interactor {

    private final DataRepository dataRepository;
    private final RealmService realmService;

    @Inject
    public FaqActivityInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                 @Named(RxModule.MAIN) Scheduler mainScheduler,
                                 DataRepository repository, RealmService realmService) {
        super(workScheduler, mainScheduler);

        this.dataRepository = repository;
        this.realmService = realmService;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        return realmService.getFaqList();
        //return dataRepository.getFaqData();
    }

    @Override
    public void unsubscribe() {
        realmService.closeRealm();
        super.unsubscribe();
    }
}
