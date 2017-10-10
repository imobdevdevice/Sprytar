package com.sprytar.android.game.trails;

import android.view.View;

import com.sprytar.android.presentation.MvpView;

public interface TrailsMapView extends MvpView {

    View getChildFragment();

    void onTrailVisited(boolean[] visitedTrails, int position);

    void onGameCompleted();
}
