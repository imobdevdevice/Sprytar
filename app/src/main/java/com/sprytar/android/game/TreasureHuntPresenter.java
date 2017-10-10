package com.sprytar.android.game;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.GameRule;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class TreasureHuntPresenter extends BasePresenter<TreasureHuntView> {

    private final TreasureHuntInteractor treasureHuntInteractor;
    private final GameRulesInteractor gameRulesInteractor;
    private final SpService spService;
    private TreasureHuntMainInteractor treasureHuntMainInteractor;
    private VenueActivity venueActivity;
    private List<LocationBoundary> boundaries;
    private boolean insideBoundaries;
    private int venueId;
    private String locationName;
    private String locationImageUrl;
    private List<GameRule> rules;
    private SpSession spSession;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();


    @Inject
    TreasureHuntPresenter(TreasureHuntInteractor treasureHuntInteractor, TreasureHuntMainInteractor treasureHuntMainInteractor, GameRulesInteractor
            gameRulesInteractor, SpService spService, SpSession spSession) {
        this.treasureHuntInteractor = treasureHuntInteractor;
        this.treasureHuntMainInteractor = treasureHuntMainInteractor;
        this.gameRulesInteractor = gameRulesInteractor;
        this.spService = spService;
        this.spSession = spSession;

    }

    @Override
    public void attachView(TreasureHuntView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        treasureHuntInteractor.unsubscribe();

        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
        treasureHuntInteractor.unsubscribe();
    }

    public void setVenueActivity(VenueActivity venue, boolean insideBoundaries, int locationId, String locationName, String imageUrl, List<LocationBoundary> boundaries, Context context) {
        this.venueActivity = venue;
        this.boundaries = boundaries;
        this.insideBoundaries = insideBoundaries;
        this.venueId = locationId;
        this.locationName = locationName;
        this.locationImageUrl = imageUrl;

        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
            treasureHuntInteractor.execute(new PresenterSubscriber(), String.valueOf(venueActivity
                    .getId()));
        }else{
            getMvpView().showErrorDialog(true);
        }

    }

    private void updateShowVenueActivity(VenueActivity venueActivity) {
        this.venueActivity = venueActivity;
        getMvpView().showVenueActivity(venueActivity);
    }


    public void onViewRulesClick() {
        //getMvpView().showRules(venueActivity.getRules());
        // gameRulesInteractor.execute(new GameRulesSubscriber());
        getMvpView().showRules();
    }

    public void onStartGameClick() {
        spSession.setShowIntroTreasureFalse();
        if (insideBoundaries) {
            getMvpView().startQuiz(venueActivity, boundaries, venueId, locationName, locationImageUrl);
        } else {
            getMvpView().showDistanceDialog();
        }
    }

    public void showIntro() {
        if (spSession.showIntroTreasure()) {
            getMvpView().startDialog();
        } else {
            onStartGameClick();
        }
    }

    private class PresenterSubscriber extends Subscriber<VenueActivity> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            getMvpView().hideLoadingIndicator();
            if (e instanceof SocketTimeoutException) {
                getMvpView().showErrorDialog(true);
            }
            else {
                getMvpView().showErrorDialog(false);
            }

        }

        @Override
        public void onNext(VenueActivity venueActivity) {
            updateShowVenueActivity(venueActivity);
            getMvpView().hideLoadingIndicator();
        }
    }


    /**
     private class GameRulesSubscriber extends Subscriber<List<GameRule>> {
    @Override public void onCompleted() {

    }

    @Override public void onError(Throwable e) {

    }

    @Override public void onNext(List<GameRule> gameRules) {
    getMvpView().showRules(gameRules);
    }
    }
     */
}