package com.novp.sprytar.game.quiz;

import android.content.Context;
import android.util.Log;

import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.PlayedGame;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.Quiz;
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

public class QuizGamePresenter extends BasePresenter<QuizGameView> {

    private final SpService spService;
    private final SpSession spSession;
    private final EventBus bus;

    private VenueActivity venueActivity;
    private int venueId;
    private int currentQuestionNumber;
    private EarnedBadge badge;
    private Quiz quiz;
    private Location location;

    private Question currentQuestion;
    private List<Question> questions;
    private boolean isLastQuestion = false;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    QuizGamePresenter(Context context, SpService spService, SpSession spSession, EventBus bus) {
        this.spService = spService;
        this.spSession = spSession;
        this.bus = bus;
    }

    @Override
    public void attachView(QuizGameView mvpView) {
        super.attachView(mvpView);
        bus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        bus.unregister(this);
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void setVenueActivity(VenueActivity venueActivity, int venueId, Location location,
                                 Quiz quiz) {
        this.venueActivity = venueActivity;
        this.venueActivity.setQuestions(quiz.getQuestionsRealm());
        this.questions = quiz.getQuestions();
        this.venueId = venueId;
        this.location = location;
        this.quiz = quiz;

        updateQuestions();
        //  getMvpView().setQuestions(venueActivity.getQuestions());
    }

    public void showHint() {
        String hint = venueActivity.getQuestions().get(currentQuestionNumber).getHint();
        getMvpView().showHint(hint);
    }

    public void setNextQuestion() {
        currentQuestionNumber++;
        if (currentQuestionNumber > venueActivity.getQuestions().size() - 1) {
            // finish the quiz
            setEarnedBadge();
        } else {
            currentQuestion = questions.get(currentQuestionNumber);
            if (currentQuestion.isImageRecognitionQuestion()) {
                getMvpView().showImageQuestion(venueActivity, currentQuestion);
            } else {
                getMvpView().setNextQuestion(currentQuestion, currentQuestionNumber);
            }
        }
    }

    private void updateQuestions() {
        currentQuestionNumber = 0;
        currentQuestion = questions.get(currentQuestionNumber);

        if (currentQuestion.isImageRecognitionQuestion()) {
            getMvpView().showImageQuestion(venueActivity, currentQuestion);
        } else {
            getMvpView().showVenueActivity(venueActivity, currentQuestion);
        }
    }

    private void setEarnedBadge() {
        if (spSession.isLoggedIn()) {
            compositeSubscription.add(spService
                    .sendEarnedBadge(spSession.getUserId(), quiz.getQuizBadgeId(), quiz.getQuizId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SpResult<List<EarnedBadge>>>() {
                        @Override
                        public void call(SpResult<List<EarnedBadge>> response) {
                            Log.v("test_tag", response.getData().get(0).toString());
                            if (!response.isSuccess()) {
                                // getMvpView().showError("Error: " + response.getMessage());
                            } else {
                                badge = response.getData().get(0);
                                setGameFinished();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            // getMvpView().showError(throwable.getLocalizedMessage());
                        }
                    }));
        } else {
            getMvpView().onFinishQuiz(location, venueActivity, badge, 0, 0);
        }
    }

    private void setGameFinished() {
        compositeSubscription.add(spService.sendPlayedGame(spSession.getUserId(), venueId, venueActivity.getId(), venueActivity.getGameTypeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<PlayedGame>>() {
                    @Override
                    public void call(SpResult<PlayedGame> response) {
                        int correctAnswers = response.getData().correctAnswers;
                        getMvpView().onFinishQuiz(location, venueActivity, badge, correctAnswers, questions.size());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //  getMvpView().showError(throwable.getLocalizedMessage());
                        getMvpView().onFinishQuiz(location, venueActivity, badge, 0, 0);
                    }
                }));
    }

    public void setQuestionAnswered(int questionId, int localQuestionId, boolean questionStatus) {
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
                            // getMvpView().showError("Error: " + response.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //  getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));
    }

    @Subscribe
    public void onEvent(SendQuestionResultEvent event) {
        setQuestionAnswered(event.getQuestionId(), event.getLocalQuestionId(), event.isQuestionStatus());
    }

}
