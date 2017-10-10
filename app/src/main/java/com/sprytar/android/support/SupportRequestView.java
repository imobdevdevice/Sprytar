package com.sprytar.android.support;


import com.sprytar.android.presentation.MvpView;

interface SupportRequestView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void showValidationError(String message);

    void showError(String message);

    void showResultUi(String email);
}
