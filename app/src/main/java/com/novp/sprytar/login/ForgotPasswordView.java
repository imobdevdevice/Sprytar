package com.novp.sprytar.login;


import com.novp.sprytar.presentation.MvpView;

interface ForgotPasswordView extends MvpView {

    void showError(String message);

    void showSuccessDialog(String message);
}
