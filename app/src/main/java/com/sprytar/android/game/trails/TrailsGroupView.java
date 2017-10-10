package com.sprytar.android.game.trails;

import android.os.Bundle;

import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.TrailPoint;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface TrailsGroupView extends MvpView {

    void showFragments(List<LocationBoundary> boundaries, SubTrail subTrail);

    void updateARView(Bundle bundle);

    void hideClearMapButton();

    void showClearMapButton();

    void showTrailPointReachedDialog(TrailPoint trailPoint);

    void showDirectionsDialog(TrailPoint trailPoint);
}
