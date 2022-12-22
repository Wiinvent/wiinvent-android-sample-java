package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.AdsWelcomeManager;
import tv.wiinvent.wiinventsdk.interfaces.welcome.WelcomeAdEventListener;

public class WelcomeAdActivity extends AppCompatActivity {
  public static final String TAG = WelcomeAdActivity.class.getCanonicalName();

  public static final String SAMPLE_ACCOUNT_ID = "14";

  private AdsWelcomeManager adsWelcomeManager;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
    setContentView(R.layout.activity_welcome);

    adsWelcomeManager = new AdsWelcomeManager();
    adsWelcomeManager.showAds(this, R.id.welcome_ad_view, 10);
    adsWelcomeManager.addWelcomeListener(new WelcomeAdEventListener() {
      @Override
      public void onAdsWelcomeDismiss() {
        Log.d(TAG, "===> onDismiss");
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            finish();
          }
        });
      }

      @Override
      public void onAdsWelcomeClick(long campaignId) {
        Log.d(TAG, "===> onAdsWelcomeClick: " + campaignId);
      }

      @Override
      public void onAdsWelcomeImpression(long campaignId) {
        Log.d(TAG, "===> onAdsWelcomeImpression: " + campaignId);
      }

      @Override
      public void onAdsWelcomeSkip(long campaignId) {
        Log.d(TAG, "===> onAdsWelcomeSkip: " + campaignId);
      }

      @Override
      public void onAdsWelcomeTimeout() {
        Log.d(TAG, "===> onAdsWelcomeTimeout");
      }

      @Override
      public void onAdsWelcomeError() {
        Log.d(TAG, "===> onAdsWelcomeError");
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (adsWelcomeManager.getIsAllowBack()) {
      super.onBackPressed();
    }
  }
}
