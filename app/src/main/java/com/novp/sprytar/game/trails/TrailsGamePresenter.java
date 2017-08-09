package com.novp.sprytar.game.trails;

import com.novp.sprytar.R;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TrailsGamePresenter extends BasePresenter<TrailsGameView> {

    private final SpService spService;
    private final SpSession spSession;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private Trail trail;
    private SubTrail subTrail;
    private List<LocationBoundary> boundaries;
    private EarnedBadge badge;
    private int venueId;

    @Inject
    TrailsGamePresenter(SpService spService,SpSession spSession) {
        this.spService = spService;
        this.spSession = spSession;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    public void setTrailData(Trail trail, SubTrail subTrail, List<LocationBoundary> boundaries,int venueId) {
        this.trail = trail;
        this.subTrail = subTrail;
        this.boundaries = boundaries;
        this.venueId = venueId;
        getMvpView().setTrailTitle(trail.getTitle());
        getMvpView().setSubTrailData(subTrail.getName(),getPreparedInfo(),getWheelchairSupport());
        getMvpView().setFragment(boundaries,subTrail);
    }

    private int getWheelchairSupport() {
        if (subTrail.getWheelchairAccess() == 0) {
            return R.drawable.trails_no_wh;
        }

        return R.drawable.trails_wh;
    }

    private String getPreparedInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(subTrail.getDistance())
                .append(" meters")
                .append(" \u2022 ")
                .append(subTrail.getTypicalTime())
                .append("min")
                .append(" \u2022 ")
                .append(subTrail.getDifficulty())
                .append(" \u2022 ");

        return builder.toString();
    }

    public void onGameCompleted(){
        compositeSubscription.add(spService.saveTrailsResult(spSession.getUserId(),subTrail.getId(),subTrail.getTrailsPoints().size(),venueId,trail.getTrailsId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult response) {
                        if (!response.isSuccess()) {
                            // getMvpView().showError("Error: " + response.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                       // getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));


        getMvpView().finishTheGame(trail,subTrail,badge);
    }

    @Override
    public void onDestroyed() {
    }
}
