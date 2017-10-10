package com.sprytar.android.injection.component;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.domain.DataRepository;
import com.sprytar.android.domain.RealmService;
import com.sprytar.android.injection.SessionScope;
import com.sprytar.android.injection.module.RetrofitModule;
import com.sprytar.android.main.MainActivity;
import com.sprytar.android.network.ErrorResult;
import com.sprytar.android.network.SpService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;

import dagger.Component;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import rx.Scheduler;

import static com.sprytar.android.injection.module.RxModule.COMPUTATION;
import static com.sprytar.android.injection.module.RxModule.IO;
import static com.sprytar.android.injection.module.RxModule.MAIN;

@SessionScope
@Component(dependencies = ApplicationComponent.class, modules = {RetrofitModule.class})
public interface SessionComponent {

    @Named(IO)
    Scheduler ioScheduler();

    @Named(MAIN)
    Scheduler mainScheduler();

    @Named(COMPUTATION)
    Scheduler computationScheduler();

    Converter<ResponseBody, ErrorResult> converter();

    SpService SpService();

    SpSession SpSession();

    Realm Realm();

    RealmService RealmService();

    EventBus EventBus();

    DataRepository DataRepository();

    Context context();

    GoogleApiClient GoogleApiClient();

    void inject(MainActivity mainActivity);

}


