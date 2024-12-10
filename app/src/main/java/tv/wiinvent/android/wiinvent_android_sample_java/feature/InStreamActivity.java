package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.interactivemedia.v3.api.FriendlyObstruction;
import com.google.ads.interactivemedia.v3.api.FriendlyObstructionPurpose;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
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
import tv.wiinvent.wiinventsdk.ui.instream.player.AdPlayerView;

public class InStreamActivity extends AppCompatActivity {
  private static final String DRM_LICENSE_URL = "https://license.uat.widevine.com/cenc/getcontentkey/widevine_test";

  //DUng 1 trong 2 luong;

  // Luong live
  private static final ContentType contentType = ContentType.TV;
  private static final String CONTENT_URL = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8";

  //Cho luong TV
//  private static final ContentType contentType = ContentType.FILM;
//  private static final String CONTENT_URL = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";

  private float currentVolume = 0F;

  public static final String TAG = InStreamActivity.class.getCanonicalName();
  public static final String SAMPLE_ACCOUNT_ID = "14";
  private PlayerView playerView = null;
  private AdPlayerView adPlayerView = null;
  private ExoPlayer player;

  private View videoZoomStatus = null;
  private View playerViewSpherical = null;
  private View overlayView = null;
  private View subtitlesView = null;
  private View playerCoverIv = null;
  private View controlView = null;
  private TV360SkipAdsButtonAds skipButton = null;
  private View containerDonateMessage = null;
  private View playerTopBarLl = null;
  private View layoutListEpisodesFullscreen = null;
  private View txtFingerPrint = null;

