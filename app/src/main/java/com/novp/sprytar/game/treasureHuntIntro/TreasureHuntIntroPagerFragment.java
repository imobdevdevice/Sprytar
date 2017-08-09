package com.novp.sprytar.game.treasureHuntIntro;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.novp.sprytar.R;
import com.novp.sprytar.presentation.BaseFragment;
import com.novp.sprytar.support.FaqActivity;

/**
 * Created by Manoj Singh
 */

public class TreasureHuntIntroPagerFragment extends BaseFragment {

    public static final String ARG_PAGE_NUMBER = "com.novp.sprytar.game.treasureHuntIntro.PageNumber";
    public static final String ARG_LAYOUT_ID = "com.novp.sprytar.game.treasureHuntIntro.LayoutId";

    private DialogTreasureHuntIntro callBack;
    int layoutId;
    int pageNumber;

    public static TreasureHuntIntroPagerFragment newInstance(int pageNumber, int layoutId) {

        TreasureHuntIntroPagerFragment fragment = new TreasureHuntIntroPagerFragment();
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView introTitle = (TextView) view.findViewById(R.id.treasurehunt_intro_title);
        TextView desTv = (TextView) view.findViewById(R.id.treasurehunt_intro_des);

        if (pageNumber == 0) {

            String str = getString(R.string.treasurehunt_intro_1);
            desTv.setMovementMethod(LinkMovementMethod.getInstance());

            SpannableStringBuilder spannableStr = new SpannableStringBuilder();
            MyClickableSpan link = new MyClickableSpan();
            spannableStr.append(str);

            int startIndex = str.indexOf("more");

            spannableStr.setSpan(link, startIndex, startIndex + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            desTv.setText(spannableStr);
            desTv.setHighlightColor(Color.TRANSPARENT);

        }

        try {
            Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/custom_font.ttf");
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/museo.ttf");
            introTitle.setTypeface(face1);
            if (desTv != null) desTv.setTypeface(face);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button button = (Button) view.findViewById(R.id.getStarted_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onStartButtonClick();
                }
            });
        }
    }

    public void setCallBack(DialogTreasureHuntIntro callBack) {
        this.callBack = callBack;
    }


    public interface TreasureHuntIntroFragmentCallBack {
        void onStartButtonClick();
    }

    class MyClickableSpan extends ClickableSpan {


        public MyClickableSpan() {
        }

        @Override
        public void onClick(View widget) {
//            Toast.makeText(getActivity(), "Hi", Toast.LENGTH_SHORT).show();

            FaqActivity.start(getActivity());
//            getActivity().finish();
        }

        @Override
        public void updateDrawState(TextPaint ds) {// override updateDrawState
            ds.setUnderlineText(false);
            ds.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));// set to false to remove underline
        }

    }

}
