package com.novp.sprytar.injection.component;


import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.RealmService;
import com.novp.sprytar.injection.module.ApplicationModule;
import com.novp.sprytar.injection.module.RxModule;
import com.novp.sprytar.splash.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import rx.Scheduler;

import static com.novp.sprytar.injection.module.RxModule.COMPUTATION;
import static com.novp.sprytar.injection.module.RxModule.IO;
import static com.novp.sprytar.injection.module.RxModule.MAIN;


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
