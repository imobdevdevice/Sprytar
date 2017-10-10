package com.sprytar.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentSettingsBinding;
import com.sprytar.android.settings.DaggerSettingsComponent;
import com.sprytar.android.main.TcActivity;
import com.sprytar.android.presentation.BaseFragment;

import javax.inject.Inject;

public class SettingsFragment extends BaseFragment implements SettingsView {


    @SuppressWarnings("WeakerAccess")
    @Inject
    SettingsPresenter presenter;


    private FragmentSettingsBinding binding;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.settings);

        /*
        binding.childrenTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyMemberActivity.start(getActivity(), false);
            }
        });
        */

        binding.accountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAccountSettingsClick();
            }
        });

        binding.termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TcActivity.class);
                getActivity().startActivity(i);
            }
        });

//        binding.sendRequestTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SupportRequestActivity.start(getActivity());
//            }
//        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
    }

    private SettingsComponent createComponent() {
        return DaggerSettingsComponent.builder().applicationComponent(getApplicationComponent())
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

    @Override
    public void showAccountSettingsUi() {
        AccountSettingsDialog fragment = AccountSettingsDialog.newInstance();

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }
}
