package com.novp.sprytar.game.trails;

import android.os.Bundle;

import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.TrailPoint;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface TrailsGroupView extends MvpView {

    void showFragments(List<LocationBoundary> boundaries, SubTrail subTrail);

    void updateARView(Bundle bundle);

    void hideClearMapButton();

    void showClearMapButton();

    void showTrailPointReachedDialog(TrailPoint trailPoint);

    void showDirectionsDialog(TrailPoint trailPoint);
}
