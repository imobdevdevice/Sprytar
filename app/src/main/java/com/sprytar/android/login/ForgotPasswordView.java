package com.sprytar.android.login;


import com.sprytar.android.presentation.MvpView;

interface ForgotPasswordView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void showSuccessDialog(String message);
}
