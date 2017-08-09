package com.novp.sprytar.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.novp.sprytar.ConfigFirebase;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.injection.component.ApplicationComponent;
import com.novp.sprytar.location.LocationFragment;
import com.novp.sprytar.login.LoginActivity;
import com.novp.sprytar.login.PickUserActivity;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.profile.ProfileFragment;
import com.novp.sprytar.services.LocationSendToServerService;
import com.novp.sprytar.settings.SettingsFragment;
import com.novp.sprytar.setup.SetupFragment;
import com.novp.sprytar.splash.SplashActivity;
import com.novp.sprytar.support.SupportFragment;
import com.novp.sprytar.util.NotificationUtils;
import com.novp.sprytar.util.PermissionUtils;
import java.util.Calendar;
import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainActivityView, NavigationView.OnNavigationItemSelectedListener {

  @SuppressWarnings("WeakerAccess") @Inject SpSession spSession;

  @SuppressWarnings("WeakerAccess") @Inject MainActivityPresenter presenter;

  private DrawerLayout drawer;

  private MenuItem selected = null;
  private Tracker mTracker;
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

  private boolean locationPermissionGranted;

  private BroadcastReceiver mRegistrationBroadcastReceiver;

  public static void start(Context context) {
    Intent starter = new Intent(context, MainActivity.class);
    starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSessionComponent().inject(this);
    setContentView(R.layout.activity_base_drawer);
    presenter.attachView(this);

    ensurePermissions();
    presenter.setMyLocationEnabled(locationPermissionGranted);

    createBroadcastReceiver();

    checkFirebaseToken();

    configureUi();
    initNavigationView();
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.content, LocationFragment.newInstance()).commit();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Main Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    presenter.showDisclaimerIfNeed();
  }

  private void checkFirebaseToken() {
    SharedPreferences pref = getApplicationContext().getSharedPreferences(ConfigFirebase.SHARED_PREF, 0);
    String regId = pref.getString("regId", null);

    if (regId != null) {
      String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
      presenter.registerDeviceForPushNotifications(regId, deviceId);
    }
  }

  @Override protected void onDestroy() {
    presenter.detachView();
    presenter.onDestroyed();
    super.onDestroy();
  }

  @Override public void showApplicationUi() {

  }

  @Override public void showError(String message) {

  }

  protected ApplicationComponent getApplicationComponent() {
    return ((SprytarApplication) getApplication()).getComponent();
  }

  @Override public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  private void initNavigationView() {

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.setCheckedItem(R.id.nav_discover);
    navigationView.setItemIconTintList(null);

    Menu menu = navigationView.getMenu();
    menu.findItem(R.id.nav_settings).setVisible(spSession.isLoggedIn());
    menu.findItem(R.id.nav_profile).setVisible(spSession.isLoggedIn());
    menu.findItem(R.id.nav_setup).setVisible(spSession.isAdmin());

    View headerView = navigationView.getHeaderView(0);

    TextView nameTextView = (TextView) headerView.findViewById(R.id.name_textView);

    String name = "";

    String buttonText = "";
    if (spSession.isLoggedIn()) {
      name = spSession.getName();
      //     buttonText = getString(R.string.switch_user_button);
    } else {
      name = getString(R.string.unregistered_user);
      //     buttonText = getString(R.string.login_user_button);
    }

    nameTextView.setText(name);

    SimpleDraweeView drawee = (SimpleDraweeView) headerView.findViewById(R.id.avatar_imageView);

    DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(spSession.getAvatar()).setOldController(drawee.getController()).build();
    drawee.setController(controller);

    //   Button buttonHeader = (Button) headerView.findViewById(R.id.header_button);
    //   buttonHeader.setText(buttonText);

  }

  private void configureUi() {

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        navigate();
      }

      @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
      }
    });
    toggle.syncState();
  }

  private void navigate() {
    if (selected != null) {
      Fragment fragment = null;
      switch (selected.getItemId()) {
        case R.id.nav_discover:
          fragment = LocationFragment.newInstance();
          break;
        case R.id.nav_settings:
          fragment = SettingsFragment.newInstance();
          break;
        case R.id.nav_profile:
          fragment = ProfileFragment.newInstance();
          break;
        case R.id.nav_setup:
          fragment = SetupFragment.newInstance();
          break;
        case R.id.nav_support:
          fragment = SupportFragment.newInstance();
          break;
        case R.id.nav_logout:
          presenter.onLogoutClick();
      }

      if (fragment != null) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
      }
    }
    selected = null;
  }

  @Override public boolean onNavigationItemSelected(MenuItem item) {
    drawer.closeDrawer(GravityCompat.START);
    selected = item;
    return true;
  }

  public void onGetStartedClick(View view) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.content, LocationFragment.newInstance()).commit();
  }

  public void onHeaderButtonClick(View view) {
    if (spSession.isLoggedIn()) {
      PickUserActivity.start(this);
    } else {
      spSession.logout();
      LoginActivity.start(this);
    }
  }

  @Override public void showSplashUi() {
    SplashActivity.start(this);
  }

  @Override public void showDisclaimerDialog() {
    DisclaimerDialog fragment = DisclaimerDialog.newInstance();

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  private void ensurePermissions() {

    if (!PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
      PermissionUtils.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION_PERMISSION_REQUEST_CODE,
          Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case LOCATION_PERMISSION_REQUEST_CODE: {
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
          locationPermissionGranted = true;
        } else {
          locationPermissionGranted = false;
        }
        break;
      }
    }
  }

  private void createBroadcastReceiver() {
    mRegistrationBroadcastReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {

        // checking for type intent filter
        if (intent.getAction().equals(ConfigFirebase.REGISTRATION_COMPLETE)) {
          // gcm successfully registered
          // now subscribe to `global` topic to receive app wide notifications
          FirebaseMessaging.getInstance().subscribeToTopic(ConfigFirebase.TOPIC_GLOBAL);

          registerFirebaseRegId();
        } else if (intent.getAction().equals(ConfigFirebase.PUSH_NOTIFICATION)) {
          // new push notification is received

          String message = intent.getStringExtra("message");

          Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

          Log.v("test_tag", "new message is received " + message);
        }
      }
    };
  }

  @Override protected void onResume() {
    super.onResume();

    stopSendLocationToServerAlarmManager();

    // register GCM registration complete receiver
    LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(ConfigFirebase.REGISTRATION_COMPLETE));

    // register new push message receiver
    // by doing this, the activity will be notified each time a new message arrives
    LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(ConfigFirebase.PUSH_NOTIFICATION));

    // clear the notification area when the app is opened
    NotificationUtils.clearNotifications(getApplicationContext());
  }

  @Override protected void onPause() {
    startSendLocationToServerAlarmManager();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    super.onPause();
  }

  // Fetches reg id from shared preferences
  private void registerFirebaseRegId() {
    SharedPreferences pref = getApplicationContext().getSharedPreferences(ConfigFirebase.SHARED_PREF, 0);
    String regId = pref.getString("regId", null);

    Log.e("test_tag", "Firebase Reg Id: " + regId);

    if (!TextUtils.isEmpty(regId)) {
      Log.v("test_tag", "Firebase Reg Id: " + regId);
      String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
      presenter.registerDeviceForPushNotifications(regId, deviceId);
    } else {
      Log.v("test_tag", "Firebase Reg Id is not received yet!");
    }
  }

  private void stopSendLocationToServerAlarmManager() {
    Intent intent = new Intent(MainActivity.this, LocationSendToServerService.class);
    PendingIntent sender = PendingIntent.getService(this, 0, intent, 0);
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.cancel(sender);
  }

  private void startSendLocationToServerAlarmManager() {
    Calendar cal = Calendar.getInstance();
    Intent intent = new Intent(MainActivity.this, LocationSendToServerService.class);
    PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10 * 30000, pintent);// wake in 5 minutes
  }
}
