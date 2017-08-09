package com.novp.sprytar.login;

import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PostcodePresenter extends BasePresenter<PostcodeView> {


    @Inject
    PostcodePresenter() {
    }

    @Override
    public void attachView(PostcodeView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onDestroyed() {
    }

    public void onDoneClick() {

    }
}
