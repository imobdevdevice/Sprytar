package com.sprytar.android.venue;

import android.view.View;

import com.sprytar.android.data.model.Amenity;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.PointOfInterest;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.BaseView;

import java.util.List;

public interface VenueView extends BaseView<VenueActivity> {


    void showErrorDialog(boolean hasNoInternet);

    void showError(String message);

    void setTitle(String title);

    View getChildFragment();

    void showTreasureHuntActivity(VenueActivity venueActivity, boolean
            insideBoundaries, int locationId, String locationName, String imageUrl, List<LocationBoundary> boundaries);

    void showFitnessActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries);

    void showGuidedToursActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries);

    void showQuizGameActivity(VenueActivity venueActivity, boolean insideBoundaries, Location location);

    void showTrailsGameActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries,
                                boolean insideBoundaries, int venueId);

    void showPoiDetails(PointOfInterest poi);

    void showAmenityDetails(Amenity amenity);

    void showOfflineDialog(boolean saveDialog);

    void showDialogMessage(String message);

}
