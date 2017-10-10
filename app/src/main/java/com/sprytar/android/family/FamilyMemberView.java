package com.sprytar.android.family;

import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.events.FamilyMemberEvent;
import com.sprytar.android.presentation.BaseView;

interface FamilyMemberView extends BaseView<FamilyMember> {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void addNewMember(FamilyMember familyMember);

    void showPickUserUi();

    void closeUi();

    void showConfirmDeleteDialog(FamilyMemberEvent event);

    void setFinishMenuItemVisibility(boolean visible);

    void hideNoMembersView();

    void showNoMembersView();
}
