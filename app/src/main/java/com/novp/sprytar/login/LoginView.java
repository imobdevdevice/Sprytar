package com.novp.sprytar.login;


import com.novp.sprytar.presentation.MvpView;

interface LoginView extends MvpView {

    void showError(String message);

    void showInvalidParameters();

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showPickUserUi();

    void showMainActivity();

}
