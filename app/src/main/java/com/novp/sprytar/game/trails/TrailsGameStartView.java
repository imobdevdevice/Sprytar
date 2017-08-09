package com.novp.sprytar.game.trails;

import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface TrailsGameStartView extends MvpView{

    void startSubTrailsGame(Trail trail, SubTrail subTrail, List<LocationBoundary> boundries, int venueId);

    void setVenueTitle(String name);

    void showError(String message);

    void closeActivity();

    void showItems(List<SubTrail> items);

    void showDistanceDialog();

    void showDialogMessage(String message);
}
