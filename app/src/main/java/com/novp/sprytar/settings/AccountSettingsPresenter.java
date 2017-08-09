package com.novp.sprytar.settings;

import android.content.Context;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.AuthUser;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AccountSettingsPresenter extends BasePresenter<AccountSettingsView>  {

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

    public void onOkClick(final String email, String password) {

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

    }
}
