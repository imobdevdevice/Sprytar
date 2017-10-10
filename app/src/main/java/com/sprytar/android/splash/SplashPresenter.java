package com.sprytar.android.splash;


import com.sprytar.android.data.SpSession;
import com.sprytar.android.presentation.BasePresenter;

import javax.inject.Inject;

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
