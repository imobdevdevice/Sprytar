package com.sprytar.android.injection.module;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.sprytar.android.BuildConfig;
import com.sprytar.android.data.DummyRepository;
import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.RealmService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

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
