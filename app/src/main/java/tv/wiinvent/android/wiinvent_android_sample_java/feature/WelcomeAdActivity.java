package tv.wiinvent.android.wiinvent_android_sample_java.feature;

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
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.InStreamManager;
import tv.wiinvent.wiinventsdk.OverlayManager;
import tv.wiinvent.wiinventsdk.WelcomeManager;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.interfaces.welcome.WelcomeAdEventListener;
import tv.wiinvent.wiinventsdk.models.OverlayData;
import tv.wiinvent.wiinventsdk.models.ads.AdInStreamEvent;
import tv.wiinvent.wiinventsdk.models.ads.AdsRequestData;
import tv.wiinvent.wiinventsdk.models.welcome.WelcomeAdData;
import tv.wiinvent.wiinventsdk.network.WiApiClient;
import tv.wiinvent.wiinventsdk.ui.OverlayView;

public class WelcomeAdActivity extends AppCompatActivity {
  public static final String TAG = WelcomeAdActivity.class.getCanonicalName();

  public static final String SAMPLE_ACCOUNT_ID = "14";

  private WelcomeManager welcomeManager;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
    setContentView(R.layout.activity_welcome);

    WelcomeAdData welcomeAdData = new WelcomeAdData.Builder().accountId(SAMPLE_ACCOUNT_ID)
        .env(WelcomeAdData.Environment.SANDBOX)
        .deviceType(WelcomeAdData.DeviceType.PHONE)
        .build();

    welcomeManager = new WelcomeManager(this, R.id.welcome_ad_view, welcomeAdData);
    welcomeManager.addWelcomeListener(new WelcomeAdEventListener() {
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
    if (welcomeManager.getIsAllowBack()) {
      super.onBackPressed();
    }
  }
}
