package com.sprytar.android.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.databinding.FragmentBadgesPagerBinding;
import com.sprytar.android.presentation.BaseFragment;

public class BadgesPagerFragment extends BaseFragment {

    FragmentBadgesPagerBinding binding;

    public static BadgesPagerFragment newInstance() {
        BadgesPagerFragment fragment = new BadgesPagerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBadgesPagerBinding.inflate(inflater, container, false);

        binding.gridview.setAdapter(new BadgeAdapter(this, getActivity()));

        return binding.getRoot();
    }

}
