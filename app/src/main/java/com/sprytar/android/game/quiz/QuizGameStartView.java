package com.sprytar.android.game.quiz;

import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.Quiz;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

public interface QuizGameStartView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void startQuiz(VenueActivity venueActivity, int venueId, Location location, Quiz quiz);

    void showDistanceDialog();

    void setVenueTitle(String title);

    void showDialogMessage(String message);
}
