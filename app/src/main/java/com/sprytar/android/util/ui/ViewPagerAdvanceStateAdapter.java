package com.sprytar.android.util.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdvanceStateAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<CharSequence> titles = new ArrayList<>();

    public ViewPagerAdvanceStateAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    public void setData(List<Fragment> fragments, List<CharSequence> titles){
        this.fragments =  fragments;
        this.titles =  titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
}
