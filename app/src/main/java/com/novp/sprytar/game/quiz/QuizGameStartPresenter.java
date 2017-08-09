package com.novp.sprytar.game.quiz;

import android.util.Log;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Quiz;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class QuizGameStartPresenter extends BasePresenter<QuizGameStartView> {

    private final SpService spService;

    private VenueActivity venueActivity;
    private boolean insideBoundaries;
    private int venueId;
    private Quiz quiz;

    private Location location;

    private boolean cameraPermissionsGranted;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    QuizGameStartPresenter(SpService spService) {
        this.spService = spService;
    }

    @Override
    public void attachView(QuizGameStartView mvpView) {
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

    public void setVenueActivity(VenueActivity venueActivity, boolean insideBoundaries,
                                 Location location) {
        this.venueActivity = venueActivity;
        this.insideBoundaries = insideBoundaries;
        this.location = location;
        this.venueId = location.getId();

        getQuizDetails();
        getMvpView().setVenueTitle(venueActivity.getName());
    }

    private void getQuizDetails() {
        compositeSubscription.add(spService.getQuizDetails(venueActivity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((SpResult<Quiz> response) -> {
                    quiz = response.getData();
                }, (Throwable throwable) -> {
                    Log.v("test_tag", "error: " + throwable.getLocalizedMessage());
                }));
    }

    public void onViewRulesClick() {
        getMvpView().showRules();
    }

    public void onStartGameClick() {
        if (!cameraPermissionsGranted) {
            getMvpView().showDialogMessage("To proceed you have to grant camera permissions");
            return;
        }
        if (insideBoundaries) {
            getMvpView().startQuiz(venueActivity, venueId,location, quiz);
        } else {
            getMvpView().showDistanceDialog();
        }
    }

    void setCameraPermissionsGranted(boolean cameraPermissionsGranted) {
        this.cameraPermissionsGranted = cameraPermissionsGranted;
    }
}
