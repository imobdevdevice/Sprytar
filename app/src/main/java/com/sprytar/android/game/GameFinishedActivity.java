package com.sprytar.android.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityGameFinishedBinding;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.profile.BadgeDialog;
import com.sprytar.android.util.BadgeUtils;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.sprytar.android.profile.BadgeDialog.getDialog;

public class GameFinishedActivity extends AppCompatActivity implements View.OnClickListener {

  public static final String EARNED_BADGE_EXTRA = "com.sprytar.android.data.model.EarnedBadgeExtra";
  public static final String CORRECT_ANSWERS_EXTRA = "com.sprytar.android.data.model.CorrectAnswers";
  public static final String NUM_OF_QUESTIONS = "com.sprytar.android.data.model.NumOfQuestions";
  public static final String LOCATION_NAME_EXTRA = "location_name_extra";
  public static final String IMAGE_URL_EXTRA = "image_url_extra";

  ActivityGameFinishedBinding binding;
  private Tracker mTracker;
  private AlertDialog badgeDialog = null;
  private AlertDialog shareBadgeDialog = null;

  private EarnedBadge badge;
  private String locationName;
  private String locationImageUrl;

  private CallbackManager callbackManager;
  private LoginManager loginManager;

  DataSource<CloseableReference<CloseableImage>> dataSource;

  private BadgeDialog.BadgeDialogListener badgeDialogListener = new BadgeDialog.BadgeDialogListener() {
    @Override public void onClose() {
      badgeDialog.dismiss();
    }
  };

  private void shareToFacebook() {

    try {
      Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
      if (intent != null) {
        // The application exists
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.facebook.katana");
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://sprytar.com");
        startActivity(shareIntent);
      }
    } catch (Exception e) {
      Log.v("test_tag", e.getMessage());
    }
  }

  /**
   * public Uri getLocalBitmapUri(ImageView imageView) {
   * // Extract Bitmap from ImageView drawable
   * Drawable drawable = imageView.getDrawable();
   * Bitmap bmp = null;
   * if (drawable instanceof BitmapDrawable){
   * bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
   * } else {
   * return null;
   * }
   * // Store image to default external storage directory
   * Uri bmpUri = null;
   * try {
   * File file =  new File(Environment.getExternalStoragePublicDirectory(
   * Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
   * file.getParentFile().mkdirs();
   * FileOutputStream out = new FileOutputStream(file);
   * bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
   * out.close();
   * bmpUri = Uri.fromFile(file);
   * } catch (IOException e) {
   * e.printStackTrace();
   * }
   * return bmpUri;
   * }
   */

  public static void startForResult(Activity activity, int requestCode, EarnedBadge badge, String locationName, String locationImageUrl,
                                    int correctAnswers, int numOfQuestions) {
    Intent starter = new Intent(activity, GameFinishedActivity.class);
    starter.putExtra(EARNED_BADGE_EXTRA, Parcels.wrap(badge));
    starter.putExtra(CORRECT_ANSWERS_EXTRA, correctAnswers);
    starter.putExtra(NUM_OF_QUESTIONS, numOfQuestions);
    starter.putExtra(LOCATION_NAME_EXTRA, locationName);
    starter.putExtra(IMAGE_URL_EXTRA, locationImageUrl);
    activity.startActivityForResult(starter, requestCode);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_game_finished);

    Intent intent = getIntent();
    badge = Parcels.unwrap(intent.getParcelableExtra(EARNED_BADGE_EXTRA));
    locationName = intent.getStringExtra(LOCATION_NAME_EXTRA);
    locationImageUrl = intent.getStringExtra(IMAGE_URL_EXTRA);
    int correctAnswers = intent.getIntExtra(CORRECT_ANSWERS_EXTRA, 0);
    int numOfQuestions = intent.getIntExtra(NUM_OF_QUESTIONS, 0);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Game Finish Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
    setQuestionsData(correctAnswers, numOfQuestions);
  }

  private void setQuestionsData(int correctAnswers, int numOfQuestions) {
    if (numOfQuestions > 0) {
      binding.scoreTextView.setText("You scored " + correctAnswers + " out of " + numOfQuestions);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (dataSource != null) {
      dataSource.close();
    }
  }

  private void initUi() {
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.game_finished_title));

    if (badge != null) {
      binding.distanceTextView.setImageDrawable(getResources().getDrawable(BadgeUtils.getBadgeResource(badge.getBadgeIcon())));
      setListeners();
    }
  }

  private void setListeners() {
    binding.distanceTextView.setOnClickListener(this);
  }

  @Override public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  public void onFinishedClick(View view) {
    onBackPressed();
    finish();
  }

  public void onShareToFB(View view) {
    sharePhotoFb();
  }

  private ProfileEarnedBadges getProfileEarnedObject() {
    ProfileEarnedBadges earnedBadge = new ProfileEarnedBadges();
    earnedBadge.setName(badge.getBadgeName());
    earnedBadge.setIcon(badge.getBadgeIcon());
    earnedBadge.setDescription(badge.getBadgeDescription());

    return earnedBadge;
  }

  private void sharePhotoFb() {
    String imageLink = locationImageUrl;
    if (imageLink != null && !imageLink.isEmpty()) {
      ImagePipeline imagePipeline = Fresco.getImagePipeline();

      ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageLink))
          .setRequestPriority(Priority.HIGH)
          .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
          .build();

      dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);

      try {
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
          @Override public void onNewResultImpl(@Nullable Bitmap bitmap) {

            if (bitmap == null) {
              Timber.d("Bitmap data source returned success, but bitmap null.");
              return;
            }

            shareToFacebookApi(bitmap);
          }

          @Override public void onFailureImpl(DataSource dataSource) {
            Timber.d(dataSource.getFailureCause().getLocalizedMessage());
          }
        }, CallerThreadExecutor.getInstance());
      } catch (Exception ex) {
        Timber.d(ex.getLocalizedMessage());
      }
    } else {
      Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
      shareToFacebookApi(image);
    }
  }

  @Override public void onClick(View view) {

    if (badge == null) {
      return;
    }
    int id = view.getId();
    if (id == binding.distanceTextView.getId()) {
      badgeDialog = getDialog(this, badgeDialogListener, getProfileEarnedObject(), R.drawable.ic_game_active_24dp);
    }
    badgeDialog.show();
  }

  private void shareToFacebookApi(Bitmap bitmap) {
    FacebookSdk.sdkInitialize(getApplicationContext());

    callbackManager = CallbackManager.Factory.create();

    String caption = badge == null ? locationName : badge.getBadgeName();

    List<String> permissionNeeds = Arrays.asList("publish_actions");

    //this loginManager helps you eliminate adding a LoginButton to your UI
    loginManager = LoginManager.getInstance();
    loginManager.logInWithPublishPermissions(this, permissionNeeds);

    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap)
            //.setImageUrl(Uri.parse(location.getImageLink()))
            .setCaption(caption).build();

        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

        ShareDialog facebookDialog = new ShareDialog(GameFinishedActivity.this);
        facebookDialog.show(content);
      }

      @Override public void onCancel() {
        Timber.v("Facebook register call bac -onCancel");
      }

      @Override public void onError(FacebookException exception) {
        Timber.d(exception.getLocalizedMessage());
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}
