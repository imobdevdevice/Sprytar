package com.novp.sprytar.game.trails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.databinding.FragmentGameMapBinding;
import com.novp.sprytar.presentation.BaseFragmentUpdateable;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class TrailsMapFragment extends BaseFragmentUpdateable implements TrailsMapView {

    public static final String BOUNDARIES_EXTRA = "com.novp.sprytar.game.BoundariesExtra";
    public static final String SUBTRAIL_EXTRA = "com.novp.sprytar.game.SubTrailExtra";

    public static TrailsMapFragment newInstance(List<LocationBoundary> boundaries, SubTrail subTrail) {
        Bundle args = new Bundle();

        args.putParcelable(BOUNDARIES_EXTRA, Parcels.wrap(boundaries));
        args.putSerializable(SUBTRAIL_EXTRA, subTrail);

        TrailsMapFragment fragment = new TrailsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface TrailVisitCallback {
        void onTrailVisited(boolean[] visitedTrails, int position);

        void onGameCompleted();
    }

    @Inject
    TrailsMapPresenter presenter;

    private FragmentGameMapBinding binding;
    private SupportMapFragment mapFragment;
    private TrailVisitCallback trailVisitCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTrailsMapComponent().inject(this);
    }

    public void setTrailVisitedCallback(TrailVisitCallback trailVisitedCallback) {
        this.trailVisitCallback = trailVisitedCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentGameMapBinding.inflate(inflater, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            List<LocationBoundary> boundaries = Parcels.unwrap(arguments.getParcelable
                    (BOUNDARIES_EXTRA));
            SubTrail subTrail = (SubTrail) arguments.getSerializable(SUBTRAIL_EXTRA);

            presenter.createMapData(boundaries, subTrail);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        initUi();
    }

    private void initUi() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(presenter);
    }

    @Override
    public View getChildFragment() {
        return mapFragment.getView();
    }

    private TrailsMapComponent createTrailsMapComponent() {
        return DaggerTrailsMapComponent.builder().sessionComponent(getSessionComponent()).build();
    }

    @Override
    public void onTrailVisited(boolean[] visitedTrails, int position) {
        trailVisitCallback.onTrailVisited(visitedTrails, position);
    }

    public void clearMap() {
        presenter.clearMap();
    }

    public void updateVisitedTrails(boolean[] visitedTrails) {
        presenter.updateVisitedTrails(visitedTrails);
    }

    @Override
    public void onGameCompleted() {
        trailVisitCallback.onGameCompleted();
    }

    @Override
    public void updateContent(Bundle bundle) {

    }
}
