package com.sprytar.android.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.databinding.FragmentMainBinding;
import com.sprytar.android.presentation.BaseFragment;

public class MainFragment extends BaseFragment {
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