  private Button btnChangeSource = null;
  private boolean isHaveAds = true;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_instream);
    Objects.requireNonNull(getSupportActionBar()).hide();

    playerView = findViewById(R.id.player_view);
    adPlayerView = findViewById(R.id.ad_player_view);

    videoZoomStatus = findViewById(R.id.video_zoom_status);
    playerViewSpherical = findViewById(R.id.player_view_spherical);
    overlayView = findViewById(R.id.wisdk_overlay_view);
    subtitlesView = findViewById(R.id.subtitles_view);
    playerCoverIv = findViewById(R.id.player_cover_iv);
    controlView = findViewById(R.id.control_view);
    skipButton = findViewById(R.id.skip_button);
    containerDonateMessage = findViewById(R.id.container_donate_message);
    playerTopBarLl = findViewById(R.id.player_top_bar_ll);
    layoutListEpisodesFullscreen = findViewById(R.id.layout_list_episodes_fullscreen);
    txtFingerPrint = findViewById(R.id.txtFingerPrint);
    btnChangeSource = findViewById(R.id.btnChangeSource);
    btnChangeSource.setOnClickListener(view -> changeSource());

    initializePlayerAndVast();
  }

  private void initializePlayerAndVast() {
    player = new ExoPlayer.Builder(getBaseContext()).build();
    playerView.setPlayer(player);
    playerView.setUseController(false);

    if(player != null) {
      currentVolume = player.getVolume();
    }
    InStreamManager.Companion.getInstance().init(getBaseContext(), "14", DeviceType.PHONE, Environment.SANDBOX, 10, 5, 5, 2000, LevelLog.BODY, 6, true);
    InStreamManager.Companion.getInstance().setLoaderListener(new InStreamManager.WiAdsLoaderListener() {

      @Override
      public void showContentPlayer() {
        if(contentType == ContentType.TV) {
          if(player != null) {
            player.setVolume(currentVolume);
          }
        } else {
          if(player != null) {
            player.play();
          }
        }

        if(playerView != null) {
          playerView.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void hideContentPlayer() {
        if(contentType == ContentType.TV) {
          if(player != null) {
            currentVolume = player.getVolume();
            player.setVolume(0F);
          }
        } else {
          if(player != null) {
            player.pause();
          }
        }

        if(playerView != null) {
          playerView.setVisibility(View.GONE);
        }
      }

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
        if (skipButton != null) {
          skipButton.resume();
          skipButton.startCountdown(duration);
        }
      }

      @Override
      public void hideSkipButton(@NonNull String campaignId) {
        Log.d(TAG, "=========InStreamManager hideSkipButton ");

        if (skipButton != null) {
          skipButton.hide();
        }
      }

      @Override
      public void onTimeout() {
        Log.d(TAG, "==========onTimeout ");
        //Write log
      }

      @Override
      public void onFailure() {
        Log.d(TAG, "==========onFailure ");
        //Write log
      }
    });

//    String contentUrl = "https://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";

    changeSource();
  }

  private void changeSource() {
    InStreamManager.Companion.getInstance().release();

    String userAgent = Util.getUserAgent(this, "Exo");
    AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId("998989,222222") // danh sách id của category của nội dung & cách nhau bằng dấu ,
        .streamId("999999") // id nội dung 0877
        .contentType(ContentType.FILM) //content type TV | FILM | VIDEO
        .title("Highlights Áo vs Thổ Nhĩ Kỳ | Giao Hữu Quốc Tế 2024") // tiêu đề nội dung
        .category("danh muc 1, danh muc 2") // danh sach tiêu đề category của nội dung & cách nhau bằng dấu ,
        .keyword("key word 1, keyword 2") // từ khoá nếu có | để "" nếu ko có
        .transId("01sssss") //mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin
        .uid20("") // unified id 2.0, nếu không có thì set ""
        .segments("23,23,23,23") //segtment id lay tu server doi tac
        .build();

    DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
    httpDataSourceFactory.setUserAgent(userAgent);
    httpDataSourceFactory.setTransferListener(new DefaultBandwidthMeter.Builder(this)
        .setResetOnNetworkTypeChange(false).build());

    MediaSource mediaSource = buildMediaSource(buildDataSourceFactory(httpDataSourceFactory), CONTENT_URL, getDrmSessionManager(httpDataSourceFactory));

    InStreamManager.Companion.getInstance()
        .requestAds(adsRequestData,
            adPlayerView,
            player,
            registerFriendlyObstruction());
    player.setMediaSource(mediaSource);
    player.prepare();

    player.setPlayWhenReady(true);
  }

  private List<FriendlyObstruction> registerFriendlyObstruction() {
    // Khai bao friendly obstruction
    List<FriendlyObstruction> friendlyObstructionList = new ArrayList<>();

    FriendlyObstruction videoZoomStatusOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        videoZoomStatus,
        FriendlyObstructionPurpose.VIDEO_CONTROLS,
        "Video zoom status"
    );
    friendlyObstructionList.add(videoZoomStatusOb);

    FriendlyObstruction playerViewSphericalOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        playerViewSpherical,
        FriendlyObstructionPurpose.OTHER,
        "Player other"
    );
    friendlyObstructionList.add(playerViewSphericalOb);

    FriendlyObstruction overlayViewOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        overlayView,
        FriendlyObstructionPurpose.OTHER,
        "Overlay"
    );
    friendlyObstructionList.add(overlayViewOb);

    FriendlyObstruction subtitlesViewOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        subtitlesView,
        FriendlyObstructionPurpose.OTHER,
        "Subtitles"
    );
    friendlyObstructionList.add(subtitlesViewOb);

    FriendlyObstruction playerCoverIvOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        playerCoverIv,
        FriendlyObstructionPurpose.OTHER,
        "Player Cover"
    );
    friendlyObstructionList.add(playerCoverIvOb);

    FriendlyObstruction controlViewOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        controlView,
        FriendlyObstructionPurpose.VIDEO_CONTROLS,
        "Control"
    );
    friendlyObstructionList.add(controlViewOb);

    FriendlyObstruction skipAdsButtonAdsOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        skipButton,
        FriendlyObstructionPurpose.CLOSE_AD,
        "Skip Button"
    );
    friendlyObstructionList.add(skipAdsButtonAdsOb);

    FriendlyObstruction containerDonateMessageOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        containerDonateMessage,
        FriendlyObstructionPurpose.OTHER,
        "Container Donate"
    );
    friendlyObstructionList.add(containerDonateMessageOb);

    FriendlyObstruction playerTopBarLlOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        playerTopBarLl,
        FriendlyObstructionPurpose.OTHER,
        "Top Bar"
    );
    friendlyObstructionList.add(playerTopBarLlOb);

    FriendlyObstruction layoutListEpisodesFullscreenOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        layoutListEpisodesFullscreen,
        FriendlyObstructionPurpose.OTHER,
        "Episodes Fullscreen"
    );
    friendlyObstructionList.add(layoutListEpisodesFullscreenOb);

    FriendlyObstruction txtFingerPrintOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        txtFingerPrint,
        FriendlyObstructionPurpose.OTHER,
        "Txt FingerPrint"
    );
    friendlyObstructionList.add(txtFingerPrintOb);

    FriendlyObstruction btnChangeSourceOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        btnChangeSource,
        FriendlyObstructionPurpose.OTHER,
        "Btn Change Source"
    );
    friendlyObstructionList.add(btnChangeSourceOb);

    return friendlyObstructionList;
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

    if (player != null) {
      player.stop();
      player.release();
      player = null;
    }

    InStreamManager.Companion.getInstance().release();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (player != null)
      player.setPlayWhenReady(false);

    if (skipButton != null)
      skipButton.pause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null)
      player.setPlayWhenReady(true);

    if (skipButton != null)
      skipButton.resume();
  }
}
