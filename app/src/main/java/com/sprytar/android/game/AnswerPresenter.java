package com.sprytar.android.game;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Answer;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.events.SendQuestionResultEvent;
import com.sprytar.android.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public class AnswerPresenter extends BasePresenter<AnswerView> {

    private final SpSession spSession;
    private final EventBus bus;

    private Question question;

    private boolean questionAnswered;

    private boolean questionAnsweredCorrectly;

    private List<Answer> answers;

    private Answer correctAnswer;

    private Answer userAnswer;

    @Inject
    AnswerPresenter(SpSession spSession, EventBus bus) {
        this.spSession = spSession;
        this.bus = bus;
    }

    @Override
    public void attachView(AnswerView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onDestroyed() {
    }

    public void setQuestion(Question question){
        this.question = question;

        answers = question.getAnswers();

        List<Answer> correct = Lists.newArrayList(Collections2.filter(answers, new AnswerFilter()));
        if (! correct.isEmpty()) {
            correctAnswer = correct.get(0);
        }

        getMvpView().showQuestion(question, questionAnswered, questionAnsweredCorrectly);
    }

    public void onAnswerClick(Answer answer) {
        if (questionAnswered) {
            return;
        }

        questionAnswered = true;
        userAnswer = answer;
        if (userAnswer.equals(correctAnswer)) {
            questionAnsweredCorrectly = true;
        }

        sendResultToServer(questionAnsweredCorrectly);

        getMvpView().showQuestion(question, questionAnswered, questionAnsweredCorrectly);
    }

    private void sendResultToServer(boolean questionStatus) {
        if (!spSession.isLoggedIn()) {
            return;
        }
        bus.post(new SendQuestionResultEvent(question.getGlobalId(),question.getId(),questionStatus));
    }

    public void onNavigationButtonClick(){
        if (questionAnswered) {
            getMvpView().nextQuestion();
        } else {
            getMvpView().close();
        }
    }

    public void onShowHintClick() {
        String hint = question.getHint();
        if (hint != null) {
            getMvpView().showHint(hint);
        }
    }

    private final class AnswerFilter implements Predicate<Answer> {
        @Override
        public boolean apply(Answer input) {
            return input.isAnswerCorrect();
        }
    }

}
