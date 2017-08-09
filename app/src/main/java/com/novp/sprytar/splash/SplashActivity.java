package com.novp.sprytar.splash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.intro.IntroDialog;
import com.novp.sprytar.login.LoginActivity;
import com.novp.sprytar.main.MainActivity;
import com.novp.sprytar.presentation.BaseActivity;
import javax.inject.Inject;

/**
 * This activity uses Loader approach to make Presenter survive orientation change
 */
public class SplashActivity extends BaseActivity implements SplashView {

  @SuppressWarnings("WeakerAccess") @Inject SplashPresenter presenter;
  private Tracker mTracker;

  public static void start(Context context) {

    Intent starter = new Intent(context, SplashActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    createSplashComponent().inject(this);
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);

    super.onCreate(savedInstanceState);
    presenter.attachView(this);

    setContentView(R.layout.activity_splash);

    setTypeFace();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Splash Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    presenter.showIntroDialogIfNeeded();
  }

  private void setTypeFace() {
    try {
      Typeface face = Typeface.createFromAsset(getAssets(), "fonts/museo.ttf");
      ((Button) findViewById(R.id.getStarted_button)).setTypeface(face);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private SplashComponent createSplashComponent() {
    return DaggerSplashComponent.builder().sessionComponent(getSessionComponent()).build();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override public void showIntroDialog() {
    IntroDialog fragment = IntroDialog.newInstance();

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  @Override public void showError(String message) {
    Toast.makeText(this, getString(R.string.error_occurred, message), Toast.LENGTH_SHORT).show();
  }

  public void onGetStartedClick(View view) {
    presenter.onGetStartedClick();
  }

  @Override public void showMainActivityUi() {
    MainActivity.start(this);
  }

  @Override public void showLoginUi() {
    LoginActivity.start(this);
  }
}
