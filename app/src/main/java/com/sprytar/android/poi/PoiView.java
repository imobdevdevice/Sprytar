package com.sprytar.android.poi;

import com.sprytar.android.data.model.Amenity;
import com.sprytar.android.data.model.PointOfInterest;
import com.sprytar.android.presentation.MvpView;

interface PoiView extends MvpView {

    void showError(String message);

    void showPoi(PointOfInterest poi);

    void showAmenity(Amenity amenity);

    void showAudioPoi(PointOfInterest audioPoi);

}
