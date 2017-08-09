package com.novp.sprytar.game;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface ImageRecognitionMapView extends MvpView {
    void showVenueActivity(VenueActivity venueActivity, Question question);

    void showFragments(List<LocationBoundary> boundaries, LatLng latLng, double latitude,
                       double longitude);

    void showHint(String hint);
}
