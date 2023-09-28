package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.interactivemedia.v3.api.FriendlyObstruction;
import com.google.ads.interactivemedia.v3.api.FriendlyObstructionPurpose;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360SkipAdsButtonAds;
import tv.wiinvent.wiinventsdk.InStreamManager;
import tv.wiinvent.wiinventsdk.logging.LevelLog;
import tv.wiinvent.wiinventsdk.models.ads.AdInStreamEvent;
import tv.wiinvent.wiinventsdk.models.ads.AdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;

public class Home360InStreamActivity extends AppCompatActivity {
  public static final String TAG = Home360InStreamActivity.class.getCanonicalName();

  public static final String SAMPLE_ACCOUNT_ID = "14";
  public static final String SAMPLE_CHANNEL_ID = "998989";
  public static final String SAMPLE_STREAM_ID = "667788";

  private static final String CONTENT_URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";

  private PlayerView exoplayerView = null;
  private SimpleExoPlayer exoplayer;

  private TV360SkipAdsButtonAds skipButton = null;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home360_detail_activity);
    Objects.requireNonNull(getSupportActionBar()).hide();

    exoplayerView = findViewById(R.id.player_view);
    skipButton = findViewById(R.id.skip_button);

    initializePlayer();
  }

  private void initializePlayer() {
    ComponentName componentName = new ComponentName(getBaseContext(), "Exo");

    exoplayer = new SimpleExoPlayer.Builder(getBaseContext()).build();
    exoplayerView.setPlayer(exoplayer);
    exoplayerView.setUseController(false);

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

    InStreamManager.Companion.getInstance().init(getBaseContext(), SAMPLE_ACCOUNT_ID, DeviceType.PHONE, Environment.SANDBOX, 10, 10, 10, LevelLog.BODY, 5, true);
    InStreamManager.Companion.getInstance().setLoaderListener(new InStreamManager.WiAdsLoaderListener() {
      @Override
      public void showSkipButton(@NonNull String campaignId, int duration) {
        Log.d(TAG, "=========InStreamManager showSkipButton " + duration + " --- " + skipButton);
        if(skipButton != null) {
          skipButton.startCountdown(duration);
        }
      }

      @Override
      public void onTimeout() {
        InStreamManager.Companion.getInstance().release();
      }

      @Override
      public void hideSkipButton(@NonNull String campaignId) {
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

    //khai bao friendly obstruction
    List<FriendlyObstruction> friendlyObstructionList = new ArrayList<>();
    FriendlyObstruction skipButtonObstruction = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        skipButton,
        FriendlyObstructionPurpose.CLOSE_AD,
        "This is close ad"
    );

    friendlyObstructionList.add(skipButtonObstruction);

    AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId(SAMPLE_CHANNEL_ID)
        .streamId(SAMPLE_STREAM_ID)
        .build();

    InStreamManager.Companion.getInstance().requestAds(adsRequestData, friendlyObstructionList);
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

  @Override
  protected void onPause() {
    super.onPause();

    if(skipButton != null) {
      skipButton.pause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(skipButton != null) {
      skipButton.resume();
    }
  }
}
