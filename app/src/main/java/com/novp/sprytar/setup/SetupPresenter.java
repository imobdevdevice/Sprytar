package com.novp.sprytar.setup;

import android.content.Context;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.location.LocationInteractor;
import com.novp.sprytar.location.LocationListInteractor;
import com.novp.sprytar.presentation.BasePresenter;
import com.novp.sprytar.profile.Repository;
import com.novp.sprytar.util.Utils;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SetupPresenter extends BasePresenter<SetupView>  {

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
