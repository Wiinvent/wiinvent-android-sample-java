package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.BannerManager;
import tv.wiinvent.wiinventsdk.interfaces.banner.BannerAdEventListener;
import tv.wiinvent.wiinventsdk.models.ads.BannerAdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.BannerAdSize;
import tv.wiinvent.wiinventsdk.models.type.ContentType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.banner.BannerAdView;

public class BannerActivity extends AppCompatActivity {

  private static final String TAG = BannerActivity.class.getCanonicalName();

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_banner);

    Button bannerButton = findViewById(R.id.reload_banner);
    bannerButton.setOnClickListener(v -> {
      showBanner(BannerAdSize.BANNER);
//            showBanner(BannerAdSize.RECTANGLE);
//            showBanner(BannerAdSize.LARGE_BANNER);
    });

    if(savedInstanceState == null) {
      initBanner();
      showBanner(BannerAdSize.BANNER);
//            showBanner(BannerAdSize.RECTANGLE);
//            showBanner(BannerAdSize.LARGE_BANNER);
    }

  }

  private void initBanner() {
    BannerManager.Companion.getInstance().init(this, "14", Environment.SANDBOX, 5, true);
    BannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
      @Override
      public void onDisplayAds(@Nullable BannerAdView bannerAdView) {
        Log.d(TAG, "=========onDisplayAds");

        runOnUiThread(() -> {
          if(bannerAdView != null) {
            bannerAdView.setVisibility(View.VISIBLE);
          }
        });

      }

      @Override
      public void onNoAds(@Nullable BannerAdView bannerAdView) {
        Log.d(TAG, "=========khong co ads de show 1");
      }

      @Override
      public void onAdsBannerDismiss(@Nullable BannerAdView bannerAdView) {
        Log.d(TAG, "=========onAdsBannerDismiss");
        runOnUiThread(() -> {
          if(bannerAdView != null) {
            bannerAdView.setVisibility(View.GONE);
            BannerManager.Companion.getInstance().releaseBanner(bannerAdView);
          }
        });
      }

      @Override
      public void onAdsBannerError(@Nullable BannerAdView bannerAdView) {
        Log.d(TAG, "=========onAdsBannerError");
        runOnUiThread(() -> {
          if(bannerAdView != null) {
            bannerAdView.setVisibility(View.GONE);
            BannerManager.Companion.getInstance().releaseBanner(bannerAdView);
          }
        });
      }

      @Override
      public void onAdsBannerClick(@NonNull String clickThroughLink) {
        Log.d(TAG, "=========onAdsBannerClick " + clickThroughLink);
      }
    });
  }

  private void showBanner(BannerAdSize adSize) {
    BannerAdsRequestData bannerAdsRequestData = new BannerAdsRequestData.Builder()
        .adSize(adSize) //các loại banner
        .contentType(ContentType.FILM) //content type TV | FILM | VIDEO
        .channelId("998989,2222") // danh sach category id cach nhau bang dau ,
        .streamId("999999") // id nội dung
        .title("Day la title noi dung") // tiêu đề nội dung
        .category("title category 1, title category 2") // title category cach nhau bang dau ,
        .transId("33333") // id nay cua server partner cung cap
        .color("#e5e5e5") // mau background cua banner
        .uid20("") // unified id 2.0, nếu không có thì set ""
        .build();

    int viewId = R.id.banner_ad_view;
    if(adSize == BannerAdSize.LARGE_BANNER) {
      viewId = R.id.banner_ad_large_banner_view;
    } else if(adSize == BannerAdSize.RECTANGLE) {
      viewId = R.id.banner_ad_rectangle_banner_view;
    }

    BannerManager.Companion.getInstance().requestAds(
        this, viewId, bannerAdsRequestData
    );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BannerManager.Companion.getInstance().release();
  }
}
