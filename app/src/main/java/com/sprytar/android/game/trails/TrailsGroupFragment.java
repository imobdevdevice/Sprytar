package com.sprytar.android.game.trails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentTrailsGroupBinding;
import com.sprytar.android.game.trails.DaggerTrailsGroupComponent;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.SubTrail;
import com.sprytar.android.data.model.TrailPoint;
import com.sprytar.android.game.SpryteViewFragment;
import com.sprytar.android.presentation.BaseFragmentUpdateable;
import com.sprytar.android.util.ui.UpdateableFragment;
import com.sprytar.android.util.ui.ViewPagerAdvanceStateAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrailsGroupFragment extends BaseFragmentUpdateable implements TrailsGroupView, TrailsMapFragment.TrailVisitCallback,
        TrailReachedDialog.TrailReachedCallback, DirectionsDialog.DirectionsDialogCallback {

    public static final String BOUNDARIES_EXTRA = "com.sprytar.android.game.BoundariesExtra";
    public static final String SUBTRAIL_EXTRA = "com.sprytar.android.game.SubTrailExtra";

    public interface TrailsCallback {
        void onGameCompleted();

        void onHideClearMapButton();

        void onShowClearMapButton();
    }

    public static TrailsGroupFragment newInstance(List<LocationBoundary> boundaries, SubTrail subTrail) {
        Bundle args = new Bundle();

        args.putParcelable(BOUNDARIES_EXTRA, Parcels.wrap(boundaries));
        args.putSerializable(SUBTRAIL_EXTRA, subTrail);

        TrailsGroupFragment fragment = new TrailsGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    TrailsGroupPresenter presenter;

    private FragmentTrailsGroupBinding binding;
    private List<CharSequence> titleList;
    private ViewPagerAdvanceStateAdapter adapter;
    private TrailsCallback trailsCallback;
    private TrailsMapFragment trailsMapFragment;
    private SpryteViewFragment spryteViewFragment;
    private List<Fragment> fragmentList;

    public void setTrailsCallback(TrailsCallback trailsCallback) {
        this.trailsCallback = trailsCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTrailsGroupComponent().inject(this);
    }

    private TrailsGroupComponent createTrailsGroupComponent() {
        return DaggerTrailsGroupComponent.builder().applicationComponent(getApplicationComponent())
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentTrailsGroupBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);

        Bundle arguments = getArguments();

        if (arguments != null) {
            List<LocationBoundary> boundaries = Parcels.unwrap(arguments.getParcelable(BOUNDARIES_EXTRA));
            SubTrail subTrail = (SubTrail) arguments.getSerializable(SUBTRAIL_EXTRA);
            presenter.setData(boundaries, subTrail);
        }
    }

    @Override
    public void showFragments(List<LocationBoundary> boundaries, SubTrail subTrail) {
        titleList = new ArrayList<CharSequence>();
        titleList.add(getString(R.string.trails_trail_on_map));
        titleList.add(getString(R.string.trails_ar_view));

        adapter = new ViewPagerAdvanceStateAdapter(getChildFragmentManager());
        trailsMapFragment = TrailsMapFragment.newInstance(boundaries, subTrail);
        trailsMapFragment.setTrailVisitedCallback(this);

        LatLng latLng = new LatLng(subTrail.getTrailsPoints().get(0).getLatitude(),
                subTrail.getTrailsPoints().get(0).getLongitude());
        spryteViewFragment = SpryteViewFragment.newInstance(boundaries, latLng);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(trailsMapFragment);
        fragmentList.add(spryteViewFragment);

        adapter.setData(fragmentList, titleList);

        binding.viewPager.setAdapter(adapter);
        binding.tabs.setupWithViewPager(binding.viewPager);


        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                presenter.navigateViewPager(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*
        binding.viewPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                }
        });
        */
    }

    @Override
    public void updateContent(Bundle bundle) {

    }

    @Override
    public void hideClearMapButton() {
        trailsCallback.onHideClearMapButton();
    }

    @Override
    public void showClearMapButton() {
        trailsCallback.onShowClearMapButton();
    }

    @Override
    public void onTrailVisited(boolean[] visitedTrails, int position) {
        presenter.updateVisitedTrails(visitedTrails, position);
    }

    public void clearMap() {
        presenter.clearMap();
        trailsMapFragment.clearMap();
    }

    @Override
    public void updateARView(Bundle bundle) {
        if (bundle != null) {
            UpdateableFragment fragment = (UpdateableFragment) adapter.getItem(1);
            fragment.updateContent(bundle);
        }
    }

    @Override
    public void onGameCompleted() {
        trailsCallback.onGameCompleted();
    }

    @Override
    public void showTrailPointReachedDialog(TrailPoint trailPoint) {
        TrailReachedDialog dialog = TrailReachedDialog.newInstance(trailPoint.getMessage());
        dialog.setTrailReachedCallback(this);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(dialog, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showDirectionsDialog(TrailPoint trailPoint) {
        DirectionsDialog dialog = DirectionsDialog.newInstance(trailPoint.getDirection());
        dialog.setDirectionsCallback(this);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(dialog, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onShowDirectionsClick() {
        presenter.showDirectionsDialog();
    }

    @Override
    public void onShowMessageClick() {
        presenter.showTrailReachedDialog();
    }
}
