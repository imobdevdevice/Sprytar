package com.novp.sprytar.venue;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.FragmentVenueBinding;
import com.novp.sprytar.fitness.FitnessClassesActivity;
import com.novp.sprytar.game.TreasureHuntActivity;
import com.novp.sprytar.game.quiz.QuizGameStartActivity;
import com.novp.sprytar.game.trails.TrailsGameStartActivity;
import com.novp.sprytar.poi.PoiActivity;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.tour.GuidedToursActivity;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class VenueFragment extends BaseFragment implements VenueView {

    public static final String LOCATION_PARAM = "com.novp.sprytar.venue.VenueFragment" +
            ".locationParam";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    @SuppressWarnings("WeakerAccess")
    @Inject
    VenuePresenter presenter;
    private final BaseBindingAdapter.ItemClickListener<VenueActivity> venueclickListener = new
            BaseBindingAdapter.ItemClickListener<VenueActivity>() {
                @Override
                public void onClick(VenueActivity item, int position) {
                    presenter.onVenueActivityClick(item);
                }
            };
    @Inject
    VenueListAdapter adapter;
    private boolean locationPermissionGranted;
    private FragmentVenueBinding binding;

    public VenueFragment() {
    }

    public static VenueFragment newInstance(Location location) {
        Bundle args = new Bundle();
        args.putParcelable(LOCATION_PARAM, Parcels.wrap(location));
        VenueFragment fragment = new VenueFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        ensurePermissions();

        presenter.setMyLocationEnabled(locationPermissionGranted);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentVenueBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.venue);

        initRecyclerView();

        initBottomSheet();

        return binding.getRoot();
    }

    private void initRecyclerView() {

        adapter.setItemClickListener(venueclickListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        binding.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.itemsRecyclerView.setAdapter(adapter);

//        binding.swipeRefreshLayout.setEnabled(true);
//
//        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//
//        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout
// .OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                presenter.loadItems();
//            }
//        });
    }

    private void initBottomSheet() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(presenter);

        Location location = Parcels.unwrap(getArguments().getParcelable(LOCATION_PARAM));
        presenter.setLocation(location);

    }

    private VenueComponent createLocationComponent() {
        return DaggerVenueComponent.builder()
                .sessionComponent(getSessionComponent())
                .venueModule(new VenueModule())
                .build();
    }

    private void ensurePermissions() {

        if (!PermissionUtils.checkPermission(getActivity(), Manifest.permission
                .ACCESS_FINE_LOCATION)) {
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

    @Override
    public void setTitle(String title) {
        getActivity().setTitle(title);
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
        inflater.inflate(R.menu.menu_offline_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(presenter);
        searchView.setOnCloseListener(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_offline) {
            presenter.onOfflineClick();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoadingIndicator() {
    }

    @Override
    public void hideLoadingIndicator() {
    }

    @Override
    public void showItems(List<VenueActivity> items) {
        adapter.setItems(items);
    }

    @Override
    public void clearItems() {
        adapter.clear();
    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public View getChildFragment() {
        return getChildFragmentManager().findFragmentById(R.id.map).getView();
    }

    @Override
    public void showTreasureHuntActivity(VenueActivity venueActivity, boolean insideBoundaries, int locationId, String locationName, String imageUrl, List<LocationBoundary> boundaries) {
        TreasureHuntActivity.start(getActivity(), venueActivity, insideBoundaries, locationId, locationName, imageUrl, boundaries);
    }

    @Override
    public void showQuizGameActivity(VenueActivity venueActivity, boolean insideBoundaries, Location location) {
        QuizGameStartActivity.start(getActivity(), venueActivity, insideBoundaries, location);
    }

    @Override
    public void showTrailsGameActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries, boolean insideBoundaries, int venueId) {
        TrailsGameStartActivity.start(getActivity(), venueActivity, insideBoundaries, boundaries, venueId);
    }

    @Override
    public void showFitnessActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries) {
        FitnessClassesActivity.start(getActivity(), venueActivity, boundaries);
    }

    @Override
    public void showGuidedToursActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries) {
        GuidedToursActivity.start(getActivity(), venueActivity, boundaries);
    }

    @Override
    public void showPoiDetails(PointOfInterest poi) {
        PoiActivity.start(getActivity(), poi);
    }

    @Override
    public void showAmenityDetails(Amenity amenity) {
        if (amenity.getDescription() != null) {
            PoiActivity.start(getActivity(), amenity);
        }
    }

    @Override
    public void showOfflineDialog(boolean saveDialog) {
        AlertDialog dialog = OfflineAccessDialog.getDialog(getContext(), saveDialog, presenter);
        dialog.show();
    }

    @Override
    public void showDialogMessage(String message) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_offline_access_granted);

        TextView thanks = (TextView) dialog.findViewById(R.id.btn_thanks);
        TextView title = (TextView) dialog.findViewById(R.id.status_title);
        TextView messageText = (TextView) dialog.findViewById(R.id.message);

        thanks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        messageText.setText(message);

        if (message.equals(getResources().getString(R.string.message_save_succes))) {
            title.setText(getResources().getString(R.string.download_complete));
        } else {
            title.setText(getResources().getString(R.string.offline_access_title));
        }

        dialog.show();
    }
}
