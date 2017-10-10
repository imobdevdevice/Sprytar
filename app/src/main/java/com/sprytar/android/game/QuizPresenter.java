package com.sprytar.android.game;

import android.content.Context;

import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.PlayedGame;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.events.SendQuestionResultEvent;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class QuizPresenter extends BasePresenter<QuizView> {

    private final QuizInteractor quizInteractor;
    private final Context context;
    private final SpSession spSession;
    private final SpService spService;
    private final EventBus bus;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private VenueActivity venueActivity;
    private int venueId;
    private Question currentQuestion;
    private List<Question> questions;
    private List<LocationBoundary> boundaries;
    private boolean isLastQuestion = false;
    private String locationName;
    private String locationImageUrl;
    private EarnedBadge badge;

    @Inject
    QuizPresenter(QuizInteractor quizInteractor, Context context, SpSession spSession, SpService
            spService, EventBus bus) {
        this.quizInteractor = quizInteractor;
        this.context = context;
        this.spSession = spSession;
        this.spService = spService;
        this.bus = bus;
    }

    @Override
    public void attachView(QuizView mvpView) {
        super.attachView(mvpView);
        bus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        bus.unregister(this);
        quizInteractor.unsubscribe();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
        quizInteractor.unsubscribe();
    }

    public void setVenueActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, String locationName, String locationImageUrl) {
        this.venueActivity = venueActivity;
        this.venueId = venueId;
        this.locationName = locationName;
        this.locationImageUrl = locationImageUrl;

        //this.questions = venueActivity.getQuestions();
        getQuestions();

        this.currentQuestion = questions.get(0);
        this.boundaries = boundaries;

        if (currentQuestion.isImageRecognitionQuestion()) {
            getMvpView().showImageRecognitionMapActivity(venueActivity, boundaries,
                    currentQuestion.getLatLng(), currentQuestion);
        } else if (currentQuestion.isPhotoHuntQuestion()) {
            getMvpView().showPhotoHuntActivity(venueActivity, boundaries, venueId,
                    currentQuestion.getLatLng(), currentQuestion);
        } else {
            getMvpView().showVenueActivity(venueActivity, currentQuestion);
            getMvpView().showFragments(boundaries, currentQuestion.getLatLng(), currentQuestion
                    .getLatitude(), currentQuestion.getLongitude(),currentQuestion.getQuestionDistance());
        }
    }

    public void showAnswerImageActivity(){
        getMvpView().showAnswerImageActivity(currentQuestion);
    }

    public void onShowHintClick() {
        String hint = currentQuestion.getHint();
        if (hint != null) {
            getMvpView().showHint(hint);
        }
    }

    public Question getCurrentQuestion(){
        return currentQuestion;
    }

    public boolean isLastQuestion(){
        return isLastQuestion;
    }

    public  List<LocationBoundary> getLocationBoundries(){
        return boundaries;
    }

    private void getQuestions() {
        this.questions = venueActivity.getQuestions();
    }

    public void nextQuestion() {
            sendQuestionResult(currentQuestion.getGlobalId(), currentQuestion.getId(), currentQuestion.isAnsweredCorrectly());
        if (currentQuestion.getId() + 1 == questions.size()) {
            currentQuestion.setAnswered(true);
            onFinishGame();
        } else {
            currentQuestion.setAnswered(true);
            currentQuestion = questions.get(currentQuestion.getId() + 1);

            if (currentQuestion.getId() + 1 == questions.size()) {
                isLastQuestion = true;
            }

//            if (currentQuestion.isImageRecognitionQuestion()) {
//                getMvpView().showImageQuestion(venueActivity, currentQuestion);
//            } else
            if (currentQuestion.isPhotoHuntQuestion()) {
                getMvpView().showPhotoHuntActivity(venueActivity, boundaries, venueId,
                        currentQuestion.getLatLng(), currentQuestion);
            } else {
                getMvpView().showVenueActivity(venueActivity, currentQuestion);
                getMvpView().showFragments(boundaries, currentQuestion.getLatLng(), currentQuestion
                        .getLatitude(), currentQuestion.getLongitude(),currentQuestion.getQuestionDistance());

            }
        }
    }

    private void onFinishGame() {
        if (spSession.isLoggedIn()) {
            compositeSubscription.add(sendEarnedBadgeFunction()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<EarnedBadge>>() {
                        @Override
                        public void call(List<EarnedBadge> earnedBadge) {
                            badge = earnedBadge.get(0);
                            sendGamePlayed();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //   getMvpView().showError(throwable.getLocalizedMessage());

                        }
                    }));

        } else {
            getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, 0, 0);
        }
    }
    private Observable<List<EarnedBadge>> sendEarnedBadgeFunction() {
        return spService.sendEarnedBadge(spSession.getUserId(), venueActivity.getGameBadgeId(), venueId).flatMap(new Func1<SpResult<List<EarnedBadge>>,
                Observable<List<EarnedBadge>>>() {
            @Override
            public Observable<List<EarnedBadge>> call(SpResult<List<EarnedBadge>> listResponse) {
                if (listResponse.isSuccess()) {
                    List<EarnedBadge> earnedBadges = listResponse.getData();
                    return Observable.just(earnedBadges);
                } else {
                    return Observable.just(Collections.<EarnedBadge>emptyList());
                }
            }
        });
    }

    private void sendGamePlayed() {
        compositeSubscription.add(spService.sendPlayedGame(spSession.getUserId(), venueId, venueActivity.getId(), venueActivity.getGameTypeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<PlayedGame>>() {
                    @Override
                    public void call(SpResult<PlayedGame> response) {
                        try {
                            int correctAnswers = response.getData().correctAnswers;
                            getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, correctAnswers, questions.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, 0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }

    public void onStartAnswerClick() {
        if (currentQuestion.getId() + 1 == questions.size() && currentQuestion.isAnswered()) {
            getMvpView().showGameFinishedActivity(badge, locationName,locationImageUrl, 0, 0);
        } else {
            if (currentQuestion.isImageRecognitionQuestion()) {
                getMvpView().showImageRecognitionMapActivity(venueActivity, boundaries,
                        currentQuestion.getLatLng(), currentQuestion);
            } else {
                getMvpView().showAnswerActivity(currentQuestion, isLastQuestion);
            }
        }
    }

    public void onSprytarViewButtonClick() {
        double latitude = currentQuestion.getLatitude();
        double longitude = currentQuestion.getLongitude();

        if (latitude == 0.0f || longitude == 0.0f) {
            return;
        }
        getMvpView().showArCameraActivity(latitude, longitude);
    }

    private void sendQuestionResult(int questionId, int localQuestionId, boolean questionStatus) {
        compositeSubscription.add(spService
                .sendAnsweredQuestion(
                        spSession.getUserId(),
                        venueId,
                        venueActivity.getId(),
                        venueActivity.getGameTypeId(),
                        questionId,
                        questionStatus ? 1 : 0,
                        localQuestionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult response) {
                        if (!response.isSuccess()) {
                            getMvpView().showError("Error: " + response.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));
    }

    @Subscribe
    public void onEvent(SendQuestionResultEvent event) {
        sendQuestionResult(event.getQuestionId(), event.getLocalQuestionId(), event.isQuestionStatus());
    }

    public void setExplainerCompleted(boolean isCompleted) {
        spSession.setQuizExplainerCompleted(isCompleted);
    }

    public boolean getExplainerStatus() {
        return spSession.isExplainerCompleted();
    }
}
