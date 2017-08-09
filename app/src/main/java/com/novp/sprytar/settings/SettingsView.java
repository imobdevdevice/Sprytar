package com.novp.sprytar.settings;

import com.novp.sprytar.presentation.MvpView;

public interface SettingsView extends MvpView {

    void showError(String message);

    void showAccountSettingsUi();
}
