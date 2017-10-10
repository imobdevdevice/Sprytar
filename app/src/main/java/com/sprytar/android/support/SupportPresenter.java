package com.sprytar.android.support;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.presentation.BasePresenter;

import javax.inject.Inject;

public class SupportPresenter extends BasePresenter<SupportView> {

    private final Context context;
    private final SpSession spSession;


    @Inject
    SupportPresenter(Context context, SpSession spSession) {
        this.context = context;
        this.spSession = spSession;
    }

    @Override
    public void attachView(SupportView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onDestroyed() {
    }

    public void initData() {

        getMvpView().setWidgetVisibility(spSession.isLoggedIn());

    }
}
