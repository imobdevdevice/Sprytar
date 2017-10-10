package com.sprytar.android.family;


import com.sprytar.android.domain.Interactor;
import com.sprytar.android.injection.module.RxModule;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class FamilyMemberInteractor extends Interactor {

    private final CloudFamilyRepository dataRepository;

    @Inject
    public FamilyMemberInteractor(@Named(RxModule.IO) Scheduler workScheduler,
                                  @Named(RxModule.MAIN) Scheduler mainScheduler,
                                  CloudFamilyRepository repository) {
        super(workScheduler, mainScheduler);

        this.dataRepository = repository;
    }

    @Override
    public Observable buildInteractorObservable(String... params) {
        return dataRepository.getFamilyMembers();
    }


}
