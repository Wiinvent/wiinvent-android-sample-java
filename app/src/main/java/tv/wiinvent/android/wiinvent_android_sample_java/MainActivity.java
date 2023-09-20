package tv.wiinvent.android.wiinvent_android_sample_java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.feature.GameActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.Home360InStreamActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.InStreamActivity;
import tv.wiinvent.wiinventsdk.AdsWelcomeManager;
import tv.wiinvent.wiinventsdk.interfaces.welcome.WelcomeAdsEventListener;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.welcomead.WelcomeAdView;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getCanonicalName();
  private WelcomeAdView welcomeAdView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    Objects.requireNonNull(getSupportActionBar()).hide();


    findViewById(R.id.instreamBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, InStreamActivity.class);
      startActivity(intent);
    });

    findViewById(R.id.votingBtn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, GameActivity.class);
      startActivity(intent);
    });

    findViewById(R.id.instream360Btn).setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, Home360InStreamActivity.class);
      startActivity(intent);
    });

    //Khoi tao welcome
    welcomeAdView = findViewById(R.id.welcome_ad_view);
    AdsWelcomeManager.Companion.getInstance().init(getBaseContext(),  "14", DeviceType.PHONE, Environment.SANDBOX, 10, "", true);
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
      AdsWelcomeManager.Companion.getInstance().requestAds(this,
          R.id.welcome_ad_view,
          R.layout.wisdk_welcome_tvc_detail,
          R.id.wisdk_exo_player_view,
          R.id.wisdk_skip_button);
    });
  }
}
