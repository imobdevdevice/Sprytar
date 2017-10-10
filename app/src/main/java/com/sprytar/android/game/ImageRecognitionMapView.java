package com.sprytar.android.game;

import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface ImageRecognitionMapView extends MvpView {
    void showVenueActivity(VenueActivity venueActivity, Question question);

    void showFragments(List<LocationBoundary> boundaries, LatLng latLng, double latitude,
                       double longitude);

    void showHint(String hint);
}
