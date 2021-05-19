package tv.wiinvent.android.wiinvent_android_sample_java;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tv.wiinvent.wiinventsdk.OverlayManager;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.interfaces.ProfileListener;
import tv.wiinvent.wiinventsdk.models.OverlayData;
import tv.wiinvent.wiinventsdk.ui.OverlayView;

public class SampleProfileActivity extends AppCompatActivity {

    public static final String TAG = SampleProfileActivity.class.getCanonicalName();

    public static final String SAMPLE_ACCOUNT_ID = "<wiinvent cung cap>";
    public static final String SAMPLE_TOKEN = "jwt token";

    private OverlayManager overlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_profile);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initProfileOverlays();
        }
    }

    private void initProfileOverlays() {
        OverlayData overlayData = new OverlayData.Builder()
                .type(OverlayData.OverlayType.PROFILE)
                .accountId(SAMPLE_ACCOUNT_ID)
                .thirdPartyToken(SAMPLE_TOKEN)
                .env(OverlayData.Environment.DEV)
                .deviceType(OverlayData.DeviceType.PHONE)
                .build();

        overlayManager = new OverlayManager(this, R.id.wisdk_overlay_view, overlayData);
        overlayManager.addProfileListener(new ProfileListener() {
            @Override
            public void onVideoDetail(@NotNull String videoId) {

            }

            @Override
            public void onProfileClose() {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(overlayManager != null) {
            overlayManager.release();
            overlayManager = null;
        }
    }
}
