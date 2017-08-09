package com.novp.sprytar.game.trails;

import android.view.View;

import com.novp.sprytar.presentation.MvpView;

public interface TrailsMapView extends MvpView {

    View getChildFragment();

    void onTrailVisited(boolean[] visitedTrails, int position);

    void onGameCompleted();
}
