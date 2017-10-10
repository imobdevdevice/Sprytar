package com.sprytar.android.login;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.presentation.BaseView;

import java.util.List;

interface PickUserView extends BaseView<FamilyMember> {

    void showError(String message);

    void showMainActivity();

    void showItems(List<FamilyMember> items, ArrayMap<String, Uri> avatarMap);

    void displayConfirmPasswordDialog(ConfirmPasswordDialog.OnConfirmPasswordListener listener);

    void hideConfirmPasswordDialog();
}
