package com.novp.sprytar.family;

import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.Interactor;
import com.novp.sprytar.injection.module.RxModule;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class AvatarListInteractor extends Interactor {

    private final FamilyMembersRepository repository;
    private final DummyRepository dummyRepository;

    @Inject
    public AvatarListInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                @Named(RxModule.MAIN) Scheduler mainScheduler,
                                FamilyMembersRepository repository, DummyRepository dummyRepository) {
        super(workScheduler, mainScheduler);
        this.repository = repository;
        this.dummyRepository = dummyRepository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        //return repository.getAvatarList();
        return dummyRepository.getAvatarList();
    }
}
