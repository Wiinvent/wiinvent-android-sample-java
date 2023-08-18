package tv.wiinvent.android.wiinvent_android_sample_java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tv.wiinvent.android.wiinvent_android_sample_java.feature.Home360InStreamActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.InStreamActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.GameActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.WelcomeAdActivity;
import tv.wiinvent.wiinventsdk.interfaces.welcome.WelcomeAdsLoaderListener;
import tv.wiinvent.wiinventsdk.loaders.WelcomeAdsLoader;
import tv.wiinvent.wiinventsdk.models.type.DeviceType;
import tv.wiinvent.wiinventsdk.models.type.Environment;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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

    findViewById(R.id.welcomeBtn).setOnClickListener(v -> {
      WelcomeAdsLoader.Companion.getInstance().init(getBaseContext() ,"4", DeviceType.PHONE, Environment.PRODUCTION, 10);
      WelcomeAdsLoader.Companion.getInstance().requestAds(new WelcomeAdsLoaderListener() {
        @Override
        public void onDisplayAds() {
          Intent intent = new Intent(MainActivity.this, WelcomeAdActivity.class);
          startActivity(intent);
        }

        @Override
        public void onNoAds() {
          Log.d("TAG", "=========khong co ads de show");
        }
      });
    });
  }
}
