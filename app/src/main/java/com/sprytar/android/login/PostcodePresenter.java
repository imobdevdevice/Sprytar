package com.sprytar.android.login;

import com.sprytar.android.presentation.BasePresenter;

import javax.inject.Inject;

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
