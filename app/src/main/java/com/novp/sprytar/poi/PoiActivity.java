package com.novp.sprytar.poi;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.PoiFile;
import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.databinding.ActivityPoiBinding;
import com.novp.sprytar.databinding.PoiPlayerViewBinding;
import com.novp.sprytar.presentation.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.parceler.Parcels;

public class PoiActivity extends BaseActivity
    implements PoiView, ImagePagerAdapter.Callback, ExoPlayer.EventListener, PlaybackControlView.VisibilityListener {

  public static final String POI_EXTRA = "com.novp.sprytar.poi.PoiExtra";
  public static final String AMENITY_EXTRA = "com.novp.sprytar.poi.AmenityExtra";
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

  @Inject PoiPresenter presenter;

  private ActivityPoiBinding binding;
  private ActionBar actionBar;

  private ImagePagerAdapter imagePagerAdapter;
  private CirclePageIndicator pageIndicator;

  private SimpleExoPlayer player;
  private DefaultTrackSelector trackSelector;
  private EventLogger eventLogger;
  private SimpleExoPlayerView playerView;
  private DebugTextViewHelper debugViewHelper;
  private boolean playerNeedsSource;
  private DataSource.Factory mediaDataSourceFactory;
  private Handler mainHandler;
  private Tracker mTracker;

  public static void start(Context context, PointOfInterest poi) {
    Intent starter = new Intent(context, PoiActivity.class);
    starter.putExtra(POI_EXTRA, Parcels.wrap(poi));

    context.startActivity(starter);
  }

  public static void start(Context context, Amenity amenity) {
    Intent starter = new Intent(context, PoiActivity.class);
    starter.putExtra(AMENITY_EXTRA, Parcels.wrap(amenity));

    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_poi);

    createComponent().inject(this);
    presenter.attachView(this);

    mediaDataSourceFactory = buildDataSourceFactory(true);
    mainHandler = new Handler();
    ensurePermissions();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("POI Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();

    PointOfInterest poi = Parcels.unwrap(getIntent().getParcelableExtra(POI_EXTRA));
    if (poi != null) {
      presenter.setPoi(poi);
    } else {
      Amenity amenity = Parcels.unwrap(getIntent().getParcelableExtra(AMENITY_EXTRA));
      presenter.setAmenity(amenity);
    }
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(getString(R.string.poi_title));

    imagePagerAdapter = new ImagePagerAdapter(this);
    imagePagerAdapter.setCallback(this);
    binding.galleryPager.setAdapter(imagePagerAdapter);

    pageIndicator = binding.controlsLayout.galleryIndicator;
    pageIndicator.setViewPager(binding.galleryPager);
  }

  private void ensurePermissions() {
  }

  @Override public void showAmenity(Amenity amenity) {
    actionBar.setTitle(amenity.getName());
    binding.descriptionTextView.setText(amenity.getDescription());
    PoiFile file = new PoiFile();
    file.setFileType(PoiFile.IMAGE);
    file.setFilePath(amenity.getImage());
    List<PoiFile> files = new ArrayList<>();
    files.add(file);
    imagePagerAdapter.setImages(files);
    imagePagerAdapter.notifyDataSetChanged();
    pageIndicator.notifyDataSetChanged();
    if (files == null || files.size() <= 1) {
      pageIndicator.setVisibility(View.GONE);
    } else {
      pageIndicator.setVisibility(View.VISIBLE);
    }
  }

  @Override public void showAudioPoi(PointOfInterest audioPoi) {
    binding.audioPlayer.setPoiFile(audioPoi);
    binding.audioPlayer.setVisibility(View.VISIBLE);
  }

  @Override public void showPoi(PointOfInterest poi) {
    actionBar.setTitle(poi.getTitle());
    binding.setPoi(poi);
    binding.descriptionTextView.setText(poi.getDescription());
    List<PoiFile> poiFiles = poi.getPoiFiles();
    imagePagerAdapter.setImages(poiFiles);
    imagePagerAdapter.notifyDataSetChanged();
    pageIndicator.notifyDataSetChanged();
    if (poiFiles == null || poiFiles.size() <= 1) {
      pageIndicator.setVisibility(View.GONE);
    } else {
      pageIndicator.setVisibility(View.VISIBLE);
    }

    //        List<PoiFile> files = poi.getPoiFiles();
    //        if (files.size() > 0) {
    //            Uri imagePath = Uri.parse(files.get(0).getFilePath());

    //
    //            DraweeController controller = Fresco.newDraweeControllerBuilder()
    //                    .setUri(imagePath)
    //                    .setOldController(binding.photoImageView.getController())
    //                    .build();
    //            binding.photoImageView.setController(controller);
    //        }

  }

  @Override protected void onDestroy() {
    super.onDestroy();
    binding.audioPlayer.releasePlayer();
    presenter.detachView();
    releasePlayer();
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private PoiComponent createComponent() {
    return DaggerPoiComponent.builder().applicationComponent(getApplicationComponent()).build();
  }

  @Override public void showError(String message) {

  }

  @Override public void updatePoiControls(PoiFile poiFile, PoiPlayerViewBinding playerBinding) {

    this.playerView = playerBinding.playerView;

    this.playerView.setControllerVisibilityListener(this);
    this.playerView.requestFocus();

    initializePlayer(poiFile, playerBinding);
  }

  @Override protected void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  private void initializePlayer(PoiFile poiFile, PoiPlayerViewBinding playerBinding) {
    if (player == null) {

      DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

      @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode = SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;

      TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);

      trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
      player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, new DefaultLoadControl(), drmSessionManager, extensionRendererMode);
      player.addListener(this);

      eventLogger = new EventLogger(trackSelector);
      player.addListener(eventLogger);
      player.setAudioDebugListener(eventLogger);
      player.setVideoDebugListener(eventLogger);
      player.setMetadataOutput(eventLogger);

      playerView.setPlayer(player);
      player.setPlayWhenReady(false);

      debugViewHelper = new DebugTextViewHelper(player, playerBinding.debugTextView);
      debugViewHelper.start();

      playerNeedsSource = true;
    }

    MediaSource mediaSource = buildMediaSource(Uri.parse(poiFile.getFilePath()), null);
    player.prepare(mediaSource, false, false);
  }

  private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
    int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension : uri.getLastPathSegment());
    switch (type) {
      case C.TYPE_SS:
        return new SsMediaSource(uri, buildDataSourceFactory(false), new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler,
            eventLogger);
      case C.TYPE_DASH:
        return new DashMediaSource(uri, buildDataSourceFactory(false), new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler,
            eventLogger);
      case C.TYPE_HLS:
        return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
      case C.TYPE_OTHER:
        return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, eventLogger);
      default: {
        throw new IllegalStateException("Unsupported type: " + type);
      }
    }
  }

  private void releasePlayer() {
    if (player != null) {
      player.release();
      player = null;
      trackSelector = null;
      eventLogger = null;
    }
  }

  private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
    return SprytarApplication.get(this).buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

  }

  @Override public void onLoadingChanged(boolean isLoading) {

  }

  @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

  }

  @Override public void onPlayerError(ExoPlaybackException error) {

  }

  @Override public void onPositionDiscontinuity() {

  }

  @Override public void onVisibilityChange(int visibility) {

  }
}
