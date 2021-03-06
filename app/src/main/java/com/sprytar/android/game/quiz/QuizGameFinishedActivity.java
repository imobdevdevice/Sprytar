package com.sprytar.android.game.quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
import com.sprytar.android.R;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.Location;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.databinding.ActivityQuizGameFinishedBinding;
import com.sprytar.android.game.ShareBadgeDialog;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.profile.BadgeDialog;
import com.sprytar.android.util.BadgeUtils;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.sprytar.android.game.ShareBadgeDialog.getFacebookShareResult;
import static com.sprytar.android.profile.BadgeDialog.getDialog;

public class QuizGameFinishedActivity extends BaseActivity implements QuizGameFinishedView, View.OnClickListener, ShareBadgeDialog.ShareBadgeDialogListener {

    public static final String LOCATION_EXTRA = "com.sprytar.android.data.model.LocationExtra";
    public static final String EARNED_BADGE_EXTRA = "com.sprytar.android.data.model.EarnedBadgeExtra";
    public static final String CORRECT_ANSWERS_EXTRA = "com.sprytar.android.data.model.CorrectAnswers";
    public static final String NUM_OF_QUESTIONS = "com.sprytar.android.data.model.NumOfQuestions";

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    DataSource<CloseableReference<CloseableImage>> dataSource;

    public static void startForResult(Activity activity, int requestCode, EarnedBadge badge,
                                      Location location,
                                      int correctAnswers, int numOfQuestions) {
        Intent starter = new Intent(activity, QuizGameFinishedActivity.class);
        starter.putExtra(EARNED_BADGE_EXTRA, Parcels.wrap(badge));
        starter.putExtra(LOCATION_EXTRA, Parcels.wrap(location));
        starter.putExtra(CORRECT_ANSWERS_EXTRA, correctAnswers);
        starter.putExtra(NUM_OF_QUESTIONS, numOfQuestions);
        activity.startActivityForResult(starter, requestCode);
    }

    private BadgeDialog.BadgeDialogListener badgeDialogListener = new BadgeDialog.BadgeDialogListener() {
        @Override
        public void onClose() {
            badgeDialog.dismiss();
        }
    };

    private ActivityQuizGameFinishedBinding binding;
    private EarnedBadge badge;
    private Location location;

    private AlertDialog badgeDialog = null;
    private AlertDialog shareBadgeDialog = null;
    private String scroeMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_game_finished);

        Intent intent = getIntent();
        badge = Parcels.unwrap(intent.getParcelableExtra(EARNED_BADGE_EXTRA));
        location = Parcels.unwrap(intent.getParcelableExtra(LOCATION_EXTRA));
        int correctAnswers = intent.getIntExtra(CORRECT_ANSWERS_EXTRA, 0);
        int numOfQuestions = intent.getIntExtra(NUM_OF_QUESTIONS, 0);

        initUi();
        setQuestionsData(correctAnswers, numOfQuestions);
    }

    private void setQuestionsData(int correctAnswers, int numOfQuestions) {
        if (numOfQuestions > 0) {
            binding.scoreTextView.setText("You scored " + correctAnswers + " out of " + numOfQuestions);

            scroeMsg = " I just scored " + correctAnswers + " out of " + numOfQuestions + " on the Sprytar quiz at " + location.getName() + ". Have any of my friends scored higher?";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private void shareToFacebookApi(Bitmap bitmap) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        String caption = badge == null ? location.getName() : badge.getBadgeName();

        List<String> permissionNeeds = Arrays.asList("publish_actions");

        //this loginManager helps you eliminate adding a LoginButton to your UI

        loginManager = LoginManager.getInstance();
        loginManager.logOut();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                SharePhoto photo = new SharePhoto.Builder()
//                        .setBitmap(bitmap)
//                        //.setImageUrl(Uri.parse(location.getImageLink()))
//                        .setCaption(caption)
//                        .build();
//
//                SharePhotoContent content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .build();
//
//                ShareDialog facebookDialog = new ShareDialog(QuizGameFinishedActivity.this);
//                I just scored \( treasureHunt.correctAnswerCount) out of \(treasureHunt.questionCount) on the Sprytar quiz at \(venueName). Have any of my friends scored higher?

//
//                ShareLinkContent content1 = new ShareLinkContent.Builder()
//                        .setQuote(scroeMsg)
//                        .setContentUrl(Uri.parse("https://media.sprytar.com/images/locations/venue-97.png"))
//                        .build();
//                ShareDialog facebookDialog = new ShareDialog(QuizGameFinishedActivity.this);
//                facebookDialog.show(content1);


                shareBadgeDialog = getFacebookShareResult(QuizGameFinishedActivity.this, QuizGameFinishedActivity.this, location.getImageLink(), scroeMsg);
                shareBadgeDialog.show();


            }

            @Override
            public void onCancel() {
                Timber.v("Facebook register call bac -onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Timber.d(exception.getLocalizedMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
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
        String imageLink = location.getImageLink();
        if (imageLink != null && !imageLink.isEmpty()) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();

            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(imageLink))
                    .setRequestPriority(Priority.HIGH)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .build();

            dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);

            try {
                dataSource.subscribe(new BaseBitmapDataSubscriber() {
                    @Override
                    public void onNewResultImpl(@Nullable Bitmap bitmap) {

                        if (bitmap == null) {
                            Timber.d("Bitmap data source returned success, but bitmap null.");
                            return;
                        }

                        shareToFacebookApi(bitmap);
                    }

                    @Override
                    public void onFailureImpl(DataSource dataSource) {
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

    private ProfileEarnedBadges getProfileEarnedObject() {
        ProfileEarnedBadges earnedBadge = new ProfileEarnedBadges();
        earnedBadge.setName(badge.getBadgeName());
        earnedBadge.setIcon(badge.getBadgeIcon());
        earnedBadge.setDescription(badge.getBadgeDescription());

        return earnedBadge;
    }

    private void setListeners() {
        binding.distanceTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (badge == null) {
            return;
        }

        int id = view.getId();
        if (id == binding.distanceTextView.getId()) {
            badgeDialog = getDialog(this, badgeDialogListener, getProfileEarnedObject(), R.drawable.ic_game_active_24dp);
        }

        badgeDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClose() {
        if (shareBadgeDialog != null)
            shareBadgeDialog.dismiss();
    }

    @Override
    public void onShare(String message) {
        if (shareBadgeDialog != null)
            shareBadgeDialog.dismiss();
        Bundle params = new Bundle();
        params.putString("message", scroeMsg);
        params.putString("link", location.getImageLink());
/* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                    }
                }
        ).executeAsync();
    }
}

