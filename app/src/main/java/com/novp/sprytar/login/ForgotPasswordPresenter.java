package com.novp.sprytar.login;

import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.presentation.BasePresenter;

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

    public void onResetButtonClick(String email) {
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
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));
    }

}
