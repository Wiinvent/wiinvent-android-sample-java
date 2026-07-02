package tv.wiinvent.android.wiinvent_android_sample_java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

//import tv.wiinvent.android.wiinvent_android_sample_java.feature.BitmovinInStreamActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.BannerActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.DisplayBannerActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.OverlayBannerActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.backup.GameActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.InStreamActivity;
import tv.wiinvent.wiinventsdk.AdsWelcomeManager;
import tv.wiinvent.wiinventsdk.interfaces.welcome.WelcomeAdsEventListener;
import tv.wiinvent.wiinventsdk.models.ads.WelcomeAdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.welcomead.WelcomeAdView;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getCanonicalName();
  private WelcomeAdView welcomeAdView;

  private WebView webView;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Objects.requireNonNull(getSupportActionBar()).hide();


    findViewById(R.id.instreamBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, InStreamActivity.class);
      startActivity(intent);
    });

//    findViewById(R.id.bitmovinInstreamBtn).setOnClickListener(v -> {
//      Intent intent = new Intent(MainActivity.this, BitmovinInStreamActivity.class);
//      startActivity(intent);
//    });

    findViewById(R.id.votingBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, GameActivity.class);
      startActivity(intent);
    });

    // ===> banner
    findViewById(R.id.overlayBannerBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, OverlayBannerActivity.class);
      startActivity(intent);
    });

    findViewById(R.id.displayBannerBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, DisplayBannerActivity.class);
      startActivity(intent);
    });

    initWelcome();
  }

  private void initWelcome() {
    //Khoi tao welcome
    welcomeAdView = findViewById(R.id.welcome_ad_view);
    AdsWelcomeManager.Companion.getInstance().init(getBaseContext(),
            "14", // tenant id giống với instream, phụ thuộc vào môi trường (prod|sandbox) có thể khác nhau (lấy config từ backend)
            Environment.SANDBOX, // môi truòng prod | sandbox, giong instream
            5, // load vast timeout (lấy config từ backend), giong instream
            5, // load media timeout (lấy config từ backend), giong instream
            5, // buffer media timeout (lấy config từ backend), giong instream
            2000,  // max bitrate (lấy config từ backend), giong instream
            "",
            8,  // thời gian bo qua quang cao welcome (config dc tu phia backend)
            true); // debug
    AdsWelcomeManager.Companion.getInstance().addWelcomeListener(new WelcomeAdsEventListener() {
      @Override
      public void onDisplayAds() {
        Log.d(TAG, "=========onDisplayAds");
        runOnUiThread(() -> {
          if (welcomeAdView != null) {
            welcomeAdView.setVisibility(View.VISIBLE);
          }
        });
      }

      @Override
      public void onNoAds() {
        Log.d(TAG, "=========onNoAds");
      }

      @Override
      public void onAdsWelcomeDismiss() {
        Log.d(TAG, "=========onAdsWelcomeDismiss");
        runOnUiThread(() -> {
          if (welcomeAdView != null) {
            welcomeAdView.setVisibility(View.GONE);
          }
        });
      }

      @Override
      public void onAdsWelcomeError() {
        Log.d(TAG, "=========onAdsWelcomeError");
        runOnUiThread(() -> {
          if (welcomeAdView != null) {
            welcomeAdView.setVisibility(View.GONE);
          }
        });
      }
    });


    findViewById(R.id.welcomeBtn).setOnClickListener(v -> {
      WelcomeAdsRequestData adsRequestData =  new WelcomeAdsRequestData.Builder()
              .transId("300000") //mã giao dịch tạo từ server đối tác - client liên hệ server
              .uid("123123123") // userId của người dùng, nếu không có thì set ""
              .userImpressionLimit(20) // giới hạn số lần hiển thị theo user, không giới hạn thì set 0
              .segments("123,123,123,123") //segments lay tu server doi tac
              .build();

      /**
       * requestAds(
       *      activity = this
       *       viewId = R.id.welcome_ad_view,
       *       tvcLayoutId = R.layout.wisdk_welcome_tvc_detail,
       *       tvcPlayerViewId = R.id.wisdk_exo_player_view,
       *       tvcAdPlayerViewId = R.id.wisdk_ad_player_view,
       *       tvcSkipButtonId = R.id.wisdk_skip_button,
       *       comboLayoutId = R.layout.wisdk_welcome_combo_detail,
       *       bannerSdkLayoutId = R.layout.wisdk_welcome_banner_sdk_detail,
       *       bannerSdkWebViewId = R.id.wisdk_web_view,
       *       bannerLayoutId = R.layout.wisdk_welcome_banner_detail,
       *       bannerAdViewId = R.id.wisdk_banner_ad_view,
       *       backgroundImgId = R.id.wisdk_background_img,
       *       reportButtonId = R.id.wisdk_report_button,
       *       adsRequestData = welcomeAdsRequestData,
       *       )
       */

      AdsWelcomeManager.Companion.getInstance().requestAds(
              this, // activity
              R.id.welcome_ad_view, // viewId
              R.layout.wisdk_welcome_tvc_detail, // tvcLayoutId
              R.id.wisdk_exo_player_view, // tvcPlayerViewId
              R.id.wisdk_ad_player_view, // tvcAdPlayerViewId

              R.id.wisdk_skip_button, // tvcSkipButtonId

              R.layout.wisdk_welcome_combo_detail, // comboLayoutId

              R.layout.wisdk_welcome_banner_sdk_detail, // bannerSdkLayoutId
              R.id.wisdk_web_view, // bannerSdkWebViewId

              R.layout.wisdk_welcome_banner_detail, // bannerLayoutId
              R.id.wisdk_banner_ad_view, // bannerAdViewId

              R.id.wisdk_background_img, // backgroundImgId
              R.id.wisdk_report_button, // reportButtonId
              adsRequestData);
    });
  }
}
