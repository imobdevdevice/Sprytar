package com.novp.sprytar.login;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.novp.sprytar.data.DummyRepository;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.FamilyMember;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PickUserPresenter extends BasePresenter<PickUserView> {

    private ConfirmPasswordDialog.OnConfirmPasswordListener listener = new ConfirmPasswordDialog.OnConfirmPasswordListener() {
        @Override
        public void onDone(String password) {
            if(password.isEmpty()){
                getMvpView().showError("Password is empty.");
            }else if(!password.equals(spSession.getPassword())){
                getMvpView().showError("Please enter the correct password.");
            }else if(password.equals(spSession.getPassword())){
                getMvpView().hideConfirmPasswordDialog();
                spSession.updateSessionData(memberToChange);
                getMvpView().showMainActivity();
            }
        }

        @Override
        public void onCancel() {
            getMvpView().hideConfirmPasswordDialog();
        }
    };

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final PickUserInteractor interactor;
    private final DummyRepository dummyRepository;
    private final SpSession spSession;

    private ArrayMap<String, Uri> avatarMap;

    private FamilyMember memberToChange;

    @Inject
    PickUserPresenter(PickUserInteractor interactor, DummyRepository dummyRepository, SpSession
            spSession) {
        this.interactor = interactor;
        this.dummyRepository = dummyRepository;
        this.spSession = spSession;

        this.avatarMap = new ArrayMap<>();
    }

    @Override
    public void attachView(PickUserView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void loadFamilyMembers() {
        getMvpView().showLoadingIndicator();

        compositeSubscription.add(dummyRepository.getAvatarList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayMap<String, Uri>>() {
                    @Override
                    public void call(ArrayMap<String, Uri> map) {
                        if (isViewAttached()) {
                            avatarMap = map;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));


        interactor.execute(new PickUserInteractorSubscriber());
    }

    public void onMemberItemClicked(FamilyMember familyMember) {
        memberToChange = familyMember;
        getMvpView().displayConfirmPasswordDialog(listener);
    }

    private List<FamilyMember> prepareFamilyMembers(List<FamilyMember> familyMembers){
        for(FamilyMember member : familyMembers){
            if(member.getId() == spSession.getUserId()){
                member.setCurrentUser(true);
            }else {
                member.setCurrentUser(false);
            }
        }
        return familyMembers;
    }

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
            getMvpView().hideLoadingIndicator();
            getMvpView().showItems(prepareFamilyMembers(familyMembers), avatarMap);
        }
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
        }
    }

}
