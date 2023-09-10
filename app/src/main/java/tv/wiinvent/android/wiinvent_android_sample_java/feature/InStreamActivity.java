package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.Nullable;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360SkipAdsButtonAds;
import tv.wiinvent.wiinventsdk.InStreamManager;
import tv.wiinvent.wiinventsdk.OverlayManager;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.logging.LevelLog;
import tv.wiinvent.wiinventsdk.models.OverlayData;
import tv.wiinvent.wiinventsdk.models.ads.AdInStreamEvent;
import tv.wiinvent.wiinventsdk.models.ads.AdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.network.WiApiClient;
import tv.wiinvent.wiinventsdk.ui.OverlayView;

public class InStreamActivity extends AppCompatActivity {
  public static final String TAG = InStreamActivity.class.getCanonicalName();

  public static final String SAMPLE_ACCOUNT_ID = "4";
  public static final String SAMPLE_CHANNEL_ID = "998989";
  public static final String SAMPLE_STREAM_ID = "999999";

  private static final String CONTENT_URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";

  private PlayerView exoplayerView = null;
  private SimpleExoPlayer exoplayer;

  private Boolean fullscreen = false;
  private ImageView fullscreenButton = null;

  private TV360SkipAdsButtonAds skipButton = null;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_instream);

    exoplayerView = findViewById(R.id.simple_exo_player_view);
    fullscreenButton = findViewById(R.id.exo_fullscreen_icon);
    skipButton = findViewById(R.id.skip_button);

    initializePlayer();
  }

  private void initializePlayer() {
    ComponentName componentName = new ComponentName(getBaseContext(), "Exo");

    exoplayer = new SimpleExoPlayer.Builder(getBaseContext()).build();
    exoplayerView.setPlayer(exoplayer);
    exoplayerView.setUseController(true);

    PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();
    playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY |
        PlaybackStateCompat.ACTION_PAUSE |
        PlaybackStateCompat.ACTION_FAST_FORWARD);

    MediaSessionCompat mediaSession = new MediaSessionCompat(getBaseContext(), "ExoPlayer", componentName, null);
    mediaSession.setPlaybackState(playbackStateBuilder.build());
    mediaSession.setActive(true);

    String userAgent = Util.getUserAgent(getApplicationContext(), "Exo");
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getBaseContext(), userAgent);
    MediaSource mediaSource = buildMediaSource(dataSourceFactory, CONTENT_URL);

    InStreamManager.Companion.getInstance().init(getBaseContext(), SAMPLE_ACCOUNT_ID, DeviceType.PHONE, Environment.PRODUCTION, 10, 10, 10, LevelLog.BODY, 5, true);
    InStreamManager.Companion.getInstance().setLoaderListener(new InStreamManager.WiAdsLoaderListener() {
      @Override
      public void showSkipButton(int duration) {
        Log.d(TAG, "=========InStreamManager showSkipButton " + duration + " --- " + skipButton);
        if(skipButton != null) {
          skipButton.startCountdown(duration);
        }
      }

      @Override
      public void onTimeout() {

      }

      @Override
      public void hideSkipButton() {
        Log.d(TAG, "=========InStreamManager hideSkipButton ");

        if(skipButton != null) {
          skipButton.hide();
        }
      }

      @Override
      public void onEvent(@NonNull AdInStreamEvent event) {
        Log.d(TAG, "==========event " + event.getEventType() + " - " + event.getCampaignId());
      }

      @Override
      public void onResponse(@NonNull ImaAdsLoader imaAdsLoader) {
        Log.d(TAG, "==========onResponse " + imaAdsLoader.toString());

        imaAdsLoader.setPlayer(exoplayer);
        AdsMediaSource adsMediaSource = new AdsMediaSource(
            mediaSource, dataSourceFactory, imaAdsLoader, exoplayerView
        );
        exoplayer.prepare(adsMediaSource);
        exoplayer.setPlayWhenReady(true);
      }

      @Override
      public void onFailure() {
        Log.d(TAG, "==========onFailure ");

        if(mediaSource != null)
          exoplayer.prepare(mediaSource);

        exoplayer.setPlayWhenReady(true);
      }
    });

    AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId(SAMPLE_CHANNEL_ID)
        .streamId(SAMPLE_STREAM_ID)
        .build();

    InStreamManager.Companion.getInstance().requestAds(adsRequestData);

    fullscreenButton.setOnClickListener(v -> {
      if (fullscreen) {
        fullscreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                getBaseContext(),
                R.drawable.ic_fullscreen_open
            )
        );

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
          getSupportActionBar().show();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //player
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) exoplayerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (250 * getApplicationContext().getResources()
            .getDisplayMetrics().density);
        exoplayerView.setLayoutParams(params);

        fullscreen = false;
      } else {
        fullscreenButton.setImageDrawable(
            ContextCompat.getDrawable(
                getBaseContext(),
                R.drawable.ic_fullscreen_close
            )
        );

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (getSupportActionBar() != null) {
          getSupportActionBar().hide();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //player
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) exoplayerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        exoplayerView.setLayoutParams(params);

        fullscreen = true;
      }
    });
  }

  private MediaSource buildMediaSource(DataSource.Factory dataSourceFactory, String url) {
    Uri uri = Uri.parse(url);
    switch (Util.inferContentType(uri)) {
      case C.TYPE_DASH:
        return new DashMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_HLS:
        return new HlsMediaSource
            .Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(uri);
      case C.TYPE_SS:
        return new SsMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_OTHER:
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
    }
    return null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(exoplayer != null) {
      exoplayer.stop();
      exoplayer.release();
      exoplayer = null;
    }

    InStreamManager.Companion.getInstance().release();
  }
}
