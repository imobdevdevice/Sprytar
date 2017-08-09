package com.novp.sprytar.game;

import android.view.View;

import com.novp.sprytar.presentation.MvpView;

public interface GameMapView extends MvpView {

    void showError(String message);

    void setTitle(String title);

    View getChildFragment();

    void showInSpryteZone();

}
