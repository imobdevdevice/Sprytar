package com.sprytar.android.location;

import com.google.common.collect.ComparisonChain;
import com.sprytar.android.data.model.Location;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Subscriber;

class LocationListInteractorSubscriber extends Subscriber<List<Location>> {

    private LocationPresenter locationPresenter;

    private LocationListInteractorSubscriber.SubscriberCallback subscriberCallback;

    public LocationListInteractorSubscriber(LocationPresenter locationPresenter) {
        this.locationPresenter = locationPresenter;
        this.subscriberCallback = locationPresenter;
    }

    @Override
    public void onCompleted() {
        if (locationPresenter.isViewAttached()) {
            locationPresenter.getMvpView().hideLoadingIndicator();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (locationPresenter.isViewAttached()) {
            locationPresenter.getMvpView().hideLoadingIndicator();
            if(e instanceof SocketTimeoutException){
                locationPresenter.getMvpView().showErrorDialog(true);
            }else{
                locationPresenter.getMvpView().showErrorDialog(false);
            }
        }
    }

    @Override
    public void onNext(List<Location> locations) {
        Collections.sort(locations, new DistanceComparator());
        subscriberCallback.onItemsReceived(locations);
        if (locationPresenter.isViewAttached()) {
            locationPresenter.getMvpView().showItems(locations);
        }
    }

    public interface SubscriberCallback {
        void onItemsReceived(List<Location> items);
    }

    public class DistanceComparator implements Comparator<Location> {
        @Override
        public int compare(Location o1, Location o2) {
            return ComparisonChain.start().compare(o1.getDistance(), o2.getDistance()).result();
        }
    }
}
