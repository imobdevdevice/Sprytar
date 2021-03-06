package com.sprytar.android.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.sprytar.android.R;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.EarnedBadge;
import com.sprytar.android.data.model.LocationBoundary;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.data.model.VenueActivity;
import com.sprytar.android.databinding.ActivityQuizBinding;
import com.sprytar.android.databinding.ItemQuestionIconBinding;
import com.sprytar.android.game.photohunt.PhotoHuntActivity;
import com.sprytar.android.game.treasureHuntIntro.DialogTreasureHuntIntro;
import com.sprytar.android.game.treasureHuntIntro.TreasureHuntDialogListner;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;
import com.sprytar.android.presentation.BaseFragmentUpdateable;
import com.sprytar.android.util.Showcase;
import com.sprytar.android.util.Utils;
import com.sprytar.android.wikiarchitect.AutoHdSampleCamActivity;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class QuizActivity extends BaseActivity
        implements QuizView, GameMapFragment.Callback,
        LookZoneFragment.Callback, View.OnClickListener, TreasureHuntDialogListner, SpryteViewFragment.Callback {

    public static final String VENUE_EXTRA = "com.sprytar.android.game.VenueExtra";
    public static final String MARKERS_EXTRA = "com.sprytar.android.game.MarkersExtra";
    public static final String VENUE_ID_EXTRA = "com.sprytar.android.game.VenueIdExtra";
    public static final String LOCATION_NAME_EXTRA = "location_name_extra";
    public static final String IMAGE_URL_EXTRA = "image_url_extra";
    public static final String SITE_NAME_EXTRA = "site_name_extra";
    private static final int ANSWER_REQUEST_CODE = 1;
    private static final int CLOSE_REQUEST_CODE = 11;
    private static final int IMAGE_RECOGNITION_MAP = 22;

    private FancyShowCaseView showCaseView1, showCaseView2, showCaseView3, showCaseView4, showCaseView5 = null;
    private FancyShowCaseQueue showCaseQueue = null;


    @Inject
    QuizPresenter presenter;

    @Inject
    QuestionAdapter questionAdapter;

    private Tracker mTracker;
    private ActivityQuizBinding binding;
    private QuizGroupFragment quizGroupFragment;
    private FragmentManager fragmentManager;
    private LookZoneFragment lookZoneFragment;

    private static String SITE_NAME = "";
    private SpryteViewFragment spryteViewFragment;


    public static void startForResult(Activity activity, VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, int requestCode,
                                      String locationName, String locationImageUrl, String siteName) {

        Intent starter = new Intent(activity, QuizActivity.class);
        starter.putExtra(VENUE_EXTRA, Parcels.wrap(venueActivity));
        starter.putExtra(MARKERS_EXTRA, Parcels.wrap(boundaries));
        starter.putExtra(VENUE_ID_EXTRA, venueId);
        starter.putExtra(LOCATION_NAME_EXTRA, locationName);
        starter.putExtra(IMAGE_URL_EXTRA, locationImageUrl);
        starter.putExtra(SITE_NAME_EXTRA, siteName);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);

        createQuizComponent().inject(this);
        presenter.attachView(this);

        mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
        mTracker.setScreenName("Quiz Screen");
        mTracker.send(new HitBuilders.EventBuilder().build());
        mTracker.enableAutoActivityTracking(true);

        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        VenueActivity venueActivity = Parcels.unwrap(intent.getParcelableExtra(VENUE_EXTRA));
        List<LocationBoundary> boundaries = Parcels.unwrap(intent.getParcelableExtra(MARKERS_EXTRA));
        int venueId = intent.getIntExtra(VENUE_ID_EXTRA, 0);
        String locationName = intent.getStringExtra(LOCATION_NAME_EXTRA);
        String locationImageUrl = intent.getStringExtra(IMAGE_URL_EXTRA);
        SITE_NAME = intent.getStringExtra(SITE_NAME_EXTRA);
        initUi();
        try {
            presenter.setVenueActivity(venueActivity, boundaries, venueId, locationName, locationImageUrl);

//            initShowcase();
        } catch (NullPointerException e) {
            showError("Something went wrong. Please try again later");
            finish();
        }
    }


    private void initUi() {
        binding.venueName.setText(SITE_NAME);
        binding.gameType.setText(R.string.treasure_hunt);
        setTypeFace();

        binding.backArrow.setOnClickListener(this);
        binding.ivInfo.setOnClickListener(this);
    }

    private void setTypeFace() {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo_sans_500.otf");
        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/exljbris _museo_sans_300.otf");
        binding.venueName.setTypeface(face);
        binding.gameType.setTypeface(face);
    }

    @Override
    public void showFragments(List<LocationBoundary> boundaries, LatLng currentLatLn, double latitude, double longitude, double distance) {

        //quizGroupFragment = QuizGroupFragment.newInstance(boundaries, currentLatLn
        //  compass = new Compass(this,location,target,binding.tvCompass,binding.ivCompass);


        openGamemapFragment(boundaries, currentLatLn, distance);

    }

    private void openGamemapFragment(List<LocationBoundary> boundaries, LatLng currentLatLn, double distance) {
        GameMapFragment gameMapFragment = GameMapFragment.newInstance(boundaries, currentLatLn, true, distance);
        gameMapFragment.setCallback(this);
        //  quizGroupFragment.setCallback(this);
        if (lookZoneFragment != null) {
            fragmentManager.beginTransaction().remove(lookZoneFragment).commit();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.placeholder_fragment, gameMapFragment, "GROUP_FRAGMENT")
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


    @Override
    public void showPhotoHuntActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries, int venueId, LatLng currentLatLn,
                                      Question question) {
        PhotoHuntActivity.startForResult(this, venueActivity, boundaries, venueId, ANSWER_REQUEST_CODE, currentLatLn, question);
    }

    @Override
    public void showImageRecognitionMapActivity(VenueActivity venueActivity, List<LocationBoundary> boundaries, LatLng currentLatLng, Question question) {
        ImageRecognitionMapActivity.startForResult(this, venueActivity, boundaries, IMAGE_RECOGNITION_MAP, currentLatLng, question);
    }

    @Override
    public void updateFragmentContent(LatLng currentLatLn) {

        getSupportFragmentManager().beginTransaction().replace(R.id.placeholder_fragment, quizGroupFragment).commit();

        Bundle bundle = new Bundle();
        bundle.putParcelable(BaseFragmentUpdateable.UPDATE_PARAM_1, Parcels.wrap(currentLatLn));
        quizGroupFragment.updateContent(bundle);
    }

    @Override
    public void onInARZone(Location currentLocation) {
        if (Utils.hasCompass(this)) {
            spryteViewFragment = SpryteViewFragment
                    .newInstance(presenter.getLocationBoundries(),
                            presenter.getCurrentQuestion().getLatLng(), true, currentLocation, presenter.getCurrentQuestion().getQuestionDistance());
            spryteViewFragment.setCallback(this);
            fragmentManager.beginTransaction().replace(R.id.placeholder_fragment, spryteViewFragment).addToBackStack(null).commitAllowingStateLoss();
        }
    }

    @Override
    public void onGameMapZone(List<LocationBoundary> boundaries, LatLng currentLatLn, double distance) {
        openGamemapFragment(boundaries, currentLatLn, distance);
    }

    @Override
    public void onInSpryteZone() {
        lookZoneFragment = LookZoneFragment.newInstance(presenter.getCurrentQuestion(), presenter.isLastQuestion());
        lookZoneFragment.setCallback(this);
        fragmentManager.beginTransaction().replace(R.id.placeholder_fragment, lookZoneFragment).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    public void onReadyToAnswerClick() {
        presenter.onStartAnswerClick();
    }

    @Override
    public void onNextQuestion() {
        presenter.nextQuestion();
    }

    @Override
    public void onSkipQuestion() {
        presenter.nextQuestion();
    }


    @Override
    public void showVenueActivity(VenueActivity venueActivity, Question question) {
        binding.setVenue(venueActivity);
        questionAdapter.setCurrentQuestion(question);
        questionAdapter.setItems(venueActivity.getQuestions());
        binding.gameType.setText("Q" + String.valueOf(question.getId() + 1) + " of " + String.valueOf(venueActivity.getQuestions().size()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    private QuizComponent createQuizComponent() {
        return DaggerQuizComponent.builder().sessionComponent(getSessionComponent()).build();
    }

    public void onShowHintClick(View view) {
        presenter.onShowHintClick();
    }

    public void showIntro(View view) {
        DialogTreasureHuntIntro fragment = DialogTreasureHuntIntro.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void onStartClick(View view) {
        presenter.onStartAnswerClick();
    }

    @Override
    public void showAnswerActivity(Question question, boolean isLastQuestion) {
        AnswerActivity.startForResult(this, question, isLastQuestion, ANSWER_REQUEST_CODE);
    }

    @Override
    public void showAnswerImageActivity(Question question) {
        AnswerImageActivity.startForResult(this, question, ANSWER_REQUEST_CODE);
    }

  /*
  @Override public void showImageQuestion(VenueActivity venueActivity, Question question) {
    Toast.makeText(getApplicationContext(),"goes here", Toast.LENGTH_SHORT).show();
    AnswerImageActivity.startForResult(this, question, ANSWER_REQUEST_CODE);
  }
  */

    @Override
    public void showGameFinishedActivity(EarnedBadge badge, String locationName, String locationImageUrl, int correctAnswers, int numOfQuestions) {
        GameFinishedActivity.startForResult(this, CLOSE_REQUEST_CODE, badge, locationName, locationImageUrl, correctAnswers, numOfQuestions);
    }

    @Override
    public View getChildFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.map).getView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLOSE_REQUEST_CODE) {
            finish();
        } else if (requestCode == IMAGE_RECOGNITION_MAP) {
            presenter.showAnswerImageActivity();
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == ANSWER_REQUEST_CODE) {
                    presenter.nextQuestion();
                }
            }
        }
    }

    @Override
    public void showArCameraActivity(double latitude, double longitude) {
        AutoHdSampleCamActivity.start(this, latitude, longitude);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivInfo:
                startDialog();
                break;
            case R.id.back_arrow:
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void startDialog() {
        DialogTreasureHuntIntro fragment = DialogTreasureHuntIntro.newInstance(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDialogDismiss() {

    }


    public static class QuestionAdapter extends BaseBindingAdapter<Question> {
        private final Context context;
        private Question currentQuestion;

        @Inject
        public QuestionAdapter(Context context) {
            this.context = context;
        }

        public void setCurrentQuestion(Question question) {
            this.currentQuestion = question;
        }

        @Override
        protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return ItemQuestionIconBinding.inflate(inflater, parent, false);
        }

        @Override
        public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
            Question question = items.get(position);

            ItemQuestionIconBinding binding = (ItemQuestionIconBinding) holder.binding;
            binding.idTextView.setText(String.valueOf(question.getId() + 1));

            if (question.isAnswered() || question.equals(currentQuestion)) {
                binding.idTextView.setBackgroundResource(R.drawable.bg_question_active_circle_48dp);
                binding.idTextView.setTextColor(context.getResources().getColor(R.color.colorText));
            } else {
                binding.idTextView.setBackgroundResource(R.drawable.bg_question_inactive_circle_48dp);
                binding.idTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
            }
        }
    }

    @Override
    public void showHint(String hint) {
        GameHintDialog fragment = GameHintDialog.newInstance(hint);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }


    private void initShowcase() {
        if (presenter.getExplainerStatus()) {
            return;
        }
        // This code is for Explainer
/*
        binding.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                //Remove the listener before proceeding
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    binding.mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                showCaseQueue = new FancyShowCaseQueue();
                if (Showcase.getStep() == 1) {
                    showCaseView1 = Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
                            QuizActivity.this, R.layout.showcase_layout);
                    showCaseQueue.add(showCaseView1);
                } else if (Showcase.getStep() == 3) {
                    showCaseView3 =
                            Showcase.prepareShowCaseFor(binding.placeholderFragment, true, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE, QuizActivity.this,
                                    R.layout.showcase_layout_up);
                    showCaseQueue.add(showCaseView3);
                }

                showCaseQueue.show();
            }
        });
*/
    }

    public void onShowHelpClick(View view) {
        presenter.setExplainerCompleted(false);
        Showcase.resetStep();

        showCaseQueue = new FancyShowCaseQueue();
        if (Showcase.getStep() == 1) {
            showCaseView1 =
                    Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE, QuizActivity.this,
                            R.layout.showcase_layout);
            showCaseQueue.add(showCaseView1);
        } else if (Showcase.getStep() == 3) {
            showCaseView3 =
                    Showcase.prepareShowCaseFor(binding.placeholderFragment, true, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE, QuizActivity.this,
                            R.layout.showcase_layout_up);
            showCaseQueue.add(showCaseView3);
        }

        showCaseQueue.show();
    }

    private void hideShowCase() {
        if (Showcase.getStep() == 2) {
            if (Showcase.isReadyForRemove(showCaseView1)) {
                showCaseView1.hide();
            }
        } else if (Showcase.getStep() == 3) {
            if (Showcase.isReadyForRemove(showCaseView2)) {
                showCaseView2.hide();
            }
        } else if (Showcase.getStep() == 4) {
            if (Showcase.isReadyForRemove(showCaseView3)) {
                showCaseView3.hide();
            }
        } else if (Showcase.getStep() == 5) {
            if (Showcase.isReadyForRemove(showCaseView4)) {
                showCaseView4.hide();
            }
        } else if (Showcase.getStep() > 5) {
            if (Showcase.isReadyForRemove(showCaseView5)) {
                showCaseView5.hide();
            }
        }
    }

    private void showRelevantShowcase() {
        if (Showcase.getStep() == 1) {
            showCaseView1 =
                    Showcase.prepareShowCaseFor(findViewById(R.id.subNavigation), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE, QuizActivity.this,
                            R.layout.showcase_layout);
            showCaseQueue.add(showCaseView1);
        } else if (Showcase.getStep() == 2) {
            showCaseView2 = Showcase.prepareShowCaseFor(findViewById(R.id.question_textView), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
                    QuizActivity.this, R.layout.showcase_layout);
            showCaseQueue.add(showCaseView2);
        } else if (Showcase.getStep() == 3) {
            showCaseView3 =
                    Showcase.prepareShowCaseFor(binding.placeholderFragment, true, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE, QuizActivity.this,
                            R.layout.showcase_layout_up);
            showCaseQueue.add(showCaseView3);
        } else if (Showcase.getStep() == 4) {
            showCaseView4 = Showcase.prepareShowCaseFor(findViewById(R.id.placeholder_fragment), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
                    QuizActivity.this, R.layout.showcase_layout_up);
            showCaseQueue.add(showCaseView4);
        } else if (Showcase.getStep() == 5) {
            showCaseView5 = Showcase.prepareShowCaseFor(findViewById(R.id.help_imageView), false, showcaseClickListener, FocusShape.ROUNDED_RECTANGLE,
                    QuizActivity.this, R.layout.showcase_layout);
            showCaseQueue.add(showCaseView5);
        }
    }

    private View.OnClickListener showcaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.skipExplainer) {
                Showcase.increaseStep();
                hideShowCase();
                Showcase.resetStep();
                presenter.setExplainerCompleted(true);
            } else if (id == R.id.forwardArrowShowcase) {
                Showcase.increaseStep();
                hideShowCase();
                showRelevantShowcase();
            } else if (id == R.id.backArrowShowcase) {
                Showcase.increaseStep();
                hideShowCase();
                Showcase.decreaseStepBy(2);
                showRelevantShowcase();
            } else if (id == R.id.nextShowcase) {
                Showcase.resetStep();
                showCaseView5.hide();
                presenter.setExplainerCompleted(true);
            }
        }
    };
}
