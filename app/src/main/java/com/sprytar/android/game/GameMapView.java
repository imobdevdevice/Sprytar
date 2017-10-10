package com.sprytar.android.game;

import android.view.View;
import android.view.animation.Animation;

import com.sprytar.android.presentation.MvpView;

public interface GameMapView extends MvpView {

    void showError(String message);

    void setTitle(String title);

    View getChildFragment();

    void showInSpryteZone();

    void showInARZone();
    void showNoGpsMessage();
    void onChangeDirection(Animation animation);
    void onChangeDistance(String distance);
}
