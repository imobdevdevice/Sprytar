package com.novp.sprytar.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.databinding.FragmentPointsPagerBinding;
import com.novp.sprytar.presentation.BaseFragment;

public class PointsPagerFragment extends BaseFragment{

    FragmentPointsPagerBinding binding;

    public static PointsPagerFragment newInstance() {
        PointsPagerFragment fragment = new PointsPagerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPointsPagerBinding.inflate(inflater, container, false);

        //binding.gridview.setAdapter(new VisitedVenuesAdapter(this, getActivity()));

        return binding.getRoot();
    }

}
