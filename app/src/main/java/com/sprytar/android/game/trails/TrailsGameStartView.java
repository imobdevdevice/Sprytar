package com.sprytar.android.game.trails;

import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.Trail;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface TrailsGameStartView extends MvpView {

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorDialog(boolean hasNoInternet);

    void startSubTrailsGame(Trail trail, SubTrail subTrail, List<LocationBoundary> boundries, int venueId);

    void setVenueTitle(String name);

    void showError(String message);

    void closeActivity();

    void showItems(List<SubTrail> items);

    void showDistanceDialog();

    void showDialogMessage(String message);
}
