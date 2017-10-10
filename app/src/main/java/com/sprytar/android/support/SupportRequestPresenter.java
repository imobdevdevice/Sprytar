package com.sprytar.android.support;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;

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

    public void sendRequest(String text, Context context) {

        if (text.isEmpty()) {
            getMvpView().showValidationError("Empty request");
            return;
        }
        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
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
                            getMvpView().hideLoadingIndicator();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            getMvpView().hideLoadingIndicator();
                            if (throwable instanceof SocketTimeoutException) {
                                getMvpView().showErrorDialog(true);
                            }
                            else {
                                getMvpView().showErrorDialog(false);
                            }
                        }
                    }));
        }else{
            getMvpView().showErrorDialog(true);
        }



    }
}
