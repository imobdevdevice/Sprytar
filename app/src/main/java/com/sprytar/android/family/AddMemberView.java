package com.sprytar.android.family;

import android.net.Uri;

import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface AddMemberView extends MvpView {

    void showError(String message);

    void showAvatars(List<Uri> items, int selected);

    void showSelectedAvatar(int selected);

    void updateBirthday(String birthday);

    void closeDialog(boolean success);

    void showMemberInfo(String name, String birthday);
}
