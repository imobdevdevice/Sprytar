package com.sprytar.android.main;


import com.sprytar.android.presentation.MvpView;

interface MainActivityView extends MvpView {

    void showApplicationUi();

    void showError(String message);

    void showSplashUi();

    void showDisclaimerDialog();

}
