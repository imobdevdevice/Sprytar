package com.novp.sprytar.support;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SupportRequestPresenter extends BasePresenter<SupportRequestView> {

    private final SpSession spSession;
    private final SpService spService;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    SupportRequestPresenter(SpSession spSession, SpService spService){
        this.spService= spService;
        this.spSession= spSession;
    }

    @Override
    public void attachView(SupportRequestView mvpView) {
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

    public void sendRequest(String text) {

        if (text.isEmpty()) {
            getMvpView().showError("Empty request");
            return;
        }

        compositeSubscription.add(spService
                .sendSupportRequest(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult response) {
                        if (response.isSuccess()) {
                            getMvpView().showResultUi(spSession.getEmail());
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
