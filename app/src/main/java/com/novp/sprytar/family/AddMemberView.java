package com.novp.sprytar.family;

import android.net.Uri;
import android.provider.ContactsContract;

import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface AddMemberView extends MvpView {

    void showError(String message);

    void showAvatars(List<Uri> items, int selected);

    void showSelectedAvatar(int selected);

    void updateBirthday(String birthday);

    void closeDialog(boolean success);

    void showMemberInfo(String name, String birthday);
}
