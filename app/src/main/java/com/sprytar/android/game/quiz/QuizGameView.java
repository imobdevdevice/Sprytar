package com.sprytar.android.game.quiz;

import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface QuizGameView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void setQuestions(List<Question> questions);

    void setNextQuestion(Question question, int questionNumber);

    void onFinishQuiz(Location location, VenueActivity venueActivity, EarnedBadge badge,
                      int correctAnswers, int numOfQuestions);

    void showHint(String hint);

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity, Question question);

    void showImageQuestion(VenueActivity venueActivity, Question question);

}
