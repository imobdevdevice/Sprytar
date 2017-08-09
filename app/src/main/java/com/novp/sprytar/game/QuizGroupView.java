package com.novp.sprytar.game;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface QuizGroupView extends MvpView {

    void showError(String message);

    void setTitle(String title);

    void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn);

    void updateFragmentContent(LatLng currentLatLn);

}
