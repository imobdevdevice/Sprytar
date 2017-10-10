package com.sprytar.android;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.DraweeConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.BuildConfig;
import com.sprytar.android.R;
import com.sprytar.android.injection.component.DaggerApplicationComponent;
import com.sprytar.android.injection.component.DaggerSessionComponent;
import com.sprytar.android.data.InitialData;
import com.sprytar.android.domain.CrashlyticsTree;
import com.sprytar.android.domain.SchemaMigration;
import com.sprytar.android.domain.UniversalRealmListParcelConverter;
import com.sprytar.android.injection.component.ApplicationComponent;
import com.sprytar.android.injection.component.SessionComponent;
import com.sprytar.android.injection.module.ApplicationModule;
import com.sprytar.android.injection.module.RetrofitModule;
import com.sprytar.android.util.frescocustom.CustomImageFormatConfigurator;

import org.parceler.Parcel;
import org.parceler.ParcelClass;

import java.util.HashSet;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import timber.log.Timber;

@ParcelClass(value = RealmList.class, annotation = @Parcel(converter = UniversalRealmListParcelConverter.class)) public class SprytarApplication
    extends Application {

  private ApplicationComponent applicationComponent;
  private SessionComponent sessionComponent;

  private static GoogleAnalytics sAnalytics;
  private static Tracker sTracker;

  protected String userAgent;

  //    private RefWatcher refWatcher;

  public static SprytarApplication get(Context context) {
    return (SprytarApplication) context.getApplicationContext();
  }

  //    public static RefWatcher getRefWatcher(Context context) {
  //        SprytarApplication application = (SprytarApplication) context.getApplicationContext();
  //        return  application.refWatcher;
  //    }

  @Override public void onCreate() {
    super.onCreate();

    //Fresco.initialize(this);

    Set<RequestListener> listeners = new HashSet<>();
    listeners.add(new RequestLoggingListener());

    ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
        .setRequestListeners(listeners)
        .setImageDecoderConfig(CustomImageFormatConfigurator.createImageDecoderConfig(this))
        .experiment()
        .setMediaVariationsIndexEnabled(new Supplier<Boolean>() {
          @Override public Boolean get() {
            return true;
          }
        })
        .build();

    DraweeConfig.Builder draweeConfigBuilder = DraweeConfig.newBuilder();
    CustomImageFormatConfigurator.addCustomDrawableFactories(this, draweeConfigBuilder);

    Fresco.initialize(this, imagePipelineConfig, draweeConfigBuilder.build());

    Realm.init(this);
    RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(0)
        .migration(new SchemaMigration())
        .deleteRealmIfMigrationNeeded()
        .initialData(new InitialData())
        .build();
    Realm.setDefaultConfiguration(config);

    CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
    Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new CrashlyticsTree());
    }

    userAgent = Util.getUserAgent(this, "Sprytar");
    //        if (!LeakCanary.isInAnalyzerProcess(this)) {
    //            refWatcher = LeakCanary.install(this);
    //        }
    sAnalytics = GoogleAnalytics.getInstance(this);
  }

  /**
   * Gets the default {@link Tracker} for this {@link Application}.
   *
   * @return tracker
   */
  synchronized public Tracker getDefaultTracker() {
    // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
    if (sTracker == null) {
      sTracker = sAnalytics.newTracker(R.xml.global_tracker);
    }

    return sTracker;
  }

  public ApplicationComponent getComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }
    return applicationComponent;
  }

  public SessionComponent getSessionComponent() {
    if (sessionComponent == null) {
      createSessionComponent();
    }
    return sessionComponent;
  }

  private void createSessionComponent() {
    sessionComponent = DaggerSessionComponent.builder().applicationComponent(getComponent()).retrofitModule(new RetrofitModule()).build();
  }

  public void releaseSessionComponent() {
    sessionComponent = null;
  }

  public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
  }

  public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
  }
}
