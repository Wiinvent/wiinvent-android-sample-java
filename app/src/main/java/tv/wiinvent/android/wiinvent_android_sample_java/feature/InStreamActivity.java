package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.ads.interactivemedia.v3.api.FriendlyObstruction;
import com.google.ads.interactivemedia.v3.api.FriendlyObstructionPurpose;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360SkipAdsButtonAds;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.utils.VideoCache;
import tv.wiinvent.wiinventsdk.InStreamManager;
import tv.wiinvent.wiinventsdk.logging.LevelLog;
import tv.wiinvent.wiinventsdk.models.ads.AdInStreamEvent;
import tv.wiinvent.wiinventsdk.models.ads.AdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.ContentType;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;

public class InStreamActivity extends AppCompatActivity {
  private static final String DRM_LICENSE_URL = "https://license.uat.widevine.com/cenc/getcontentkey/widevine_test";
  private static final String CONTENT_URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";

  public static final String TAG = InStreamActivity.class.getCanonicalName();
  public static final String SAMPLE_ACCOUNT_ID = "14";
  private StyledPlayerView playerView = null;
  private ExoPlayer player;
  private Boolean fullscreen = false;
  private ImageView fullscreenButton = null;
  private TV360SkipAdsButtonAds skipButton = null;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_instream);
    Objects.requireNonNull(getSupportActionBar()).hide();

    playerView = findViewById(R.id.simple_exo_player_view);
    fullscreenButton = findViewById(R.id.exo_fullscreen_icon);
    skipButton = findViewById(R.id.skip_button);

    initializePlayerAndVast();
//    initFullscreen();
  }

  private void initFullscreen() {

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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (250 * getApplicationContext().getResources()
            .getDisplayMetrics().density);
        playerView.setLayoutParams(params);

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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);

        fullscreen = true;
      }
    });
  }

  private void initializePlayerAndVast() {
    String userAgent = Util.getUserAgent(this, "Exo");

    player = new ExoPlayer.Builder(getBaseContext()).build();
    playerView.setPlayer(player);
    playerView.setUseController(false);

    InStreamManager.Companion.getInstance().init(getBaseContext(), SAMPLE_ACCOUNT_ID, DeviceType.PHONE, Environment.SANDBOX, 5, 5, 5, 2000, LevelLog.BODY, 6, true);
    InStreamManager.Companion.getInstance().setLoaderListener(new InStreamManager.WiAdsLoaderListener() {

      @Override
      public void onEvent(@NonNull AdInStreamEvent event) {
        Log.d(TAG, "==========event " + event.getEventType() + " - " + event.getCampaignId());
      }


      @Override
      public void onEventSkip(@NonNull String s) {

      }

      @Override
      public void showSkipButton(@NonNull String campaignId, int duration) {
        Log.d(TAG, "=========InStreamManager showSkipButton " + duration + " --- " + skipButton);
        if(skipButton != null) {
          skipButton.startCountdown(duration);
        }
      }

      @Override
      public void hideSkipButton(@NonNull String campaignId) {
        Log.d(TAG, "=========InStreamManager hideSkipButton ");

        if(skipButton != null) {
          skipButton.hide();
        }
      }

      @Override
      public void onTimeout() {
        InStreamManager.Companion.getInstance().release();
      }

      @Override
      public void onFailure() {
        Log.d(TAG, "==========onFailure ");

        InStreamManager.Companion.getInstance().release();
      }
    });

//    String contentUrl = "https://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";


    //khai bao friendly obstruction
    List<FriendlyObstruction> friendlyObstructionList = new ArrayList<>();
    FriendlyObstruction skipButtonObstruction = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        skipButton,
        FriendlyObstructionPurpose.CLOSE_AD,
        "This is close ad"
    );
    friendlyObstructionList.add(skipButtonObstruction);

    AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId("998989,222222") // danh sách id của category của nội dung & cách nhau bằng dấu ,
        .streamId("999999") // id nội dung
        .contentType(ContentType.FILM) //content type TV | FILM | VIDEO
        .title("Day la title cua noi dung") // tiêu đề nội dung
        .category("danh muc 1, danh muc 2") // danh sach tiêu đề category của nội dung & cách nhau bằng dấu ,
        .keyword("key word 1, keyword 2") // từ khoá nếu có | để "" nếu ko có
        .transId("01sssss") //mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin
        .uid20("") // unified id 2.0, nếu không có thì set ""
        .build();

    DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
    httpDataSourceFactory.setUserAgent(userAgent);
    httpDataSourceFactory.setTransferListener(new DefaultBandwidthMeter.Builder(this)
        .setResetOnNetworkTypeChange(false).build());

    MediaSource mediaSource = buildMediaSource(buildDataSourceFactory(httpDataSourceFactory), CONTENT_URL, getDrmSessionManager(httpDataSourceFactory));

    DefaultMediaSourceFactory defaultMediaSourceFactory = new DefaultMediaSourceFactory(this);
    AdsMediaSource adsMediaSource = InStreamManager.Companion.getInstance()
        .requestAds(adsRequestData,
            mediaSource,
            playerView,
            player,
            defaultMediaSourceFactory,
            friendlyObstructionList);

    player.addMediaSource(adsMediaSource);
    player.prepare();

    player.setPlayWhenReady(true);
  }

  private DrmSessionManager getDrmSessionManager(DefaultHttpDataSource.Factory dataSourceFactory) {
    try {
      HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(DRM_LICENSE_URL, dataSourceFactory);
      return new DefaultDrmSessionManager(C.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), drmCallback, null, true, 10);
    } catch (Exception e) {
      return null;
    }
  }

  public DataSource.Factory buildDataSourceFactory(DefaultHttpDataSource.Factory httpDataSourceFactory) {
    return buildReadOnlyCacheDataSource(httpDataSourceFactory, VideoCache.getInstance(this).getCache());
  }
  protected static CacheDataSource.Factory buildReadOnlyCacheDataSource(
      DataSource.Factory upstreamFactory, Cache cache) {
    return new CacheDataSource.Factory().setCache(cache)
        .setUpstreamDataSourceFactory(upstreamFactory)
        .setCacheReadDataSourceFactory(new FileDataSource.Factory())
        .setCacheWriteDataSinkFactory(null)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        .setEventListener(null);
  }

  private MediaSource buildMediaSource(DataSource.Factory dataSourceFactory, String url, DrmSessionManager drmSessionManager) {
    Uri uri = Uri.parse(url);
    switch (Util.inferContentType(uri)) {
      case C.TYPE_DASH:
        return new DashMediaSource
            .Factory(new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
            .setDrmSessionManagerProvider(mediaItem -> drmSessionManager)
            .setManifestParser(new DashManifestParser() {
              @NonNull
              @Override
              public DashManifest parse(@NonNull Uri uri, @NonNull InputStream inputStream) throws IOException {
                return super.parse(uri, inputStream);
              }
            }).createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_HLS:
        return new HlsMediaSource
            .Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_SS:
        return new SsMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_OTHER:
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri));
      default:
        throw new IllegalStateException("Unexpected value: " + Util.inferContentType(uri));
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(player != null) {
      player.stop();
      player.release();
      player = null;
    }

    InStreamManager.Companion.getInstance().release();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if(player != null)
      player.setPlayWhenReady(false);

    if(skipButton != null)
      skipButton.pause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(player != null)
      player.setPlayWhenReady(true);

    if(skipButton != null)
      skipButton.resume();
  }
}
