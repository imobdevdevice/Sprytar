package com.novp.sprytar.location;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.databinding.FragmentLocationBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.bottomsheet.CustomGridLayoutManager;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;
import com.novp.sprytar.venue.VenueFragment;

import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LocationFragment extends BaseFragment implements LocationView {

    private final BaseBindingAdapter.ItemClickListener<Location> locationClickListener = new
            BaseBindingAdapter.ItemClickListener<Location>() {
                @Override
                public void onClick(Location item, int position) {
                    //changeCamera(CameraUpdateFactory.newCameraPosition(itemCameraMap.get(item)));
                    VenueFragment fragment = VenueFragment.newInstance(item);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    getFragmentManager().beginTransaction().replace(R.id.content, fragment)
                            .commit();

                }
            };
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean locationPermissionGranted;

    @SuppressWarnings("WeakerAccess")
    @Inject
    LocationPresenter presenter;
    @Inject
    LocationListAdapter adapter;

    private FragmentLocationBinding binding;

    public LocationFragment() {
    }

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        ensurePermissions();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentLocationBinding.inflate(inflater, container, false);

        initUi();

        return binding.getRoot();
    }

    private void setListCanScroll(View view, final CustomGridLayoutManager layoutManager, final boolean canScroll){
        view.post(new Runnable() {
            @Override public void run() {
                layoutManager.setScrollEnabled(canScroll);
            }
        });
    }

    private void initUi() {
        getActivity().setTitle(R.string.locations);

        adapter.setItemClickListener(locationClickListener);

        final CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getActivity());
        layoutManager.setScrollEnabled(false);

        binding.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.itemsRecyclerView.setAdapter(adapter);

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.bottomFrame);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED){
                        setListCanScroll(bottomSheet,layoutManager,true);
                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                        setListCanScroll(bottomSheet,layoutManager,false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

//        binding.swipeRefreshLayout.setEnabled(true);
//
//        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//
//        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                presenter.loadItems();
//            }
//        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(presenter);

        if (savedInstanceState == null) {
            presenter.loadItems();
        }
    }

    private LocationComponent createLocationComponent() {
        return DaggerLocationComponent
                .builder()
                .sessionComponent(getSessionComponent())
                .locationModule(new LocationModule())
                .build();
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        presenter.onDestroyed();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(presenter);
        searchView.setOnCloseListener(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoadingIndicator() {
        //binding.progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
//        hideRefreshingIndicator();
//        binding.progressBar.setVisibility(GONE);
    }

    private void hideRefreshingIndicator() {
//        if (binding.swipeRefreshLayout.isRefreshing()) {
//            binding.swipeRefreshLayout.setRefreshing(false);
//        }
    }

    @Override
    public void showItems(List<Location> items) {
        adapter.setItems(items);
    }

    @Override
    public void clearItems() {
        adapter.clear();
    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public View getChildFragment() {
        return getChildFragmentManager().findFragmentById(R.id.map).getView();
    }

    private void ensurePermissions() {

        if (!PermissionUtils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtils.requestPermissions(getActivity(), getString(R.string
                            .permission_rationale_location),
                    LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            locationPermissionGranted = true;
            presenter.setMyLocationEnabled(locationPermissionGranted);
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
                presenter.setMyLocationEnabled(locationPermissionGranted);
                break;
            }
        }
    }

}
