package com.novp.sprytar.poi;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.novp.sprytar.R;
import com.novp.sprytar.data.model.PointOfInterest;

import java.util.concurrent.TimeUnit;

public class CustomAudioPlayer extends LinearLayout implements View.OnClickListener {

    private MediaPlayer player = new MediaPlayer();

    // for updating the ui with the seekbar progress
    private Handler mHandler = new Handler();
    private Runnable myRunnable;

    private ImageView btnPlayStop;
    private SimpleDraweeView mainImage;
    private TextView title, timeRemaining;
    private SeekBar playProgress;

    private boolean isPlayerPrepared = false;

    private PointOfInterest poiFile;

    public CustomAudioPlayer(Context context) {
        super(context);

        init();
    }

    public CustomAudioPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        inflateLayout();
        findUIRefs();
        setListeners();
    }

    public void setPoiFile(PointOfInterest poiFile) {
        this.poiFile = poiFile;
        setTextualData();
        setImage();
    }

    private void setImage() {
        mainImage.setImageURI(Uri.parse(poiFile.getImage()));
    }

    private void setTextualData() {
        if (poiFile != null) {
            title.setText(poiFile.getTitle());
        }
    }

    private void setListeners() {
        btnPlayStop.setOnClickListener(this);
    }

    private void findUIRefs() {
        mainImage = (SimpleDraweeView) findViewById(R.id.mainImage);
        btnPlayStop = (ImageView) findViewById(R.id.btnplayStop);
        title = (TextView) findViewById(R.id.title);
        timeRemaining = (TextView) findViewById(R.id.timeRemaining);
        playProgress = (SeekBar) findViewById(R.id.seekPlay);
    }

    /**
     * inflates this view with the selected layout
     */
    private void inflateLayout() {
        ((LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.view_custom_audio_player, this);
    }

    private void initSeekBar(int duration) {
        playProgress.setMax(duration);
    }

    private void keepUpdateUI() {

        myRunnable = new Runnable() {

            @Override
            public void run() {
                if (player != null) {
                    try {
                        int mCurrentPosition = player.getCurrentPosition();

                        long timeInMilliseconds = player.getDuration() - mCurrentPosition;

                        final String hms = String.format(
                                "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                                        - TimeUnit.HOURS
                                        .toMinutes(TimeUnit.MILLISECONDS
                                                .toHours(timeInMilliseconds)),
                                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds)
                                        - TimeUnit.MINUTES
                                        .toSeconds(TimeUnit.MILLISECONDS
                                                .toMinutes(timeInMilliseconds)));


                        playProgress.setProgress(mCurrentPosition);
                 //       int mCount = (player.getDuration() - mCurrentPosition) / 1000;

                        timeRemaining.setText(hms);

                   //     timeRemaining.setText("00:" + mCount);
                    } catch (IllegalStateException e) {
                    }

                }
                mHandler.postDelayed(this, 50);
            }
        };

        ((Activity) getContext()).runOnUiThread(myRunnable);
    }

    /**
     * Plays the audio message
     */
    public void playAudioMessage() {

        // check if it is already playing
        if (player != null) {
            try {
                if (player.isPlaying() == true) {
                    player.stop();
                    // player.release();
                    player.reset();
                }
            } catch (IllegalStateException e) {
                // mediaplayer error
            }
        }

        try {
            if (isPlayerPrepared == true) {
                player.start();
                initSeekBar(player.getDuration());
                keepUpdateUI();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudioMessage();
                    }
                });
            } else {
                try {
                    Uri uri = Uri.parse(poiFile.getPoiFiles().get(0).filePath);
                    player.reset();
                    player.release();
                    player = new MediaPlayer();
                    player.setDataSource(getContext(), uri);

                    player.prepareAsync();
                    Log.e("test_tag", "after preparing");
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            player.start();
                            initSeekBar(player.getDuration());
                            keepUpdateUI();
                        }
                    });

                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mHandler.removeCallbacks(myRunnable);
                            player.reset();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Log.v("test_tag", "error " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stops the audio message
     */
    public void stopAudioMessage() {
        try {
            player.stop();

            player.reset();
            // player.release();
            mHandler.removeCallbacks(myRunnable);
        } catch (IllegalStateException e2) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnPlayStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
    }

    public void prepareMediaPlayer() {
        try {
            Uri uri = Uri.parse(poiFile.getPoiFiles().get(0).filePath);
            player.setDataSource(getContext(), uri);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlayerPrepared = true;
                    playAudioMessage();
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mHandler.removeCallbacks(myRunnable);
                    player.reset();
                    btnPlayStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (poiFile == null) {
            return;
        }

        int id = view.getId();
        if (id == R.id.btnplayStop) {
            if (player.isPlaying()) {
                stopAudioMessage();
                btnPlayStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            } else {
                btnPlayStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                player.reset();
                prepareMediaPlayer();
            }
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
        }
    }
}
