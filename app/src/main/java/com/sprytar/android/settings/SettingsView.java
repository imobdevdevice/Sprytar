package com.sprytar.android.settings;

import com.sprytar.android.presentation.MvpView;

public interface SettingsView extends MvpView {

    void showError(String message);

    void showAccountSettingsUi();
}
