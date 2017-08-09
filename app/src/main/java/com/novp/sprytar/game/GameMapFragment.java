package com.novp.sprytar.game;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.databinding.FragmentGameMapBinding;
import com.novp.sprytar.presentation.BaseFragmentUpdateable;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;


public class GameMapFragment extends BaseFragmentUpdateable implements GameMapView {
    public static final String SHAPE_PARAM = "com.novp.sprytar.game.GameMapFragment" +
            ".shapeParam";
    public static final String CURRENT_LANLNG_PARAM = "com.novp.sprytar.game.GameMapFragment" +
            ".currentParam";

    @SuppressWarnings("WeakerAccess")
    @Inject
    GameMapPresenter presenter;

    private FragmentGameMapBinding binding;

    private SupportMapFragment mapFragment;
    private Callback callback;

    public static GameMapFragment newInstance(List<LocationBoundary> boundaries, LatLng
            currentLatLn) {
        Bundle args = new Bundle();

        args.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));

        GameMapFragment fragment = new GameMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGameMapComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentGameMapBinding.inflate(inflater, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            List<LocationBoundary> boundaries = Parcels.unwrap(arguments.getParcelable
                    (SHAPE_PARAM));
            LatLng currentLatLng = Parcels.unwrap(arguments.getParcelable(CURRENT_LANLNG_PARAM));

            presenter.createMapData(boundaries, currentLatLng);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        initUi();
    }

    private GameMapComponent createGameMapComponent() {
        return DaggerGameMapComponent.builder().sessionComponent(getSessionComponent()).build();
    }

    private void initUi() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(presenter);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public View getChildFragment() {
        return mapFragment.getView();
    }

    @Override
    public void updateContent(Bundle bundle) {
        LatLng currentLatLng = Parcels.unwrap(bundle.getParcelable(BaseFragmentUpdateable
                .UPDATE_PARAM_1));
        presenter.updateMapContent(currentLatLng);

    }

    @Override
    public void showInSpryteZone() {
        if (callback != null) {
            callback.onInSpryteZone();
        }
    }

    public interface Callback {
        void onInSpryteZone();
    }

    public Location getCurrentLocation() {
        return presenter.getCurrentLocation();
    }
}
