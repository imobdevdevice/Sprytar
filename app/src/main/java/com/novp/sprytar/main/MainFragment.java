package com.novp.sprytar.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.FragmentMainBinding;
import com.novp.sprytar.presentation.BaseFragment;

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
