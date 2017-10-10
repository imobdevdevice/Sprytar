package com.sprytar.android.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sprytar.android.R;
import com.sprytar.android.presentation.BaseFragment;

public class IntroPagerFragment extends BaseFragment {

    public static final String ARG_PAGE_NUMBER = "com.sprytar.android.intro.PageNumber";
    public static final String ARG_LAYOUT_ID = "com.sprytar.android.intro.LayoutId";

    private IntroFragmentCallBack callBack;
    int layoutId;
    int pageNumber;

    public static IntroPagerFragment newInstance(int pageNumber, int layoutId) {
        IntroPagerFragment fragment = new IntroPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        pageNumber = bundle.getInt(ARG_PAGE_NUMBER);
        layoutId = bundle.getInt(ARG_LAYOUT_ID);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);

        Button button = (Button) rootView.findViewById(R.id.getStarted_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onStartButtonClick();
                }
            });
        }

        return rootView;
    }

    public void setCallBack(IntroFragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public interface IntroFragmentCallBack {
        void onStartButtonClick();
    }

}
