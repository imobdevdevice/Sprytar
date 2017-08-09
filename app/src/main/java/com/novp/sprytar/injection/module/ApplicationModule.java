package com.novp.sprytar.injection.module;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.novp.sprytar.BuildConfig;
import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.RealmService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class ApplicationModule {

    private static EventBus eventBus;

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public DataRepository provideRepository(DummyRepository dummyRepository) {
        return dummyRepository;
    }

    @Provides
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    public RealmService provideRealmService(final Realm realm) {
        return new RealmService(realm);
    }

    @Provides
    @Singleton
    public static EventBus provideEventBus ()
    {
        if ( eventBus == null )
        {
            eventBus = EventBus.builder()
                    .logNoSubscriberMessages( BuildConfig.DEBUG )
                    .sendNoSubscriberEvent( BuildConfig.DEBUG )
                    .build();
        }
        return eventBus;
    }

    @Provides
    @Singleton
    public GoogleApiClient providesGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
    }

}
