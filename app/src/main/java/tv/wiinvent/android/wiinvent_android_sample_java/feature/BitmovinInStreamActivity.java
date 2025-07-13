package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.source.SourceConfig;
import com.google.ads.interactivemedia.v3.api.FriendlyObstruction;
import com.google.ads.interactivemedia.v3.api.FriendlyObstructionPurpose;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360SkipAdsButtonAds;
import tv.wiinvent.wiinventsdk.InStreamManager;
import tv.wiinvent.wiinventsdk.logging.LevelLog;
import tv.wiinvent.wiinventsdk.models.ads.AdInStreamEvent;
import tv.wiinvent.wiinventsdk.models.ads.AdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.ContentType;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.instream.player.AdPlayerView;

public class BitmovinInStreamActivity extends AppCompatActivity {
  // Luong live
  private static final ContentType contentType = ContentType.TV;

  private int currentVolume = 0;

  public static final String TAG = BitmovinInStreamActivity.class.getCanonicalName();
  public static final String SAMPLE_ACCOUNT_ID = "14";
  private PlayerView playerView = null;
  private AdPlayerView adPlayerView = null;
  private Player player;

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

  private String analyticsLicenseKey = "2b6d64c1-afd6-4d1c-9b8c-5c514271172d";

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bitmovin_instream);
    Objects.requireNonNull(getSupportActionBar()).hide();

    playerView = findViewById(R.id.bitmovinPlayerView);
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

    initializePlayerAndVast();
  }

  private void initializePlayerAndVast() {
    player = PlayerFactory.create(getBaseContext(), new PlayerConfig(), new AnalyticsConfig(analyticsLicenseKey));
    playerView.setPlayer(player);

    String contentUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8";


    if(player != null) {
      currentVolume = player.getVolume();
    }
    InStreamManager.Companion.getInstance().init(getBaseContext(), "14", DeviceType.PHONE, Environment.SANDBOX, 10, 5, 5, 2000, LevelLog.BODY, 6);
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
            player.setVolume(0);
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

    AdsRequestData adsRequestData = new AdsRequestData.Builder()
            .channelId("998989,222222") // danh sách id của category của nội dung & cách nhau bằng dấu ,
            .streamId("911") // id nội dung 0877
            .contentType(ContentType.FILM) //content type TV | FILM | VIDEO
            .title("Highlights Áo vs Thổ Nhĩ Kỳ | Giao Hữu Quốc Tế 2024") // tiêu đề nội dung
            .category("danh muc 1, danh muc 2") // danh sach tiêu đề category của nội dung & cách nhau bằng dấu ,
            .keyword("key word 1, keyword 2") // từ khoá nếu có | để "" nếu ko có
            .transId("01sssss") //mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin
            .uid20("") // unified id 2.0, nếu không có thì set ""
            .segments("23,23,23,23") //segtment id lay tu server doi tac
            .build();

    InStreamManager.Companion.getInstance()
            .requestAdsForBitmovin(adsRequestData,
                    adPlayerView,
                    player,
                    registerFriendlyObstruction());

    player.load(SourceConfig.fromUrl(contentUrl));
    player.play();
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

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (player != null) {
      player.onStop();
      player.destroy();
      player = null;
    }

    InStreamManager.Companion.getInstance().release();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (player != null)
      player.onPause();

    if (skipButton != null)
      skipButton.pause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null)
      player.onResume();

    if (skipButton != null)
      skipButton.resume();
  }
}
