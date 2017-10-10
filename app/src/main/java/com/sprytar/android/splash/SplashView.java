package com.sprytar.android.splash;


import com.sprytar.android.presentation.MvpView;

interface SplashView extends MvpView {

    void showError(String message);

    void showMainActivityUi();

    void showLoginUi();

    void showIntroDialog();

}
