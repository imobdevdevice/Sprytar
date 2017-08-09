package com.novp.sprytar.splash;


import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

import rx.Subscriber;

public class SplashPresenter extends BasePresenter<SplashView> {

    private final SplashInteractor splashInteractor;
    private final SpSession spSession;

    @Inject
    SplashPresenter(SplashInteractor splashInteractor, SpSession spSession) {
        this.splashInteractor = splashInteractor;
        this.spSession = spSession;
    }

    @Override
    public void attachView(SplashView mvpView) {
        super.attachView(mvpView);
//        if (isSplashWorkFinished) {
//            getMvpView().showApplicationUi();
//        } else if (isSplashWorkError) {
//            getMvpView().showError(splashWorkError.getMessage());
//            getMvpView().showApplicationUi();
//        }
    }

    @Override
    public void detachView() {
        super.detachView();
        splashInteractor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        splashInteractor.unsubscribe();
    }

    public void onGetStartedClick() {
        if (spSession.isLoggedIn()) {
            getMvpView().showMainActivityUi();
        } else {
            getMvpView().showLoginUi();
        }
    }

    public void showIntroDialogIfNeeded() {
        if (spSession.showIntro()) {
            getMvpView().showIntroDialog();
            spSession.setShowIntroFalse();
        }
    }

}
