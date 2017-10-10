package com.sprytar.android.game;


import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

interface QuizView extends MvpView {

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity, Question question);

   // void showImageQuestion(VenueActivity venueActivity, Question question);

    void showAnswerActivity(Question question, boolean isLastQuestion);

    void showAnswerImageActivity(Question question);

    View getChildFragment();

    void showArCameraActivity(double latitude, double longitude);

    void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn, double latitude,
                       double
                               longitude, double distance);

    void updateFragmentContent(LatLng currentLatLn);

    void showGameFinishedActivity(EarnedBadge badge, String locationName, String locationImageUrl, int correctAnswers, int numOfQuestions);

    void showHint(String hint);

    void showPhotoHuntActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries,
                               int venueId, LatLng currentLatLn, Question question);

    void showImageRecognitionMapActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries,
                                         LatLng currentLatLng, Question question);
}
