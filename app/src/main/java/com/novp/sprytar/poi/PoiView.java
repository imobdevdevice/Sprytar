package com.novp.sprytar.poi;

import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.presentation.MvpView;

interface PoiView extends MvpView {

    void showError(String message);

    void showPoi(PointOfInterest poi);

    void showAmenity(Amenity amenity);

    void showAudioPoi(PointOfInterest audioPoi);

}
