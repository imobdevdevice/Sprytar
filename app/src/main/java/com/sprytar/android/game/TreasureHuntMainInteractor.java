package com.sprytar.android.game;

import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class TreasureHuntMainInteractor extends Interactor {

    private final CachedGameRepository repository;

    @Inject
    public TreasureHuntMainInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                      @Named(RxModule.MAIN) Scheduler mainScheduler,
                                      CachedGameRepository repository) {
        super(workScheduler, mainScheduler);
        this.repository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        return repository.getGameRules();
//        //Simply wait 3 seconds
//        return Observable.fromCallable(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                Thread.sleep(3000);
//                return null;
//            }
//        });
    }

}
