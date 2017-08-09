package com.novp.sprytar.presentation;


public interface MvpPresenter<V extends MvpView> {
    void attachView(V mvpView);

    void detachView();

    void onDestroyed();
}
