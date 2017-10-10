package com.sprytar.android.game;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityCorrectAnswerImageBinding;
import com.sprytar.android.SprytarApplication;

import org.parceler.Parcels;

public class CorrectAnswerImageActivity extends AppCompatActivity {

  public static final String URI_EXTRA = "com.sprytar.android.game.UriExtra";
  public static final String CORRECT_EXTRA = "com.sprytar.android.game.CorrectExtra";

  private Uri uri;
  private boolean correct;
  private Tracker mTracker;
  ActivityCorrectAnswerImageBinding binding;

  public static void startForResult(Activity activity, int requestCode, Uri uri, boolean correct) {
    Intent starter = new Intent(activity, CorrectAnswerImageActivity.class);
    starter.putExtra(URI_EXTRA, Parcels.wrap(uri));
    starter.putExtra(CORRECT_EXTRA, correct);

    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_correct_answer_image);

    uri = Parcels.unwrap(getIntent().getParcelableExtra(URI_EXTRA));
    correct = getIntent().getBooleanExtra(CORRECT_EXTRA, false);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Correct Answer Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());

    initUi();
  }

  private void initUi() {

    //        setSupportActionBar(binding.toolbar);
    //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //        getSupportActionBar().setTitle(getString(R.string.game_finished_title));

    if (correct) {
      binding.correctCharacterImageView.setVisibility(View.VISIBLE);
      binding.wrongCharacterImageView.setVisibility(View.GONE);
    } else {
      binding.correctCharacterImageView.setVisibility(View.GONE);
      binding.wrongCharacterImageView.setVisibility(View.VISIBLE);
    }

    Uri loadingGif = Uri.parse("res:///" + R.drawable.spryer_preloader256);

    SimpleDraweeView draweeView = binding.photoImageView;

    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());

    GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP).build();

    DraweeController controller =
        Fresco.newDraweeControllerBuilder().setUri(uri).setOldController(binding.photoImageView.getController()).setAutoPlayAnimations(true).build();
    binding.photoImageView.setController(controller);
    //binding.correctCharacterImageView.setVisibility(View.VISIBLE);

    //        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder
    //                (getResources());
    //
    //        GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils
    //                .ScaleType.CENTER_INSIDE).build();
    //
    //        DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(loadingGif)
    //                .setAutoPlayAnimations(true).build();
    //
    //        draweeView.setHierarchy(hierarchy);
    //        draweeView.setController(controller);

    //        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    //        final ScheduledFuture<?> scheduledFuture =executorService.scheduleAtFixedRate(new Runnable
    //                () {
    //            @Override
    //            public void run() {
    //                runOnUiThread(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder
    //                                (getResources());
    //
    //                        GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils
    //                                .ScaleType.CENTER_CROP).build();
    //
    //                        DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri)
    //                                .setOldController(binding.photoImageView.getController())
    //                                .setAutoPlayAnimations(true).build();
    //                        binding.photoImageView.setController(controller);
    //                        binding.correctCharacterImageView.setVisibility(View.VISIBLE);
    //                        executorService.shutdown();
    //                    }
    //                });
    //
    //            }
    //        }, 5, 1, TimeUnit.SECONDS);
  }

  @Override public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onPostCreate(savedInstanceState, persistentState);
  }

  @Override public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  public void onNextQuestionClick(View view) {
    Intent data = new Intent();
    setResult(RESULT_OK, data);
    finish();
  }
}
