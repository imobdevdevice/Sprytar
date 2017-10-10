package com.sprytar.android.game.treasureHuntIntro;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogTreasureIntroBinding;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Manoj Singh
 */

public class DialogTreasureHuntIntro extends DialogFragment implements TreasureHuntIntroPagerFragment.TreasureHuntIntroFragmentCallBack {

    private static final int NUM_PAGES = 5;
    private SparseIntArray layoutsArray;
    private DialogTreasureIntroBinding binding;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    int currentPage = 0;
    private static TreasureHuntDialogListner treasureHuntListner;

    public static DialogTreasureHuntIntro newInstance(TreasureHuntDialogListner treasureHunt) {
        DialogTreasureHuntIntro fragment = new DialogTreasureHuntIntro();
        treasureHuntListner = treasureHunt;
        return fragment;
    }

    public static DialogTreasureHuntIntro newInstance() {
        DialogTreasureHuntIntro fragment = new DialogTreasureHuntIntro();
        return fragment;
    }


    public DialogTreasureHuntIntro() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        layoutsArray = new SparseIntArray();
        layoutsArray.append(0, R.layout.fragment_treasurehunt_intro1);
        layoutsArray.append(1, R.layout.fragment_treasurehunt_intro2);
        layoutsArray.append(2, R.layout.fragment_treasurehunt_intro3);
        layoutsArray.append(3, R.layout.fragment_treasurehunt_intro4);
        layoutsArray.append(4, R.layout.fragment_treasurehunt_intro5);

        binding = DialogTreasureIntroBinding.inflate(LayoutInflater.from(getContext()), null, false);
        pager = binding.pager;
        pager.setPadding(0, 0, 0, 0);
        CirclePageIndicator pageIndicator = binding.galleryIndicator;

        pagerAdapter = new DialogTreasureHuntIntro.ScreenSlidePagerAdapter(getChildFragmentManager(), this);
        pager.setAdapter(pagerAdapter);
        pageIndicator.setViewPager(pager);

        Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/museo.ttf");
        Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/custom_font.ttf");

        binding.tourButtonTextView.setTypeface(face);
        binding.closeTextView.setTypeface(face1);

        binding.closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.tourButtonTextView.setVisibility(View.GONE);
        binding.tourButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (treasureHuntListner != null) {
                    treasureHuntListner.onDialogDismiss();
                }
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 4) {
                    binding.tourButtonTextView.setVisibility(View.GONE);
                } else {
                    binding.tourButtonTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Dialog dialog = getDialog();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = display.widthPixels;
        lp.height = (int) (display.heightPixels * 0.9);

        dialog.getWindow().setAttributes(lp);

        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStartButtonClick() {
        currentPage = 0;
        pager.setCurrentItem(currentPage);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        DialogTreasureHuntIntro parent;

        public ScreenSlidePagerAdapter(FragmentManager fm, DialogTreasureHuntIntro parent) {
            super(fm);
            this.parent = parent;
        }

        @Override
        public Fragment getItem(int position) {
            TreasureHuntIntroPagerFragment fragment = TreasureHuntIntroPagerFragment.newInstance(position, layoutsArray.get(position));
            fragment.setCallBack(parent);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
