package com.sprytar.android.game.quiz;

import android.content.Context;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.Quiz;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;
import com.sprytar.android.util.Utils;

import java.net.SocketTimeoutException;

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
                                 Location location, Context context) {
        this.venueActivity = venueActivity;
        this.insideBoundaries = insideBoundaries;
        this.location = location;
        this.venueId = location.getId();

        getQuizDetails(context);
        getMvpView().setVenueTitle(venueActivity.getName());
    }

    private void getQuizDetails(Context context) {
        if(Utils.isNetworkAvailable(context)){
            getMvpView().showLoadingIndicator();
            compositeSubscription.add(spService.getQuizDetails(venueActivity.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((SpResult<Quiz> response) -> {
                        getMvpView().hideLoadingIndicator();
                        quiz = response.getData();
                    }, (Throwable throwable) -> {
                        getMvpView().hideLoadingIndicator();
                        if (throwable instanceof SocketTimeoutException) {
                            getMvpView().showErrorDialog(true);
                        }
                        else {
                            getMvpView().showErrorDialog(false);
                        }
                    }));
        }else{
            getMvpView().showErrorDialog(true);
        }

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
