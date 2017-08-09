package com.novp.sprytar.support;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
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
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.databinding.FragmentSupportBinding;
import com.novp.sprytar.databinding.FragmentVenueBinding;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.util.PermissionUtils;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;
import com.novp.sprytar.venue.DaggerVenueComponent;
import com.novp.sprytar.venue.VenueComponent;
import com.novp.sprytar.venue.VenueListAdapter;
import com.novp.sprytar.venue.VenuePresenter;
import com.novp.sprytar.venue.VenueView;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

public class SupportFragment extends BaseFragment implements SupportView {


    @SuppressWarnings("WeakerAccess")
    @Inject
    SupportPresenter presenter;


    private FragmentSupportBinding binding;

    public SupportFragment() {
    }

    public static SupportFragment newInstance() {
        SupportFragment fragment = new SupportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createSupportComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentSupportBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.support);

        initUi();

        return binding.getRoot();
    }

    private void initUi() {

        binding.faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaqActivity.start(getActivity());
            }
        });

        binding.contactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactInformationActivity.start(getActivity());
            }
        });

        binding.sendRequestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportRequestActivity.start(getActivity());
            }
        });

    }

    @Override
    public void setWidgetVisibility(boolean isLogedIn) {

        binding.sendRequestTextView.setVisibility(isLogedIn ? View.VISIBLE : View.GONE);
        binding.divider4.setVisibility(isLogedIn ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        presenter.initData();
    }

    private SupportComponent createSupportComponent() {
        return DaggerSupportComponent.builder()
                .sessionComponent(getSessionComponent())
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
