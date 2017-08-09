package com.novp.sprytar.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.databinding.FragmentQuestionBinding;
import com.novp.sprytar.databinding.FragmentWrongAnswerBinding;
import com.novp.sprytar.presentation.BaseFragment;


public class WrongAnswerFragment extends BaseFragment {
    public static final String QUESTION_PARAM = "com.novp.sprytar.game.QuestionFragment" +
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
