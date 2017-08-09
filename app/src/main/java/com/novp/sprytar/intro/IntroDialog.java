package com.novp.sprytar.intro;

import android.app.Dialog;
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

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.DialogIntroBinding;
import com.viewpagerindicator.CirclePageIndicator;


public class IntroDialog extends DialogFragment implements IntroPagerFragment.IntroFragmentCallBack{

    private static final int NUM_PAGES = 4;
    private SparseIntArray layoutsArray;
    private DialogIntroBinding binding;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    int currentPage = 0;

    public static IntroDialog newInstance() {
        IntroDialog fragment = new IntroDialog();
        return fragment;
    }

    public IntroDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        layoutsArray = new SparseIntArray();
        layoutsArray.append(0, R.layout.fragment_intro_page1);
        layoutsArray.append(1, R.layout.fragment_intro_page2);
        layoutsArray.append(2, R.layout.fragment_intro_page3);
        layoutsArray.append(3, R.layout.fragment_intro_page4);

        binding = DialogIntroBinding.inflate(LayoutInflater.from(getContext()), null, false);
//        Rect displayRectangle = new Rect();
//        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

//        view.setMinimumHeight((int)(displayRectangle.height() * 0.9f));
//        view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

//        view.setMinimumHeight(50);
//        view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

        //pager = (ViewPager) view.findViewById(R.id.pager);
        pager = binding.pager;
        pager.setPadding(0,0,0,0);
        CirclePageIndicator pageIndicator = binding.galleryIndicator;

        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), this);
        pager.setAdapter(pagerAdapter);
        pageIndicator.setViewPager(pager);
        binding.tourButtonTextView.setText(getString(R.string.next_button));
        binding.tourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == 3) {
                    dismiss();
                }else if (currentPage == NUM_PAGES-2) {
                    currentPage = pager.getCurrentItem() + 1;
                    binding.tourButtonTextView.setText(getString(R.string.done_button));
                    pager.setCurrentItem(currentPage);
                } else {
                    currentPage = pager.getCurrentItem() + 1;
                    binding.tourButtonTextView.setText(getString(R.string.next_button));
                    pager.setCurrentItem(currentPage);
                }
            }
        });

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Dialog dialog = getDialog();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = display.widthPixels;
        lp.height = (int)(display.heightPixels * 0.9);

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
        binding.tourButtonTextView.setText(getString(R.string.next_button));
        pager.setCurrentItem(currentPage);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        IntroDialog parent;

        public ScreenSlidePagerAdapter(FragmentManager fm, IntroDialog parent) {
            super(fm);
            this.parent = parent;
        }

        @Override
        public Fragment getItem(int position) {
            IntroPagerFragment fragment = IntroPagerFragment.newInstance(position, layoutsArray.get(position));
            fragment.setCallBack(parent);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}