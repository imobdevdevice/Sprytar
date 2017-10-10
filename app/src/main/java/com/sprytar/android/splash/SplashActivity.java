package com.sprytar.android.splash;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.sprytar.android.R;
import com.sprytar.android.splash.DaggerSplashComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.intro.IntroDialog;
import com.sprytar.android.login.LoginActivity;
import com.sprytar.android.main.MainActivity;
import com.sprytar.android.presentation.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

/**
 * This activity uses Loader approach to make Presenter survive orientation change
 */
public class SplashActivity extends BaseActivity implements SplashView {

    @SuppressWarnings("WeakerAccess")
    @Inject
    SplashPresenter presenter;
    private Tracker mTracker;

    public static void start(Context context) {


        Intent starter = new Intent(context, SplashActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

//        PackageManager pm = getPackageManager();
//        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
//            // This device does not have a compass, turn off the compass feature
//            Toast.makeText(this, "Compass not available", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Compass available", Toast.LENGTH_SHORT).show();
//        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sprytar.android",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("generated_KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showIntroDialog() {
        IntroDialog fragment = IntroDialog.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, getString(R.string.error_occurred, message), Toast.LENGTH_SHORT).show();
    }

    public void onGetStartedClick(View view) {
        presenter.onGetStartedClick();
    }

    @Override
    public void showMainActivityUi() {
        MainActivity.start(this);
    }

    @Override
    public void showLoginUi() {
        LoginActivity.start(this);
    }
}
