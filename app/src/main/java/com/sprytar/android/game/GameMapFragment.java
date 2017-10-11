package com.sprytar.android.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.R;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.databinding.FragmentGameMapBinding;
import com.sprytar.android.presentation.BaseFragmentUpdateable;
import com.sprytar.android.util.Utils;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;


public class GameMapFragment extends BaseFragmentUpdateable implements GameMapView, View.OnClickListener {
    public static final String SHAPE_PARAM = "com.sprytar.android.game.GameMapFragment" +
            ".shapeParam";
    public static final String CURRENT_LANLNG_PARAM = "com.sprytar.android.game.GameMapFragment" +
            ".currentParam";
    public static final String SHOW_RANGE_FINDER = "show_range_finder";
    public static final String QUESTION_DISTANCE = "question_distance";


    @SuppressWarnings("WeakerAccess")
    @Inject
    GameMapPresenter presenter;

    private FragmentGameMapBinding binding;

    private SupportMapFragment mapFragment;
    private Callback callback;
    private boolean showRangeFinder = false;
    private AlertDialog noGpsDialog = null;
    private double DISTANCE = 10;
    protected LocationRequest mLocationRequest;

    public static GameMapFragment newInstance(List<LocationBoundary> boundaries, LatLng
            currentLatLn) {

        Bundle args = new Bundle();
        args.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));
        GameMapFragment fragment = new GameMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GameMapFragment newInstance(List<LocationBoundary> boundaries, LatLng
            currentLatLn, boolean showRangeFinder, double distance) {
        Bundle args = new Bundle();

        args.putParcelable(SHAPE_PARAM, Parcels.wrap(boundaries));
        args.putParcelable(CURRENT_LANLNG_PARAM, Parcels.wrap(currentLatLn));
        args.putParcelable(SHOW_RANGE_FINDER, Parcels.wrap(showRangeFinder));
        args.putParcelable(QUESTION_DISTANCE, Parcels.wrap(distance));
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
        showRangeFinder = Parcels.unwrap(arguments.getParcelable
                (SHOW_RANGE_FINDER));

        DISTANCE = Parcels.unwrap(arguments.getParcelable
                (QUESTION_DISTANCE));
        if (showRangeFinder) {
            binding.relCompass.setVisibility(View.VISIBLE);
            binding.tvSkip.setVisibility(View.VISIBLE);
        } else {
            binding.relCompass.setVisibility(View.GONE);
            binding.tvSkip.setVisibility(View.GONE);
        }

        if (arguments != null) {
            List<LocationBoundary> boundaries = Parcels.unwrap(arguments.getParcelable
                    (SHAPE_PARAM));
            LatLng currentLatLng = Parcels.unwrap(arguments.getParcelable(CURRENT_LANLNG_PARAM));

            presenter.createMapData(boundaries, currentLatLng, DISTANCE);
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
        setTypeFace();
        setCompassImage();
        binding.tvSkip.setOnClickListener(this);
    }

    private void setCompassImage() {
        if (Utils.hasCompass(getActivity())) {
            binding.ivCompass.setVisibility(View.VISIBLE);
        } else {
            binding.ivCompass.setVisibility(View.INVISIBLE);
        }
    }

    private void setTypeFace() {
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/museo_sans_500.otf");
        Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/museo_sans_rounded700.otf");
        binding.tvSkip.setTypeface(face);
        binding.tvRangeToSpryte.setTypeface(face);
        binding.tvCompass.setTypeface(face1);
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


    @Override
    public void showInARZone() {
        if (callback != null) {
            callback.onInARZone(presenter.getCurrentLocation());
        }
    }


    @Override
    public void showNoGpsMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(getActivity(), R.style.DialogTheme));
        builder.create();
        builder.setMessage(getResources().getString(R.string.gps_network_not_enabled));
        builder.setPositiveButton(getResources().getString(R.string
                .open_location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(myIntent);
                noGpsDialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                noGpsDialog.dismiss();
            }
        });
        noGpsDialog = builder.create();
        noGpsDialog.show();

    }


    @Override
    public void onChangeDirection(Animation rotateAnimation,Animation animationObject) {
        binding.ivCircle.startAnimation(rotateAnimation);
        binding.ivCompass.startAnimation(animationObject);
    }

    @Override
    public void onChangeDistance(String distance) {
        binding.tvCompass.setText(distance);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSkip:
                if (callback != null) {
                    callback.onSkipQuestion();
                }
                break;
        }
    }

    public interface Callback {
        void onInARZone(Location currentLocation);

        void onInSpryteZone();

        void onSkipQuestion();
    }

    public Location getCurrentLocation() {
        return presenter.getCurrentLocation();
    }

}
