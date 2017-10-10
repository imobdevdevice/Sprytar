package com.sprytar.android.family;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.events.FamilyMemberEvent;
import com.sprytar.android.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import rx.Subscriber;

public class AddMemberPresenter extends BasePresenter<AddMemberView> {

    private final AddMemberInteractor interactor;
    private final AvatarListInteractor avatarListInteractor;
    private final Context context;
    private final EventBus bus;

    private ArrayMap<String, Uri> avatarMap;
    private String avatarName;

    private int selectedAvatar = 0;

    private FamilyMember familyMember;

    @Inject
    AddMemberPresenter(AddMemberInteractor interactor, AvatarListInteractor avatarListInteractor,
                       Context context, EventBus eventBus) {
        this.interactor = interactor;
        this.avatarListInteractor = avatarListInteractor;
        this.context = context;
        this.bus = eventBus;
    }

    @Override
    public void attachView(AddMemberView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
        avatarListInteractor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;

        if (familyMember.getId() != -1) {
            this.avatarName = familyMember.getAvatar();
            getMvpView().showMemberInfo(familyMember.getName(), DateTimeFormat.longDate().withLocale
                    (Locale.getDefault()).print(familyMember.getBirthday()*1000));
        }

        avatarListInteractor.execute(new AvatarListSubscriber());

    }

    public void updateBirthdayData(int year, int monthOfYear, int dayOfMonth) {
        long newBirthdayMillis = new DateTime(familyMember.getBirthday() * 1000)
                .withYear(year)
                .withMonthOfYear(monthOfYear)
                .withDayOfMonth(dayOfMonth).getMillis();
        familyMember.setBirthday(newBirthdayMillis / 1000);

        getMvpView().updateBirthday(DateTimeFormat.longDate().withLocale(Locale.getDefault()).print(newBirthdayMillis));
    }

    public void onAddButtonClick(String nickname) {

        familyMember.setName(nickname);
        familyMember.setAvatar(avatarMap.keyAt(selectedAvatar));
        bus.post(new FamilyMemberEvent.Builder()
                .setActionType(FamilyMemberEvent.UPDATE)
                .setFamilyMember(familyMember)
                .build());
        getMvpView().closeDialog(true);
    }

    public void onAvatarClick(int position) {
        selectedAvatar = position;
        getMvpView().showSelectedAvatar(position);
    }

    private class AvatarListSubscriber extends Subscriber<ArrayMap<String, Uri>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(ArrayMap<String, Uri>  map) {
            avatarMap = map;
            int position = 0;
            if (avatarName != null) {
                position = avatarMap.indexOfKey(avatarName);
                selectedAvatar = position;
            }
            getMvpView().showAvatars(new ArrayList<Uri>(avatarMap.values()), position);
        }
    }
}
