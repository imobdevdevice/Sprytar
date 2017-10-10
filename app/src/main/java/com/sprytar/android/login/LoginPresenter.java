package com.sprytar.android.login;

import android.content.Context;

import com.sprytar.android.data.model.AuthUser;
import com.sprytar.android.domain.LoginErrorException;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import rx.Subscriber;

public class LoginPresenter extends BasePresenter<LoginView> {

    private final LoginInteractor loginInteractor;

    @Inject
    LoginPresenter(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void attachView(LoginView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        loginInteractor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        loginInteractor.unsubscribe();
    }

    public void onSkipSingInClick() {
        getMvpView().showMainActivity();
    }


    public void onLoginClick(String email, String password,Context context) {
        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
            loginInteractor.execute(new LoginInteractorSubscriber(), email, password);
        }else{
            getMvpView().showErrorDialog(true);
        }

    }

    private final class LoginInteractorSubscriber extends Subscriber<AuthUser> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            getMvpView().hideLoadingIndicator();
            if (e instanceof SocketTimeoutException) {
                // show no internet dialog
                 getMvpView().showErrorDialog(true);
            }
            else  if(e instanceof LoginErrorException){
                // so simple message for invalid login credentials
                getMvpView().showError(e.getLocalizedMessage());
            }else if(e instanceof SocketTimeoutException){
                getMvpView().showErrorDialog(true);
            }else {
                getMvpView().showErrorDialog(false);
            }
        }

        @Override
        public void onNext(AuthUser authUser) {
            getMvpView().hideLoadingIndicator();
            getMvpView().showMainActivity();
        }
    }

}

