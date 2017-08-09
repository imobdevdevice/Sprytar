package com.novp.sprytar.injection.component;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.domain.DataRepository;
import com.novp.sprytar.domain.RealmService;
import com.novp.sprytar.injection.SessionScope;
import com.novp.sprytar.injection.module.RetrofitModule;
import com.novp.sprytar.main.MainActivity;
import com.novp.sprytar.network.ErrorResult;
import com.novp.sprytar.network.SpService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;

import dagger.Component;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import rx.Scheduler;

import static com.novp.sprytar.injection.module.RxModule.COMPUTATION;
import static com.novp.sprytar.injection.module.RxModule.IO;
import static com.novp.sprytar.injection.module.RxModule.MAIN;

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


