package com.novp.sprytar.game.trails;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.presentation.BaseFragmentUpdateable;
import com.novp.sprytar.presentation.BasePresenter;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TrailsGroupPresenter extends BasePresenter<TrailsGroupView> {

    private List<LocationBoundary> boundaries;
    private SubTrail subTrail;
    private boolean[] visitedTrails;
    private int visitedTrailPosition = 0;

    @Inject
    TrailsGroupPresenter() {

    }

    public void setData(List<LocationBoundary> boundaries, SubTrail subTrail) {
        this.boundaries = boundaries;
        this.subTrail = subTrail;
        visitedTrails = new boolean[subTrail.getTrailsPoints().size()];
        for (int i = 0; i < subTrail.getTrailsPoints().size(); i++) {
            visitedTrails[i] = false;
        }
        getMvpView().showFragments(boundaries, subTrail);
    }

    public void updateVisitedTrails(boolean[] visitedTrails, int position) {
        this.visitedTrails = visitedTrails;
        this.visitedTrailPosition = position;

        getMvpView().updateARView(changeARTrail());
        getMvpView().showTrailPointReachedDialog(subTrail.getTrailsPoints().get(position));
    }

    public void clearMap(){
        for(int i=0;i<visitedTrails.length;i++){
            visitedTrails[i] = false;
        }

        getMvpView().updateARView(changeARTrail());
    }

    private Bundle changeARTrail() {
        Bundle bundle = new Bundle();
        boolean foundNotVisited = false;
        for (int i = 0; i < visitedTrails.length; i++) {
            if (visitedTrails[i] == false) {
                LatLng latLng = new LatLng(subTrail.getTrailsPoints().get(i).getLatitude(),
                        subTrail.getTrailsPoints().get(i).getLongitude());
                bundle.putParcelable(BaseFragmentUpdateable.UPDATE_PARAM_1, Parcels.wrap(latLng));
                foundNotVisited = true;
                break;
            }
        }

        if (foundNotVisited) {
            return bundle;
        } else {
            return null;
        }
    }

    public void navigateViewPager(int position){
        if(position == 0){
            getMvpView().showClearMapButton();
        }else {
            getMvpView().hideClearMapButton();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void showDirectionsDialog(){
        getMvpView().showDirectionsDialog(subTrail.getTrailsPoints().get(visitedTrailPosition));
    }

    public void showTrailReachedDialog(){
        getMvpView().showTrailPointReachedDialog(subTrail.getTrailsPoints().get(visitedTrailPosition));
    }
}
