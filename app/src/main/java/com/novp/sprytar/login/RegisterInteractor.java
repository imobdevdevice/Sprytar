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

public class RegisterInteractor extends Interactor {

    private final DataRepository dataRepository;

    @Inject
    public RegisterInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                              @Named(RxModule.MAIN) Scheduler mainScheduler,
                              DataRepository repository) {
        super(workScheduler, mainScheduler);

        this.dataRepository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        //Simply wait 3 seconds
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(3000);
                return null;
            }
        });
    }

}
