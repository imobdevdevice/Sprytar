package com.novp.sprytar.login;

import com.novp.sprytar.BuildConfig;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class LoginModule {


    static final String LOGIN = "login_retrofit";

    @LoginScope
    @Provides
    @Named(LOGIN)
    Retrofit provideLoginRetrofit(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(LoginService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @LoginScope
    @Provides
    LoginService provideLoginService(@Named(LOGIN) Retrofit retrofit) {
        return retrofit.create(LoginService.class);
    }

    @Provides
    @LoginScope
    OkHttpClient provideOkHttpClient(@SuppressWarnings("UnusedParameters") HttpLoggingInterceptor
                                             httpLoggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(httpLoggingInterceptor);
        return builder.build();
    }

    @Provides
    @LoginScope
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }
}
