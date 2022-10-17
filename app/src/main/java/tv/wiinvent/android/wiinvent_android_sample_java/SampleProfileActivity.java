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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import tv.wiinvent.wiinventsdk.interfaces.AdEventListener;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.interfaces.ProfileListener;
import tv.wiinvent.wiinventsdk.models.AdEvent;
import tv.wiinvent.wiinventsdk.models.OverlayData;
import tv.wiinvent.wiinventsdk.ui.OverlayView;

public class SampleProfileActivity extends AppCompatActivity {

    public static final String TAG = SampleProfileActivity.class.getCanonicalName();

    public static final String SAMPLE_ACCOUNT_ID = "30";
    public static final String SAMPLE_TOKEN = "MmMyanROTExYUGZOUkRmL3J5U1BVQT09fHxBRHx8aHR0cDovL3MxaW1nLm15Y2xpcC52bi9pbWFnZTEvMjAyMC8wMy8wMy8xNTgzMjA2MjkyNTY3L2M1Mjg1NzJjYzc0Zl8xODBfMTgwLmpwZ3x8MTYyMTU2MzY5MQ%3D%3D%26%26%26ZBHLuWGTS6lvsZpzppkRwf5GTm1NHPpg0xz%2FIzIcsLSLPli7K7RMLFB9hjJD03EzQ0P%2FMJxeaqOoWOjHXdOBobApWiexQ0TncwUwTUPeQIRc5sxwLlLzkOlmuiDT5pBSzIjazEyAz27qpYrikpUd9d%2BWTHnV1byoloPTV5ogXWJGKplnswPDRaIIWusIEppZInRmWPT8pWRFxikB6boQNTQWkCbJtFg%2FpxmYXEoHE4AexBonJH7Ma9%2Fqp4VXXrwcvE3h85QbEeHdbiLtpwn%2BOL%2FPyjICWn1PblZtS5Az2TAY9Z7OnSc%2BAKZzxnzomxfZenixdnmpkobWDals28imNw%3D%3D";

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
            public void onVideoDetail(@NonNull String s, boolean b) {

            }

            @Override
            public void onCategoryDetail(@NonNull String s) {

            }

            @Override
            public void onProfileClose() {
                Log.d(TAG, "onProfileClose: ");
            }

            @Override
            public void onLogin() {
                Log.d(TAG, "onLogin: ");
            }
        });

        overlayManager.addAdEventListener(new AdEventListener() {
            @Override
            public void onAdsEvent(@NonNull AdEvent adEvent) {
                switch (adEvent.getEventType()) {
                    case START:

                        break;
                    case IMPRESSION:

                        break;
                }
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
