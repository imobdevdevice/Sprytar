package com.sprytar.android.tour;

import android.content.Context;

import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.GameRule;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GuidedToursPresenter extends BasePresenter<GuidedToursView> {

    private final GuidedToursInteractor interactor;
    private final SpService spService;
    private final SpSession spSession;
    private final Context context;

    private VenueActivity venueActivity;
    private List<LocationBoundary> boundaries;
    private List<GameRule> rules;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    GuidedToursPresenter(Context context, GuidedToursInteractor interactor, SpService spService, SpSession spSession) {
        this.interactor = interactor;
        this.spService = spService;
        this.spSession = spSession;
        this.context = context;
    }

    @Override
    public void attachView(GuidedToursView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();

        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void setVenueActivity(VenueActivity venue, List<LocationBoundary> boundaries) {
        this.venueActivity = venue;
        this.boundaries = boundaries;

        //        compositeSubscription.add(spService
        //                .getGameInfo(String.valueOf(venueActivity.getId()))
        //                .subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Action1<SpResult<VenueActivity>>() {
        //                    @Override
        //                    public void call(SpResult<VenueActivity> result) {
        //                        venueActivity = result.getData();
        //                        getMvpView().showVenueActivity(venueActivity);
        //                    }
        //                }, new Action1<Throwable>() {
        //                    @Override
        //                    public void call(Throwable throwable) {
        //                        getMvpView().showError(throwable.getLocalizedMessage());
        //                    }
        //                }));

        //interactor.execute(new PresenterSubscriber(), String.valueOf(venueActivity.getId()));
    }

    private void updateShowVenueActivity(VenueActivity venueActivity) {
        this.venueActivity = venueActivity;
        getMvpView().showVenueActivity(venueActivity);
    }

    public void onSendNotificationClick() {
        if (spSession.isLoggedIn()) {
            sendNotification();
        } else {
            getMvpView().showDialogMessage(context.getString(R.string.registration_required));
        }
    }



    private void sendNotification() {
        if(Utils.isNetworkAvailable(context)) {
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(spService
                    .sendGuidedTourRequest(venueActivity.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult>() {
                        @Override
                        public void call(SpResult result) {
                            if (result.isSuccess()) {
                                getMvpView().showSendSuccessDialog();
                            } else {
                                getMvpView().showDialogMessage(result.getMessage());
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if(throwable instanceof SocketTimeoutException){
                                getMvpView().showErrorDialog(true);
                            }else{
                                getMvpView().showErrorDialog(false);
                            }
                            getMvpView().hideLoadingIndicator();
                        }

                    }));
        }


    }

    private class PresenterSubscriber extends Subscriber<VenueActivity> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(VenueActivity venueActivity) {
            updateShowVenueActivity(venueActivity);
        }
    }

}