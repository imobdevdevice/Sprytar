package com.novp.sprytar.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novp.sprytar.databinding.FragmentQuestionBinding;
import com.novp.sprytar.presentation.BaseFragment;


public class CorrectAnswerFragment extends BaseFragment {
    public static final String QUESTION_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".questionParam";

    FragmentQuestionBinding binding;

    public static CorrectAnswerFragment newInstance(String text) {
        Bundle args = new Bundle();

        args.putString(QUESTION_PARAM, text);
        //args.putParcelable(QUESTION_PARAM, Parcels.wrap(question));
        CorrectAnswerFragment fragment = new CorrectAnswerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentQuestionBinding.inflate(inflater, container, false);

        //Location location = Parcels.unwrap(getArguments().getParcelable(LOCATION_PARAM));
        if (getArguments() != null) {
            String text = getArguments().getString(QUESTION_PARAM, "");
            binding.questionTextView.setText(text);
        }

        return binding.getRoot();

    }
}
