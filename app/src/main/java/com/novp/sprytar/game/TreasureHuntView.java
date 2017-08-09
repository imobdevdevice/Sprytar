package com.novp.sprytar.game;


import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

interface TreasureHuntView extends MvpView {

    void showError(String message);

    void showVenueActivity(VenueActivity venueActivity);

    void showRules();

    void startDialog();

    void startQuiz(VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, String locationName,String imageUrl);

    void showDistanceDialog();



}
