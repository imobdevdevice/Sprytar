package com.sprytar.android.setup;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class SetupPresenter extends BasePresenter<SetupView> {

    private final Context context;
    private final SpSession spSession;
    private final LocationListSetupInteractor locationListInteractor;

    private List<Location> locations;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    SetupPresenter(Context context, SpSession spSession, LocationListSetupInteractor locationListInteractor) {
        this.context = context;
        this.spSession = spSession;
        this.locationListInteractor = locationListInteractor;

    }

    @Override
    public void attachView(SetupView mvpView) {
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

    public void loadData() {

        if (!isViewAttached()) {
            return;
        }

        getMvpView().showLoadingIndicator();
        locationListInteractor.execute(new LocationSubscriber());

    }

    public void showLocationsUi() {
        getMvpView().showItems(locations);
    }

    private class LocationSubscriber extends Subscriber<List<Location>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Location> items) {
            locations = items;
            showLocationsUi();
        }
    }
}
