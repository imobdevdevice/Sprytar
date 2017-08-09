package com.novp.sprytar.game.trails;

import android.util.Log;

import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TrailsGameStartPresenter extends BasePresenter<TrailsGameStartView>{

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
            insideBoundaries, List<LocationBoundary> boundaries, int venueId) {
        this.venueActivity = venueActivity;
        this.insideBoundaries = insideBoundaries;
        this.boundaries = boundaries;
        this.venueId = venueId;

        getMvpView().setVenueTitle(venueActivity.getName());
        getTrailsInfo();
    }

    @Override
    public void onDestroyed() {
    }

    private void getTrailsInfo(){
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

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v("test_tag", "error: " + throwable.getLocalizedMessage());
                    }
                }));
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
