package com.sprytar.android.game.trails;

import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.Trail;
import com.sprytar.android.presentation.MvpView;

import java.util.List;

public interface TrailsGameView extends MvpView {

    void setTrailTitle(String title);

    void setSubTrailData(String subTitle, String info, int resource);

    void setFragment(List<LocationBoundary> boundaries, SubTrail subtrail);

    void finishTheGame(Trail trail, SubTrail subTrail, EarnedBadge earnedBadge);

}
