package com.novp.sprytar.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.databinding.FragmentGamesPagerBinding;
import com.novp.sprytar.presentation.BaseFragment;

public class GamesPagerFragment extends BaseFragment{

    FragmentGamesPagerBinding binding;

    public static GamesPagerFragment newInstance() {
        GamesPagerFragment fragment = new GamesPagerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGamesPagerBinding.inflate(inflater, container, false);

        //binding.gridview.setAdapter(new VisitedVenuesAdapter(this, getActivity()));

        return binding.getRoot();
    }

}
