package com.sprytar.android.support;


import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.Interactor;
import com.sprytar.android.domain.RealmService;
import com.sprytar.android.injection.module.RxModule;

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
