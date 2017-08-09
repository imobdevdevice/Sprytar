package com.novp.sprytar.settings;

import android.net.Uri;

import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface AccountSettingsView extends MvpView {

    void showError(String message);

    void closeDialog(boolean success);

    void showAccountData(String email);

    void showInvalidParameters();

}
