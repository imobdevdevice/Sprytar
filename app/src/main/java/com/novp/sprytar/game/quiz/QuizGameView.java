package com.novp.sprytar.game.quiz;

import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface QuizGameView extends MvpView {

    void setQuestions(List<Question> questions);

    void setNextQuestion(Question question, int questionNumber);

    void onFinishQuiz(Location location, VenueActivity venueActivity, EarnedBadge badge,
                      int correctAnswers, int numOfQuestions);

    void showHint(String hint);

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity, Question question);

    void showImageQuestion(VenueActivity venueActivity, Question question);

}
