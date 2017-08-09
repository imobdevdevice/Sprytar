package com.novp.sprytar.settings;

import android.content.Context;

import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

public class SettingsPresenter extends BasePresenter<SettingsView>  {

    private final SettingsInteractor interactor;
    private final Context context;


    @Inject
    SettingsPresenter(SettingsInteractor interactor, Context context) {
        this.interactor = interactor;
        this.context = context;
    }

    @Override
    public void attachView(SettingsView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void onAccountSettingsClick() {
        getMvpView().showAccountSettingsUi();
    }

}
