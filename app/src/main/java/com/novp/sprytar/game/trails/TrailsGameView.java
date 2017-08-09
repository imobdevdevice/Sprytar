package com.novp.sprytar.game.trails;

import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.presentation.MvpView;

import java.util.List;

public interface TrailsGameView extends MvpView{

    void setTrailTitle(String title);

    void setSubTrailData(String subTitle,String info, int resource);

    void setFragment(List<LocationBoundary> boundaries, SubTrail subtrail);

    void finishTheGame(Trail trail, SubTrail subTrail, EarnedBadge earnedBadge);

}
