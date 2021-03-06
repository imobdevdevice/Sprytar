package com.novp.sprytar.tour;

import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.game.CachedGameRepository;
import com.novp.sprytar.injection.module.RxModule;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class GuidedToursInteractor extends Interactor {

    private final CachedGameRepository repository;

    @Inject
    public GuidedToursInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                 @Named(RxModule.MAIN) Scheduler mainScheduler,
                                 CachedGameRepository repository) {
        super(workScheduler, mainScheduler);

        this.repository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        //return repository.getGameInfo(params[0]);
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
