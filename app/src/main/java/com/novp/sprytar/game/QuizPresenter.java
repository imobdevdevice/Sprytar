package com.novp.sprytar.game;

import android.content.Context;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.PlayedGame;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.events.SendQuestionResultEvent;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
                    .getLatitude(), currentQuestion.getLongitude());
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

    private void getQuestions() {
        this.questions = venueActivity.getQuestions();
    }

    public void nextQuestion() {
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
                        .getLatitude(), currentQuestion.getLongitude());

            }
        }
    }

    private void onFinishGame() {
        if (spSession.isLoggedIn()) {
            compositeSubscription.add(spService
                    .sendEarnedBadge(spSession.getUserId(), venueActivity.getGameBadgeId(), venueId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<List<EarnedBadge>>>() {
                        @Override
                        public void call(SpResult<List<EarnedBadge>> response) {
                            if (!response.isSuccess()) {
                                //  getMvpView().showError("Error: " + response.getMessage());
                            } else {
                                badge = response.getData().get(0);
                                sendGamePlayed();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //  getMvpView().showError(throwable.getLocalizedMessage());
                        }
                    }));

        } else {
            getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, 0, 0);
        }
    }

    private void sendGamePlayed() {
        compositeSubscription.add(spService.sendPlayedGame(spSession.getUserId(), venueId, venueActivity.getId(), venueActivity.getGameTypeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<PlayedGame>>() {
                    @Override
                    public void call(SpResult<PlayedGame> response) {
                        int correctAnswers = response.getData().correctAnswers;
                        getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, correctAnswers, questions.size());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().showGameFinishedActivity(badge, locationName, locationImageUrl, 0, 0);
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
