package tv.wiinvent.android.wiinvent_android_sample_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.exoplayer.ExoPlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tv.wiinvent.wiinventsdk.OverlayManager;
import tv.wiinvent.wiinventsdk.interfaces.DefaultOverlayEventListener;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.interfaces.UserActionListener;
import tv.wiinvent.wiinventsdk.models.ConfigData;
import tv.wiinvent.wiinventsdk.models.OverlayData;

public class MainActivity extends AppCompatActivity {

    public static final String SAMPLE_ACCOUNT_ID = "1";
    public static final String SAMPLE_CHANNEL_ID = "34";
    public static final String SAMPLE_STREAM_ID = "";

    private ExoPlayer exoPlayer;
    private OverlayManager overlayManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializePlayer();
            initializeOverlays();
        }
    }


    private void initializePlayer() {
        //init exoplayer 0, 1 , 2 , 3 ...

    }

    private void initializeOverlays() {
        OverlayData overlayData = new OverlayData.Builder()
                .mappingType(OverlayData.MappingType.WI)
             //   .accountId(SAMPLE_ACCOUNT_ID)
                .channelId(SAMPLE_CHANNEL_ID)
              //  .streamId(SAMPLE_STREAM_ID)
                .env(OverlayData.Environment.DEV)
                .deviceType(OverlayData.DeviceType.PHONE)
                .build();

        overlayManager = new OverlayManager(this, R.id.wisdk_overlay_view, overlayData);

        overlayManager.addUserPlayerListener(new UserActionListener() {
            @Override
            public void onVoted(@NotNull String s, @NotNull String s1, @Nullable String s2, @NotNull String s3, @NotNull String s4, @NotNull String s5, @NotNull String s6, int i) {

            }

            @Override
            public void onUserPurchase(@NotNull String s, @NotNull String s1) {

            }

            @Override
            public void onTokenExpire() {

            }
        });

        overlayManager.addOverlayListener(new DefaultOverlayEventListener() {

            @Override
            public void onWebViewBrowserOpen() {

            }

            @Override
            public void onWebViewBrowserContentVisible(boolean b) {

            }

            @Override
            public void onWebViewBrowserClose() {

            }

            @Override
            public void onTimeout() {

            }

            @Override
            public void onLoadError() {

            }

            @Override
            public void onConfigReady(@NotNull ConfigData configData) {

                //ready Overlays


            }
        });

        overlayManager.addPlayerListener(new PlayerChangeListener(){

            @Nullable
            @Override
            public Long getCurrentPosition() {

                return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
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
