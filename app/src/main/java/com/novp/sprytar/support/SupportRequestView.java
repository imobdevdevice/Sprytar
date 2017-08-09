package com.novp.sprytar.support;


import com.novp.sprytar.presentation.MvpView;

interface SupportRequestView extends MvpView {

    void showError(String message);

    void showResultUi(String email);
}
