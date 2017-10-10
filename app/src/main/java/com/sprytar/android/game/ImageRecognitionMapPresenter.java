package com.sprytar.android.game;

import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class ImageRecognitionMapPresenter extends BasePresenter<ImageRecognitionMapView> {
    private Question question;

    @Inject
    ImageRecognitionMapPresenter(){
    }

    public void setVenueActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries,
                                 Question question, LatLng latLng){
        this.question = question;

        getMvpView().showVenueActivity(venueActivity,question);
        getMvpView().showFragments(boundaries,latLng,question.getLatitude(),question.getLongitude());
    }

    public void onShowHintClick(){
        String hint = question.getHint();
        if (hint != null) {
            getMvpView().showHint(hint);
        }
    }

    @Override
    public void onDestroyed() {
    }
}
