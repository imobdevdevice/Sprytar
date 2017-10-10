package com.sprytar.android.splash;


import com.sprytar.android.data.SpSession;
import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class SplashInteractor extends Interactor {

    @Inject
    public SplashInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                            @Named(RxModule.MAIN) Scheduler mainScheduler, SpSession spSession) {
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
