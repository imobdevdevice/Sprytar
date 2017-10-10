package com.sprytar.android.settings;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.AuthUser;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AccountSettingsPresenter extends BasePresenter<AccountSettingsView> {

    private final Context context;
    private final SpService spService;
    private final SpSession spSession;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    AccountSettingsPresenter(Context context, SpService spService, SpSession spSession) {
        this.context = context;
        this.spService = spService;
        this.spSession = spSession;
    }

    @Override
    public void attachView(AccountSettingsView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void loadAccountData() {
        getMvpView().showAccountData(spSession.getEmail());
    }

    public void onOkClick(final String email, String password,Context context) {

        if(Utils.isNetworkAvailable(context)){
            compositeSubscription.add(spService
                    .updateAccount(email, password, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<AuthUser>>() {
                        @Override
                        public void call(SpResult<AuthUser> response) {
                            if (response.isSuccess()) {
                                spSession.setEmail(email);
                                getMvpView().closeDialog(true);
                            } else {
                                getMvpView().showError("Error: " + response.getMessage());
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().showError(throwable.getLocalizedMessage());
                        }
                    }));
        }else {
            getMvpView().showError("Please check your internet connection.");
        }


    }
}
