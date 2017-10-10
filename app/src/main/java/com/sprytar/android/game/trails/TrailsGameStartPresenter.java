package com.sprytar.android.game.trails;

import android.content.Context;
import android.util.Log;

import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.Trail;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TrailsGameStartPresenter extends BasePresenter<TrailsGameStartView> {

    private static final String ERROR_SOMETHING_WRONG = "Something went wrong.Please try again later.";

    private final SpService spService;

    private VenueActivity venueActivity;
    private boolean insideBoundaries;
    private List<LocationBoundary> boundaries;
    private int venueId;

    private Trail trail;

    private boolean cameraPermissionsGranted;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    TrailsGameStartPresenter(SpService spService){
        this.spService = spService;
    }

    public void setVenueActivity(VenueActivity venueActivity, boolean
            insideBoundaries, List<LocationBoundary> boundaries, int venueId, Context context) {
        this.venueActivity = venueActivity;
        this.insideBoundaries = insideBoundaries;
        this.boundaries = boundaries;
        this.venueId = venueId;
        getMvpView().setVenueTitle(venueActivity.getName());
        getTrailsInfo(context);
    }

    @Override
    public void onDestroyed() {
    }

    public void getTrailsInfo(Context context){
        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(spService.getTrailsInfo(venueActivity.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<Trail>>() {
                        @Override
                        public void call(SpResult<Trail> response) {
                            if(response.isSuccess()){
                                trail = response.getData();
                                getMvpView().showItems(trail.getSubTrails());
                            }else {
                                getMvpView().showError(ERROR_SOMETHING_WRONG);
                                getMvpView().closeActivity();
                            }
                            getMvpView().hideLoadingIndicator();

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.v("test_tag", "error: " + throwable.getLocalizedMessage());
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

    public void onSubTrailClick(SubTrail subTrail){
        if (!cameraPermissionsGranted) {
            getMvpView().showDialogMessage("To proceed you have to grant camera permissions");
            return;
        }

        if (insideBoundaries) {
            getMvpView().startSubTrailsGame(trail,subTrail,boundaries,venueId);
        } else {
            getMvpView().showDistanceDialog();
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    void setCameraPermissionsGranted(boolean cameraPermissionsGranted) {
        this.cameraPermissionsGranted = cameraPermissionsGranted;
    }
}
