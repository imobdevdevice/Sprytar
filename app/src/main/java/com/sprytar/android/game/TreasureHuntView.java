package com.sprytar.android.game;


import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

interface TreasureHuntView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity);

    void showRules();

    void startDialog();

    void startQuiz(VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, String locationName, String imageUrl);

    void showDistanceDialog();

}
