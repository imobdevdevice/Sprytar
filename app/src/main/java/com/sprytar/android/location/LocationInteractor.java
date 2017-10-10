package com.sprytar.android.location;

import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class LocationInteractor extends Interactor {

    @Inject
    public LocationInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                  @Named(RxModule.MAIN) Scheduler mainScheduler) {
        super(workScheduler, mainScheduler);
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
