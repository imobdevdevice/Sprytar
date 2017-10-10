package com.sprytar.android.fitness;

import android.content.Context;

import com.sprytar.android.R;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.GameRule;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.events.SendNotificationEvent;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FitnessClassesPresenter extends BasePresenter<FitnessClassesView> {

    private final FitnessClassesInteractor interactor;
    private final SpService spService;
    private final SpSession spSession;
    private final Context context;
    private final EventBus bus;

    private VenueActivity venueActivity;
    private List<LocationBoundary> boundaries;
    private List<GameRule> rules;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    FitnessClassesPresenter(Context context, FitnessClassesInteractor interactor, SpService
            spService, SpSession spSession, EventBus bus) {
        this.interactor = interactor;
        this.spService = spService;
        this.spSession = spSession;
        this.context = context;
        this.bus = bus;
    }

    @Override
    public void attachView(FitnessClassesView mvpView) {
        super.attachView(mvpView);
        bus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();

        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        bus.unregister(this);
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
            getMvpView().showNotifyDialog();
        } else {
            getMvpView().showDialogMessage(context.getString(R.string.registration_required));
        }
    }

    private void sendNotification(String subject, String message) {
        if(Utils.isNetworkAvailable(context)) {
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(spService
                    .sendFitnessClassRequest(subject, message, venueActivity.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult>() {
                        @Override
                        public void call(SpResult result) {
                            if (result.isSuccess()) {
                                getMvpView().showDialogMessage(context.getString(R.string.request_sent_success));
                            } else {

                                getMvpView().showDialogMessage(result.getMessage());
                            }
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

    @Subscribe
    public void onEvent(SendNotificationEvent event) {
        sendNotification(event.getSubject(), event.getMessage());
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