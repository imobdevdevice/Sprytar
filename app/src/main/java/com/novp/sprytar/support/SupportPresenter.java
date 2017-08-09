package com.novp.sprytar.support;

import android.content.Context;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

public class SupportPresenter extends BasePresenter<SupportView>  {

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
