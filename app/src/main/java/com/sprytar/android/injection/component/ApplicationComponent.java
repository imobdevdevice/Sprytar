package com.sprytar.android.injection.component;


import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.RealmService;
import com.sprytar.android.injection.module.ApplicationModule;
import com.sprytar.android.injection.module.RxModule;
import com.sprytar.android.splash.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import rx.Scheduler;

import static com.sprytar.android.injection.module.RxModule.COMPUTATION;
import static com.sprytar.android.injection.module.RxModule.IO;
import static com.sprytar.android.injection.module.RxModule.MAIN;


@SuppressWarnings("unused")
@Singleton
@Component(modules = {ApplicationModule.class, RxModule.class})
public interface ApplicationComponent {

    SpSession spSession();

    Context context();

    @Named(IO)
    Scheduler ioScheduler();

    @Named(MAIN)
    Scheduler mainScheduler();

    @Named(COMPUTATION)
    Scheduler computationScheduler();

    DataRepository DataRepository();

    Realm Realm();

    RealmService RealmService();

    EventBus EventBus();

    GoogleApiClient GoogleApiClient();

    void inject(SplashActivity splashActivity);


}
