package com.novp.sprytar.wikiarchitect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;

public class SamplePoiDetailActivity extends Activity {

  public static final String EXTRAS_KEY_POI_ID = "id";
  public static final String EXTRAS_KEY_POI_TITILE = "title";
  public static final String EXTRAS_KEY_POI_DESCR = "description";
  private Tracker mTracker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.sample_poidetail);

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Sample POI Details Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    ((TextView) findViewById(R.id.poi_id)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_ID));
    ((TextView) findViewById(R.id.poi_title)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_TITILE));
    ((TextView) findViewById(R.id.poi_description)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_DESCR));
  }
}
