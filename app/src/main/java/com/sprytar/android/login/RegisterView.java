package com.sprytar.android.login;


import com.sprytar.android.presentation.MvpView;

import java.util.List;

interface RegisterView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showError(String message);

    void showErrorDialog(boolean hasNoInternet);

    void showDialogMessage(String message);

    void showMainActivity();

    void showInvalidParameters();

    void showFamilyMemberUi();

    void showAddressesFound(List<String> addresses);

}
