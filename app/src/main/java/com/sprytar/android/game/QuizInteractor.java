package com.sprytar.android.game;


import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class QuizInteractor extends Interactor {

    private final DataRepository dataRepository;

    @Inject
    public QuizInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                          @Named(RxModule.MAIN) Scheduler mainScheduler, DataRepository repository) {
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
