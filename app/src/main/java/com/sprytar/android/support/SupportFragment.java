package com.sprytar.android.support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentSupportBinding;
import com.sprytar.android.support.DaggerSupportComponent;
import com.sprytar.android.presentation.BaseFragment;

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
