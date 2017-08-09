package com.novp.sprytar.game;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.presentation.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class QuizGroupPresenter extends BasePresenter<QuizGroupView> {

    private final Context context;

    private List<LocationBoundary> boundaries;
    private LatLng currentLatLn;

    @Inject
    QuizGroupPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(QuizGroupView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onDestroyed() {

    }

    public void setCoordinates(List<LocationBoundary> boundaries, LatLng currentLatLn) {
        this.boundaries = boundaries;
        this.currentLatLn = currentLatLn;

        getMvpView().showFragments(boundaries, currentLatLn);
    }

    public void updateMapContent(LatLng currentLatLn) {
        getMvpView().updateFragmentContent(currentLatLn);
    }
}
