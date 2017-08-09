package com.novp.sprytar.login;


import com.novp.sprytar.presentation.MvpView;

import java.util.List;

interface RegisterView extends MvpView {

    void showError(String message);

    void showDialogMessage(String message);

    void showMainActivity();

    void showInvalidParameters();

    void showFamilyMemberUi();

    void showAddressesFound(List<String> addresses);

}
