package com.sprytar.android.login;


import com.sprytar.android.presentation.MvpView;

interface LoginView extends MvpView {

    void showError(String message);

    void showErrorDialog(boolean hasNoInternet);

    void showInvalidParameters();

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showPickUserUi();

    void showMainActivity();

}
