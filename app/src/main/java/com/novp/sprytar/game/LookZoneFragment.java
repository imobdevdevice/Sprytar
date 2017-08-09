package com.novp.sprytar.game;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.novp.sprytar.R;
import com.novp.sprytar.databinding.FragmentLookZoneBinding;
import com.novp.sprytar.presentation.BaseFragment;

import java.util.ArrayList;
import java.util.List;


public class LookZoneFragment extends BaseFragment {
    public static final String QUESTION_PARAM = "com.novp.sprytar.game.QuestionFragment" +
            ".questionParam";

    public interface Callback {
        void onReadyToAnswerClick();
    }
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static LookZoneFragment newInstance() {
        LookZoneFragment fragment = new LookZoneFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        FragmentLookZoneBinding binding = FragmentLookZoneBinding.inflate(inflater, container,
                false);

        binding.getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onReadyToAnswerClick();
            }
        });

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes1))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes1ImageView.getController())
                .build();
        binding.eyes1ImageView.setController(controller);

        DraweeController controller2 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes3))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes2ImageView.getController())
                .build();
        binding.eyes2ImageView.setController(controller2);

        DraweeController controller3 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes4))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes3ImageView.getController())
                .build();
        binding.eyes3ImageView.setController(controller3);

        DraweeController controller4 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes5))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes21ImageView.getController())
                .build();
        binding.eyes21ImageView.setController(controller);

        DraweeController controller5 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes5))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes22ImageView.getController())
                .build();
        binding.eyes22ImageView.setController(controller5);

        DraweeController controller6 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes2))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes31ImageView.getController())
                .build();
        binding.eyes31ImageView.setController(controller6);

        DraweeController controller7 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes4))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes32ImageView.getController())
                .build();
        binding.eyes32ImageView.setController(controller7);

        DraweeController controller8 = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + R.drawable.eyes2))
                .setAutoPlayAnimations(true)
                .setOldController(binding.eyes33ImageView.getController())
                .build();
        binding.eyes33ImageView.setController(controller8);

        return binding.getRoot();

    }


}
