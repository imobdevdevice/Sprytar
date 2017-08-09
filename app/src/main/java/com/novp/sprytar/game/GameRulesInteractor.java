package com.novp.sprytar.game;


import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.injection.module.RxModule;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class GameRulesInteractor extends Interactor {

    private final DummyRepository repository;

    @Inject
    public GameRulesInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                               @Named(RxModule.MAIN) Scheduler mainScheduler,
                               DummyRepository repository) {
        super(workScheduler, mainScheduler);

        this.repository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        return repository.getGameRulesList();
    }

}
