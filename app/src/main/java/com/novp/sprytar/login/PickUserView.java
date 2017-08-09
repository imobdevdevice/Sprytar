package com.novp.sprytar.login;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.presentation.BaseView;

import java.util.List;

interface PickUserView extends BaseView<FamilyMember> {

    void showError(String message);

    void showMainActivity();

    void showItems(List<FamilyMember> items, ArrayMap<String, Uri> avatarMap);

    void displayConfirmPasswordDialog(ConfirmPasswordDialog.OnConfirmPasswordListener listener);

    void hideConfirmPasswordDialog();
}
