package com.novp.sprytar.injection.module;


import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.DataRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@SuppressWarnings("WeakerAccess")
@Module
public class RxModule {

    public static final String MAIN = "main";
    public static final String IO = "io";
    public static final String COMPUTATION = "computation";

    @Provides
    @Singleton
    @Named(MAIN)
    public Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @Named(IO)
    public Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @Named(COMPUTATION)
    public Scheduler provideComputationScheduler() {
        return Schedulers.computation();
    }

}
