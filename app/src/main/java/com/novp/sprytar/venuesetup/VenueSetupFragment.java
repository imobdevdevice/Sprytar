package com.novp.sprytar.venuesetup;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationSetup;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.FragmentSetupBinding;
import com.novp.sprytar.databinding.FragmentSetupVenueBinding;
import com.novp.sprytar.databinding.ItemVenueSetupQuestionBinding;
import com.novp.sprytar.location.LocationModule;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.presentation.BasePresenterPage;
import com.novp.sprytar.setup.DaggerSetupComponent;
import com.novp.sprytar.setup.LocationSetupAdapter;
import com.novp.sprytar.setup.SetupComponent;
import com.novp.sprytar.setup.SetupPresenter;
import com.novp.sprytar.setup.SetupView;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class VenueSetupFragment extends BaseFragment implements VenueSetupView, PoiListAdapter.AdapterCallback, QuestionListAdapter.AdapterCallback {
    public static final String LOCATION_PARAM = "com.novp.sprytar.venuesetup.VenueSetupFragment" +
            ".locationParam";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean locationPermissionGranted;

    @SuppressWarnings("WeakerAccess")
    @Inject
    VenueSetupPresenter presenter;

    @Inject
    QuestionListAdapter questionListAdapter;

    @Inject
    PoiListAdapter poiListAdapter;

    ViewPagerAdapter adapter;

    private final BaseBindingAdapter.ItemClickListener<Location> locationClickListener = new
            BaseBindingAdapter.ItemClickListener<Location>() {
                @Override
                public void onClick(Location item, int position) {

                }
            };

    private FragmentSetupVenueBinding binding;

    public VenueSetupFragment() {
    }

    public static VenueSetupFragment newInstance(Location location) {
        VenueSetupFragment fragment = new VenueSetupFragment();
        Bundle args = new Bundle();
        args.putParcelable(LOCATION_PARAM, Parcels.wrap(location));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        ensurePermissions();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentSetupVenueBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.setup);

        initUi();

        return binding.getRoot();

    }

    private void initUi() {

        List<CharSequence> titleList = new ArrayList<CharSequence>();
        titleList.add(getString(R.string.venue_setup_ioi_title));
        titleList.add(getString(R.string.venue_setup_treasure_title));

        VenueSetupPagePresenter poiPagePresenter = new VenueSetupPagePresenter(binding.poiLayout);
        VenueSetupPagePresenter huntPagePresenter = new VenueSetupPagePresenter(binding.huntLayout);

        adapter = new ViewPagerAdapter();
        adapter.addView(poiPagePresenter);
        adapter.addView(huntPagePresenter);
        adapter.setData(titleList);

        TabLayout tabLayout = binding.tabs;
        binding.viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(binding.viewPager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        binding.poiLayout.itemsRecyclerView.setLayoutManager(layoutManager1);
        binding.poiLayout.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.poiLayout.itemsRecyclerView.setAdapter(poiListAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        binding.huntLayout.itemsRecyclerView.setLayoutManager(layoutManager2);
        binding.huntLayout.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.huntLayout.itemsRecyclerView.setAdapter(questionListAdapter);

        poiListAdapter.setCallback(this);
        questionListAdapter.setCallback(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        Location location = Parcels.unwrap(getArguments().getParcelable(LOCATION_PARAM));
        presenter.setLocation(location);
    }

    private VenueSetupComponent createComponent() {
        return DaggerVenueSetupComponent.builder()
                .sessionComponent(getSessionComponent())
                .locationModule(new LocationModule())
                .build();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_occurred, message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        presenter.onDestroyed();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoadingIndicator() {

    }

    @Override
    public void hideLoadingIndicator() {

    }

    @Override
    public void onSetPoiLocation(int position) {
        setLocationWithConfirmation(VenueSetupPresenter.POI_PARAM, position);
    }

    @Override
    public void onSetQuestionLocation(int position) {
        setLocationWithConfirmation(VenueSetupPresenter.QUESTION_PARAM, position);
    }

    private void setLocationWithConfirmation(final String type, final int position) {

        new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setTitle(R.string.venue_setup_adjust_title)
                .setMessage(R.string.venue_setup_adjust_message)
                .setPositiveButton(R.string.confirm_button, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onSetLocation(type, position);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    @Override
    public void showLocationDetails(Location location, LocationSetup locationSetup) {

        binding.nameTextView.setText(location.getName());
        binding.locationTypeImageView.setImageResource(location.getIcon());
        binding.typeTextView.setText(location.getTypeName());

        poiListAdapter.setItems(locationSetup.getPois());
        questionListAdapter.setItems(locationSetup.getQuestions());

    }

    private class ViewPagerAdapter extends PagerAdapter {

        private final List<BasePresenterPage> mPresenterList = new ArrayList<>();
        private List<CharSequence> titles = new ArrayList<>();

        public void setData(List<CharSequence> titles) {
            this.titles = titles;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPresenterList.size();
        }

        public void addView(BasePresenterPage presenter) {
            mPresenterList.add(presenter);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ViewDataBinding currentView = mPresenterList.get(position).getView();
            return currentView.getRoot();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public void showDialogMessage(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.create().show();
    }

    private void ensurePermissions() {

        if (!PermissionUtils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtils.requestPermissions(getActivity(), getString(R.string
                            .permission_rationale_location),
                    LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest
                        .permission.ACCESS_FINE_LOCATION)) {
                    locationPermissionGranted = true;
                } else {
                    locationPermissionGranted = false;
                }
                break;
            }
        }
    }

}
