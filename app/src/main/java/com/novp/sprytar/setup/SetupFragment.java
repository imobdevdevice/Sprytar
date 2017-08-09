package com.novp.sprytar.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.databinding.FragmentSetupBinding;
import com.novp.sprytar.location.LocationModule;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.util.ui.SimpleDividerDecoration;
import com.novp.sprytar.venuesetup.VenueSetupFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SetupFragment extends BaseFragment implements SetupView {

    @SuppressWarnings("WeakerAccess")
    @Inject
    SetupPresenter presenter;

    @Inject
    LocationSetupAdapter adapter;

    private final BaseBindingAdapter.ItemClickListener<Location> locationClickListener = new
            BaseBindingAdapter.ItemClickListener<Location>() {
                @Override
                public void onClick(Location item, int position) {
                    VenueSetupFragment fragment = VenueSetupFragment.newInstance(item);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    getFragmentManager().beginTransaction().replace(R.id.content, fragment)
                            .commit();

                }
            };


    private FragmentSetupBinding binding;

    public SetupFragment() {
    }

    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentSetupBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.setup);

        initUi();

        return binding.getRoot();

    }

    private void initUi() {

        adapter.setItemClickListener(locationClickListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        binding.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.itemsRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        presenter.loadData();
    }

    private SetupComponent createComponent() {
        return DaggerSetupComponent.builder()
                .sessionComponent(getSessionComponent())
                .locationModule(new LocationModule())
                .build();
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoadingIndicator() {

    }

    @Override
    public void hideLoadingIndicator() {

    }

    @Override
    public void showItems(List<Location> items) {
        adapter.setItems(items);
    }

    @Override
    public void clearItems() {

    }

    @Override
    public void showMessage(String text) {

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
