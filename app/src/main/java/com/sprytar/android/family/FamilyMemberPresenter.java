package com.sprytar.android.family;

import android.content.Context;

import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FamilyMemberPresenter extends BasePresenter<FamilyMemberView> {

    private final FamilyMemberInteractor interactor;
    private final SpService spService;
    private final Context context;
    private final SpSession spSession;
    private final EventBus bus;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private List<FamilyMember> memberList;

    FamilyMember newMember;

    @Inject
    FamilyMemberPresenter(Context context, FamilyMemberInteractor interactor, SpService spService,
                          SpSession spSession, EventBus bus) {
        this.interactor = interactor;
        this.spService = spService;
        this.context = context;
        this.spSession = spSession;
        this.bus = bus;

        memberList = new ArrayList<FamilyMember>();

    }

    @Override
    public void attachView(FamilyMemberView mvpView) {
        super.attachView(mvpView);
        bus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        bus.unregister(this);
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void onCreateOptionsMenu() {
        getMvpView().setFinishMenuItemVisibility(memberList.size() > 0);
    }

    public void getFamilyMembers() {
        getMvpView().showLoadingIndicator();
        interactor.execute(new PickUserInteractorSubscriber());
    }

    public void onMemberItemClicked(FamilyMember familyMember) {
        getMvpView().addNewMember(familyMember);
    }

    public void onAddMemberClick() {
        newMember = new FamilyMember(-1);
        getMvpView().addNewMember(newMember);
    }

    public void updateMemberList() {

        if (newMember != null) {
            memberList.add(newMember);
            newMember = null;
        }
        getMvpView().showItems(memberList);
    }

    public void deleteItem(final int position,Context context) {

        if(Utils.isNetworkAvailable(context)){
            FamilyMember member = memberList.get(position);

            compositeSubscription.add(spService
                    .removeAccount(member.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult>() {
                        @Override
                        public void call(SpResult response) {
                            if (response.isSuccess()) {
                                memberList.remove(position);
                                checkHasFamilyMembers();
                                getMvpView().showItems(memberList);
                            } else {
                                getMvpView().showError("Error: " + response.getMessage());
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof SocketTimeoutException) {
                                getMvpView().showErrorDialog(true);
                            }
                            else {
                                getMvpView().showErrorDialog(false);
                            }
                        }
                    }));
        }else{
            getMvpView().showErrorDialog(true);
        }

    }

    public void updateItem(final FamilyMember familyMember, Context context) {

        if(Utils.isNetworkAvailable(context)){
            compositeSubscription.add(spService
                    .addFamilyMember(familyMember.getId(), familyMember.getName(), familyMember.getBirthday(), familyMember.getAvatar())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<FamilyMember>>() {
                        @Override
                        public void call(SpResult<FamilyMember> response) {
                            if (response.isSuccess()) {
                                FamilyMember addedMember = response.getData();
                                familyMember.setId(addedMember.getId());
                            } else {
                                getMvpView().showError("Registering error: " + response.getMessage());

                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof SocketTimeoutException) {
                                getMvpView().showErrorDialog(true);
                            }
                            else {
                                getMvpView().showErrorDialog(false);
                            }
                        }
                    }));
        }else {
            getMvpView().showErrorDialog(true);
        }

    }

    public void onFinishClick(boolean signUp) {

        if (memberList.size() == 0) {
            getMvpView().showMessage(context.getString(R.string.no_member_warning));
            return;
        }

        if (signUp) {
            getMvpView().showPickUserUi();
        } else {
            getMvpView().closeUi();
        }

    }

   /* @Subscribe
    public void onEvent(FamilyMemberEvent event) {
        switch (event.getActionType()) {
            case FamilyMemberEvent.DELETE:
                if (event.isConfirmed()) {
                    deleteItem(event.getPosition());
                } else {
                    getMvpView().showConfirmDeleteDialog(event);
                }
                break;
            case FamilyMemberEvent.UPDATE:
                updateItem(event.getFamilyMember());
        }
    }*/

    private final class PickUserInteractorSubscriber extends Subscriber<List<FamilyMember>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            getMvpView().hideLoadingIndicator();
        }

        @Override
        public void onNext(List<FamilyMember> familyMembers) {
            memberList = familyMembers;
            getMvpView().hideLoadingIndicator();
            checkHasFamilyMembers();
            getMvpView().showItems(memberList);
        }
    }

    private void checkHasFamilyMembers(){
        if(memberList.size() > 0){
            getMvpView().hideNoMembersView();
        }else {
            getMvpView().showNoMembersView();
        }
    }

}
