package com.novp.sprytar.splash;


import com.novp.sprytar.presentation.MvpView;

interface SplashView extends MvpView {

    void showError(String message);

    void showMainActivityUi();

    void showLoginUi();

    void showIntroDialog();

}
