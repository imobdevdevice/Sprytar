package com.novp.sprytar.venue;

import android.view.View;

import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.BaseView;

import java.util.List;

public interface VenueView extends BaseView<VenueActivity> {

    void showError(String message);

    void setTitle(String title);

    View getChildFragment();

    void showTreasureHuntActivity(VenueActivity venueActivity, boolean
            insideBoundaries,int locationId, String locationName, String imageUrl, List<LocationBoundary> boundaries);

    void showFitnessActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries);

    void showGuidedToursActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries);

    void showQuizGameActivity(VenueActivity venueActivity, boolean insideBoundaries, Location location);

    void showTrailsGameActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries,
                              boolean insideBoundaries,int venueId);

    void showPoiDetails(PointOfInterest poi);

    void showAmenityDetails(Amenity amenity);

    void showOfflineDialog(boolean saveDialog);

    void showDialogMessage(String message);
}
