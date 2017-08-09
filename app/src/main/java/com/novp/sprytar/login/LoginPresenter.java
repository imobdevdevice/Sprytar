package com.novp.sprytar.login;

import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
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


    public void onLoginClick(String email, String password) {
        getMvpView().showLoadingIndicator();
        loginInteractor.execute(new LoginInteractorSubscriber(), email, password);
    }

    private final class LoginInteractorSubscriber extends Subscriber<AuthUser> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            getMvpView().hideLoadingIndicator();
            String error = "";
            if (e instanceof HttpException) {
            } else {
                error = e.getLocalizedMessage();
            }
            getMvpView().showError(error);
        }

        @Override
        public void onNext(AuthUser authUser) {
            getMvpView().hideLoadingIndicator();
            getMvpView().showMainActivity();
       //     if (authUser.isAdmin()) {
       //        getMvpView().showMainActivity();
       //     } else {
       //         getMvpView().showPickUserUi();
       //     }
        }
    }

}

