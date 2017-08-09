package com.novp.sprytar.game;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.databinding.FragmentQuestionBinding;
import com.novp.sprytar.presentation.BaseFragment;

import org.parceler.Parcels;


public class QuestionFragment extends BaseFragment {
    public static final String QUESTION_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".questionParam";

    public static final String SHOW_ICON_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".showIconParam";

    public static final String IS_IMAGE_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".isImageQuestion";

    public static final String IMAGE_PATH_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".imagePath";

    FragmentQuestionBinding binding;

    public static QuestionFragment newInstance(String text, boolean showIcon, boolean
            isImageQuestion, Uri imagePath) {
        Bundle args = new Bundle();

        args.putString(QUESTION_PARAM, text);
        args.putBoolean(SHOW_ICON_PARAM, showIcon);
        args.putBoolean(IS_IMAGE_PARAM, isImageQuestion);
        args.putParcelable(IMAGE_PATH_PARAM, Parcels.wrap(imagePath));
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        binding = FragmentQuestionBinding.inflate(inflater, container, false);


        if (getArguments() != null) {
            String text = getArguments().getString(QUESTION_PARAM, "");
            boolean showIcon = getArguments().getBoolean(SHOW_ICON_PARAM, true);
            boolean isImageQuestion = getArguments().getBoolean(IS_IMAGE_PARAM, true);
            Uri imagePath = Parcels.unwrap(getArguments().getParcelable(IMAGE_PATH_PARAM));

            binding.questionTextView.setText(text);
            if (isImageQuestion) {
                binding.logoImageView.setVisibility(View.GONE);
                binding.questionImageView.setVisibility(View.VISIBLE);

                DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(imagePath)
                        .setAutoPlayAnimations(true).build();
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder
                        (getResources());
                GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils
                        .ScaleType.FIT_CENTER).build();
                binding.questionImageView.setHierarchy(hierarchy);
                binding.questionImageView.setController(controller);

            } else {
                if (showIcon) {
                    binding.logoImageView.setVisibility(View.VISIBLE);
                } else {
                    binding.logoImageView.setVisibility(View.GONE);
                }
            }
        }

        return binding.getRoot();

    }
}
