package com.novp.sprytar.game.quiz;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Quiz;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

public interface QuizGameStartView extends MvpView{

    void showRules();

    void startQuiz(VenueActivity venueActivity, int venueId,Location location, Quiz quiz);

    void showDistanceDialog();

    void setVenueTitle(String title);

    void showDialogMessage(String message);
}
