package com.novp.sprytar.login;

import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.network.SpLoginResult;
import com.novp.sprytar.network.SpResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface LoginService {

    String ENDPOINT = "https://cpanel.sprytar.com/api/";

    @FormUrlEncoded
    @POST("login")
    Observable<SpResult<AuthUser>> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Observable<SpLoginResult> register(@Field("email") String email, @Field("password") String
            password, @Field("password_confirmation") String confirmPassword,
                                       @Field("postcode") String postcode,
                                       @Field("date_of_birth") String dateOfBirth,
                                       @Field("gender") String gender);

    @FormUrlEncoded
    @POST("password/email")
    Observable<SpResult> resetPassword(@Field("email") String email);

}
