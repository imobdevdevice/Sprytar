package com.sprytar.android.injection.module;

import android.util.Base64;

import com.sprytar.android.BuildConfig;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.injection.SessionScope;
import com.sprytar.android.network.AuthenticationInterceptor;
import com.sprytar.android.network.ErrorResult;
import com.sprytar.android.network.HeaderInterceptor;
import com.sprytar.android.network.SpService;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    public static final String SESSION = "session_retrofit";

    private final int TIMEOUT_DURATION =15;

    @Provides
    @SessionScope
    SpService provideService(@Named(SESSION) Retrofit retrofit) {
        return retrofit.create(SpService.class);
    }

    @Provides
    @Named(SESSION)
    @SessionScope
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(SpService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @SessionScope
    OkHttpClient provideOkHttpClient(AuthenticationInterceptor authenticationInterceptor,
                                     @SuppressWarnings("UnusedParameters") HttpLoggingInterceptor
                                             httpLoggingInterceptor,
                                     @SuppressWarnings("UnusedParameters") HeaderInterceptor headerInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS);
        builder.addInterceptor(headerInterceptor);
     //   builder.addInterceptor(authenticationInterceptor);
        builder.addInterceptor(httpLoggingInterceptor);
        return builder.build();
    }


    @Provides
    @SessionScope
    HeaderInterceptor provideHeaderInterceptor(SpSession session) {
        return new HeaderInterceptor(session.getToken());
    }

    @Provides
    @SessionScope
    AuthenticationInterceptor provideAuthenticationInterceptor(SpSession session) {
        String credentials = session.getEmail() + ":" + session.getPassword();
        String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return new AuthenticationInterceptor(basic);
    }

    @Provides
    @SessionScope
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }

    @Provides
    @SessionScope
    Converter<ResponseBody, ErrorResult> provideErrorConverter(@Named(SESSION) Retrofit retrofit) {
        return retrofit.responseBodyConverter(ErrorResult.class, new Annotation[0]);
    }



}
