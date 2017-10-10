package com.sprytar.android.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprytar.android.databinding.FragmentWrongAnswerBinding;
import com.sprytar.android.presentation.BaseFragment;


public class WrongAnswerFragment extends BaseFragment {
    public static final String QUESTION_PARAM = "com.sprytar.android.game.QuestionFragment" +
            ".questionParam";

    public static WrongAnswerFragment newInstance() {
        WrongAnswerFragment fragment = new WrongAnswerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        FragmentWrongAnswerBinding binding = FragmentWrongAnswerBinding.inflate(inflater, container,
                false);

        return binding.getRoot();

    }
}
