package com.novp.sprytar.main;


import com.novp.sprytar.presentation.MvpView;

interface MainActivityView extends MvpView {

    void showApplicationUi();

    void showError(String message);

    void showSplashUi();

    void showDisclaimerDialog();

}
