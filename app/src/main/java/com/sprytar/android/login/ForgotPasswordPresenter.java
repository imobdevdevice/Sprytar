package com.sprytar.android.login;

import android.content.Context;

import com.sprytar.android.network.SpResult;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordView> {

    private final LoginService loginService;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    ForgotPasswordPresenter(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void attachView(ForgotPasswordView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void onResetButtonClick(String email, Context context) {

        if (Utils.isNetworkAvailable(context)) {
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(loginService
                    .resetPassword(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult>() {
                        @Override
                        public void call(SpResult response) {
                            if (response.isSuccess()) {
                                getMvpView().showSuccessDialog(response.getMessage());
                            } else {
                                getMvpView().showError("Registering error: " + response.getMessage());
                            }
                            getMvpView().hideLoadingIndicator();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().hideLoadingIndicator();
                            if (throwable instanceof SocketTimeoutException) {
                                getMvpView().showErrorDialog(true);
                            }
                            else {
                                getMvpView().showErrorDialog(false);
                            }
                        }
                    }));
        } else {
            getMvpView().showErrorDialog(true);
        }
    }

}
