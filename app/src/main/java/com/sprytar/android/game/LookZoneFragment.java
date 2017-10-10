package com.sprytar.android.game;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentLookZoneBinding;
import com.sprytar.android.data.model.Answer;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.game.quiz.AnswerView;
import com.sprytar.android.presentation.BaseFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class LookZoneFragment extends BaseFragment implements View.OnClickListener {
    public static final String QUESTION_PARAM = "com.sprytar.android.game.QuestionFragment" +
            ".questionParam";
    public static final String IS_LAST_QUESTION = "is_last_question";
    private Question question;
    private FragmentLookZoneBinding binding;
    private ImageView ivBack, ivInfo;
    private boolean isLastQuestion = false;
    private List<com.sprytar.android.game.quiz.AnswerView> answerViews;
    private String correctAnswer = "";

    public interface Callback {
        void onReadyToAnswerClick();

        void onNextQuestion();

        void onSkipQuestion();
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static LookZoneFragment newInstance(Question question) {
        LookZoneFragment fragment = new LookZoneFragment();
        Bundle args = new Bundle();

        args.putParcelable(QUESTION_PARAM, Parcels.wrap(question));
        fragment.setArguments(args);
        return fragment;
    }

    public static LookZoneFragment newInstance(Question question, boolean isLastQuestion) {
        LookZoneFragment fragment = new LookZoneFragment();
        Bundle args = new Bundle();

        args.putParcelable(QUESTION_PARAM, Parcels.wrap(question));
        args.putParcelable(IS_LAST_QUESTION, Parcels.wrap(isLastQuestion));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        try {
            Bundle arguments = getArguments();
            binding = FragmentLookZoneBinding.inflate(inflater, container,
                    false);
            question = Parcels.unwrap(arguments.getParcelable
                    (QUESTION_PARAM));
            isLastQuestion = Parcels.unwrap(arguments.getParcelable
                    (IS_LAST_QUESTION));

            ivInfo = (ImageView) getActivity().findViewById(R.id.ivInfo);
            ivBack = (ImageView) getActivity().findViewById(R.id.back_arrow);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUi();
        binding.ivClose.setOnClickListener(this);
        binding.tvHint.setOnClickListener(this);
        binding.tvReadyToAnswer.setOnClickListener(this);
        binding.resultView.tvNextQuestion.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);

    }

    private void initUi() {
        ivBack.setVisibility(View.VISIBLE);
        ivInfo.setVisibility(View.VISIBLE);
        setTypeFace();
        binding.tvQuestionText.setText(question.getText());
        binding.tvHintText.setText(question.getHint());
        if (isLastQuestion) binding.resultView.tvNextQuestion.setText("Finish");
        if (answerViews == null) {
            answerViews = new ArrayList<>();
        }

        answerViews.clear();

        for (int i = 0; i < question.getAnswers().size(); i++) {
            AnswerView answerView = new AnswerView(getContext());
            answerView.setAnswer(question.getAnswers().get(i));
            answerView.setCallback(answerCallback);
            answerViews.add(answerView);
            binding.answersLayout.addView(answerViews.get(i));
        }

        getCorrectAnswer();

    }

    private void setTypeFace() {
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/museo_sans_rounded700.otf");
        Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/museo_sans_500.otf");
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/cavet_brush_regular.ttf");

        binding.tvQuestion.setTypeface(face);
        binding.tvQuestionText.setTypeface(face1);
        binding.tvHintTitle.setTypeface(face);
        binding.tvHintText.setTypeface(face1);

        binding.resultView.tvAnswer.setTypeface(face1);
        binding.resultView.tvMessage.setTypeface(face1);
        binding.resultView.tvResult.setTypeface(face2);
        binding.resultView.tvAnswerDescr.setTypeface(face);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivClose:
                binding.relHint.setVisibility(View.GONE);
                break;
            case R.id.tvHint:
                binding.relHint.setVisibility(View.VISIBLE);
                break;
            case R.id.tvReadyToAnswer:
                binding.relContainer.setVisibility(View.GONE);
                binding.relOptions.setVisibility(View.VISIBLE);
                break;
            case R.id.tvNextQuestion:
                callback.onNextQuestion();
                break;
            case R.id.tvSkip:
                setNextButtonLogic(null, false);
                if (callback != null) {
                    callback.onNextQuestion();
                }
                break;

        }


    }

    private AnswerView.AnswerViewCallback answerCallback = new AnswerView.AnswerViewCallback() {
        @Override
        public void onCorrectAnswer(Answer answer) {
            question.setAnsweredCorrectly(true);
            setNextButtonLogic(answer, true);
            binding.resultView.rootLayout.setVisibility(View.VISIBLE);
            binding.resultView.ivResult.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.correct));
            binding.resultView.tvAnswerDescr.setText(question.getCorrectFeedback());
            binding.resultView.tvMessage.setText(getString(R.string.well_done_you_are));
            binding.resultView.tvResult.setText("CORRECT");
            binding.resultView.tvAnswer.setText(correctAnswer);
            ivInfo.setVisibility(View.GONE);
            ivBack.setVisibility(View.GONE);

        }

        @Override
        public void onIncorrectAnswer(Answer answer) {
            question.setAnsweredCorrectly(false);
            setNextButtonLogic(answer, false);
            binding.resultView.rootLayout.setVisibility(View.VISIBLE);
            binding.resultView.ivResult.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.wrong));
            binding.resultView.tvAnswerDescr.setText(question.getCorrectFeedback());
            binding.resultView.tvMessage.setText(getString(R.string.sorry_you_are));
            binding.resultView.tvResult.setText("WRONG");
            binding.resultView.tvAnswer.setText(correctAnswer);
            ivInfo.setVisibility(View.GONE);
            ivBack.setVisibility(View.GONE);

        }

        @Override
        public void onSkipQuestion() {

        }
    };

    private void setNextButtonLogic(Answer answer, boolean isCorrect) {

        for (int i = 0; i < answerViews.size(); i++) {
            answerViews.get(i).removeListeners();

            if (answer != null && answerViews.get(i).getAnswer() != null) {
                if (answerViews.get(i).getAnswer().equals(answer)) {
                    if (isCorrect) {
                        answerViews.get(i).changeGreenColor();
                    }
                } else {
                    answerViews.get(i).changeGreyColor();
                }
            }

            if (i == answerViews.size() - 1) {
                answerViews.get(i).setVisibility(View.GONE);
            }


        }
    }

    private void getCorrectAnswer() {
        for (AnswerView answerView : answerViews) {
            if (answerView.getAnswer().isAnswerCorrect()) {
                correctAnswer = answerView.getAnswer().getText();
            }
        }


    }
}