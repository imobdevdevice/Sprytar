package com.novp.sprytar.game.trails;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.EarnedBadge;
import com.novp.sprytar.data.model.ProfileEarnedBadges;
import com.novp.sprytar.data.model.SubTrail;
import com.novp.sprytar.data.model.Trail;
import com.novp.sprytar.databinding.ActivityTrailsGameFinishedBinding;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.profile.BadgeDialog;
import com.novp.sprytar.util.BadgeUtils;
import java.util.Arrays;
import java.util.List;
import org.parceler.Parcels;

import static com.novp.sprytar.profile.BadgeDialog.getDialog;

public class TrailsGameFinishedActivity extends BaseActivity implements View.OnClickListener {

  public static final String TRAIL_EXTRA = "com.novp.sprytar.data.model.TrailExtra";
  public static final String SUBTRAIL_EXTRA = "com.novp.sprytar.data.model.SubtrailExtra";
  public static final String EARNED_BADGE_EXTRA = "com.novp.sprytar.data.model.EarnedBadgeExtra";

  public static void start(Context context, Trail trail, SubTrail subTrail, EarnedBadge earnedBadge) {
    Intent starter = new Intent(context, TrailsGameFinishedActivity.class);
    starter.putExtra(TRAIL_EXTRA, trail);
    starter.putExtra(SUBTRAIL_EXTRA, subTrail);
    starter.putExtra(EARNED_BADGE_EXTRA, Parcels.wrap(earnedBadge));

    context.startActivity(starter);
  }

  private BadgeDialog.BadgeDialogListener badgeDialogListener = new BadgeDialog.BadgeDialogListener() {
    @Override public void onClose() {
      badgeDialog.dismiss();
    }
  };

  private ActivityTrailsGameFinishedBinding binding;
  private EarnedBadge badge;
  private Trail trail;
  private SubTrail subTrail;
  private AlertDialog badgeDialog = null;
  private CallbackManager callbackManager;
  private Tracker mTracker;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_trails_game_finished);

    Intent intent = getIntent();
    badge = Parcels.unwrap(intent.getParcelableExtra(EARNED_BADGE_EXTRA));
    trail = (Trail) intent.getSerializableExtra(TRAIL_EXTRA);
    subTrail = (SubTrail) intent.getSerializableExtra(SUBTRAIL_EXTRA);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Trails game Finish Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
  }

  private void initUi() {
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.game_finished_title));

    if (badge != null) {

      binding.distanceTextView.setImageDrawable(getResources().getDrawable(BadgeUtils.getBadgeResource(badge.getBadgeName())));

      setListeners();
    }
  }

  private void shareToFacebookApi(Bitmap bitmap) {
    FacebookSdk.sdkInitialize(getApplicationContext());

    callbackManager = CallbackManager.Factory.create();

    String caption = "I just completed " + subTrail.getName() + "Trails Game.";

    List<String> permissionNeeds = Arrays.asList("publish_actions");

    //this loginManager helps you eliminate adding a LoginButton to your UI
    LoginManager loginManager = LoginManager.getInstance();
    loginManager.logInWithPublishPermissions(this, permissionNeeds);

    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap)
            //.setImageUrl(Uri.parse(location.getImageLink()))
            .setCaption(caption).build();

        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

        ShareDialog facebookDialog = new ShareDialog(TrailsGameFinishedActivity.this);
        facebookDialog.show(content);
      }

      @Override public void onCancel() {
        System.out.println("onCancel");
      }

      @Override public void onError(FacebookException exception) {
        System.out.println("onError");
      }
    });
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

  private void sharePhotoFb() {
    Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    shareToFacebookApi(image);
  }

  private ProfileEarnedBadges getProfileEarnedObject() {
    if (badge != null) {
      ProfileEarnedBadges earnedBadge = new ProfileEarnedBadges();
      earnedBadge.setName(badge.getBadgeName());
      earnedBadge.setIcon(badge.getBadgeIcon());
      earnedBadge.setDescription(badge.getBadgeDescription());

      return earnedBadge;
    } else {
      return null;
    }
  }

  private void setListeners() {
    binding.distanceTextView.setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    int id = view.getId();
    if (id == binding.distanceTextView.getId()) {
      badgeDialog = getDialog(this, badgeDialogListener, getProfileEarnedObject(), R.drawable.ic_game_active_24dp);
    }

    badgeDialog.show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}
