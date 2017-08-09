package com.novp.sprytar.family;

import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.events.FamilyMemberEvent;
import com.novp.sprytar.presentation.BaseView;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

interface FamilyMemberView extends BaseView<FamilyMember> {

    void showError(String message);

    void addNewMember(FamilyMember familyMember);

    void showPickUserUi();

    void closeUi();

    void showConfirmDeleteDialog(FamilyMemberEvent event);

    void setFinishMenuItemVisibility(boolean visible);

    void hideNoMembersView();

    void showNoMembersView();
}
