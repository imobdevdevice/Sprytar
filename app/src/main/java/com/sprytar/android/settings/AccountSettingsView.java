package com.sprytar.android.settings;

import com.sprytar.android.presentation.MvpView;

public interface AccountSettingsView extends MvpView {

    void showError(String message);

    void closeDialog(boolean success);

    void showAccountData(String email);

    void showInvalidParameters();

}
