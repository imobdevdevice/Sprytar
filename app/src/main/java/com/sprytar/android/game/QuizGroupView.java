package com.sprytar.android.game;

import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface QuizGroupView extends MvpView {

    void showError(String message);

    void setTitle(String title);

    void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn);

    void updateFragmentContent(LatLng currentLatLn);

}
