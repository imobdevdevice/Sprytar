package com.novp.sprytar.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.databinding.FragmentQuizGroupBinding;
import com.novp.sprytar.presentation.BaseFragmentUpdateable;
import com.novp.sprytar.util.ui.UpdateableFragment;
import com.novp.sprytar.util.ui.ViewPagerAdvanceStateAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;


public class QuizGroupFragment extends BaseFragmentUpdateable implements QuizGroupView,
        GameMapFragment.Callback {

    public static final String SHAPE_PARAM = "com.novp.sprytar.game.GameMapFragment" +
            ".shapeParam";

    public static final String CURRENT_LANLNG_PARAM = "com.novp.sprytar.game.GameMapFragment" +
            ".currentParam";

    @SuppressWarnings("WeakerAccess")
    @Inject
    QuizGroupPresenter presenter;
    GameMapFragment gameMapFragment;
    SpryteViewFragment spryteViewFragment;
    private FragmentQuizGroupBinding binding;
    private ViewPagerAdvanceStateAdapter adapter;
    private List<LocationBoundary> boundaries;
    private LatLng currentLatLng;
    private List<CharSequence> titleList;
    private List<Fragment> fragmentList;
    private Callback callback;

    public static QuizGroupFragment newInstance(List<LocationBoundary> boundaries, LatLng
            currentLatLn) {
        Bundle args = new Bundle();

        args.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));

        QuizGroupFragment fragment = new QuizGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createQuizGroupComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentQuizGroupBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);

        Bundle arguments = getArguments();

        if (arguments != null) {
            boundaries = Parcels.unwrap(arguments.getParcelable(SHAPE_PARAM));
            currentLatLng = Parcels.unwrap(arguments.getParcelable(CURRENT_LANLNG_PARAM));
            presenter.setCoordinates(boundaries, currentLatLng);
        }

        initUi();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        outState.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLng));

        super.onSaveInstanceState(outState);
    }

    private QuizGroupComponent createQuizGroupComponent() {
        return DaggerQuizGroupComponent.builder().applicationComponent(getApplicationComponent())
                .build();
    }

    @Override
    public void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn) {

        titleList = new ArrayList<CharSequence>();
        titleList.add(getString(R.string.game_map_title));
        titleList.add(getString(R.string.game_spryte_title));

        int[] iconsResId = {
                R.drawable.ic_map_main_color_24dp,
                R.drawable.spryte_24dp
        };

        adapter = new ViewPagerAdvanceStateAdapter(getChildFragmentManager());
        gameMapFragment = GameMapFragment.newInstance(boundaries, currentLatLn);
        gameMapFragment.setCallback(this);
        spryteViewFragment = SpryteViewFragment.newInstance(boundaries, currentLatLn);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(gameMapFragment);
        fragmentList.add(spryteViewFragment);

        adapter.setData(fragmentList, titleList);
        binding.viewPager.setAdapter(adapter);
        binding.tabs.setupWithViewPager(binding.viewPager);

        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabs.getTabAt(i);
            tab.setIcon(iconsResId[i]);
            tab.setText("");
        }

    }

    @Override
    public void updateFragmentContent(LatLng currentLatLn) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BaseFragmentUpdateable.UPDATE_PARAM_1, Parcels.wrap(currentLatLn));
        for (int i = 0; i < adapter.getCount(); i++) {
            UpdateableFragment fragment = (UpdateableFragment) adapter.getItem(i);
            fragment.updateContent(bundle);
        }
    }

    @Override
    public void onInSpryteZone() {
        callback.onInSpryteZone();
        adapter.setData(Collections.<Fragment>emptyList(), new ArrayList<CharSequence>());
        adapter.notifyDataSetChanged();
    }

    private void initUi() {
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void updateContent(Bundle bundle) {
        LatLng currentLatLng = Parcels.unwrap(bundle.getParcelable(BaseFragmentUpdateable
                .UPDATE_PARAM_1));
        presenter.updateMapContent(currentLatLng);
    }

    public interface Callback {
        void onInSpryteZone();
    }
}
