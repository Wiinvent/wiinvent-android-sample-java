package tv.wiinvent.android.wiinvent_android_sample_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import kotlin.jvm.internal.MagicApiIntrinsics;
import tv.wiinvent.wiinventsdk.OverlayManager;
import tv.wiinvent.wiinventsdk.interfaces.DefaultOverlayEventListener;
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener;
import tv.wiinvent.wiinventsdk.interfaces.ProfileListener;
import tv.wiinvent.wiinventsdk.interfaces.UserActionListener;
import tv.wiinvent.wiinventsdk.models.ConfigData;
import tv.wiinvent.wiinventsdk.models.OverlayData;
import tv.wiinvent.wiinventsdk.models.StreamSource;
import tv.wiinvent.wiinventsdk.ui.OverlayView;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = MainActivity.class.getCanonicalName();

    public static final String SAMPLE_ACCOUNT_ID = "81";
    public static final String SAMPLE_CHANNEL_ID = "54";
    public static final String SAMPLE_STREAM_ID = "127";
    public static final String SAMPLE_TOKEN = "3001";

    private PlayerView exoplayerView = null;
    private SimpleExoPlayer exoplayer;
    private MediaSessionCompat mediaSession;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private  PlaybackStateCompat.Builder playbackStateBuilder;

    private OverlayManager overlayManager;
    private OverlayView overlayView;

    private Boolean fullscreen = false;
    private ImageView fullscreenButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exoplayerView = findViewById(R.id.simple_exo_player_view);
        fullscreenButton = findViewById(R.id.exo_fullscreen_icon);
        overlayView = findViewById(R.id.wisdk_overlay_view);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializePlayer();
            initializeOverlays();

//            initProfileOverlays();
        }
    }


    private void initializePlayer() {
        //init exoplayer 0, 1 , 2 , 3 ...
        concatenatingMediaSource = new ConcatenatingMediaSource();
        ComponentName componentName = new ComponentName(getBaseContext(), "Exo");

        exoplayer = new SimpleExoPlayer.Builder(getBaseContext()).build();
        exoplayerView.setPlayer(exoplayer);
        exoplayerView.setUseController(true);

        playbackStateBuilder = new PlaybackStateCompat.Builder();
        playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_FAST_FORWARD);

        mediaSession = new MediaSessionCompat(getBaseContext(), "ExoPlayer", componentName, null);
        mediaSession.setPlaybackState(playbackStateBuilder.build());
        mediaSession.setActive(true);

        exoplayer.setPlayWhenReady(true);
        exoplayer.prepare(buildMediaSource("https://wiinvent.tv/videos/rewiew_air_tag.mp4"));


        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullscreen) {
                    fullscreenButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                    getBaseContext(),
                    R.drawable.ic_fullscreen_open
                        )
                    );

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if(getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    //player
                    ConstraintLayout.LayoutParams params = ( ConstraintLayout.LayoutParams) exoplayerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = (int) (250 * getApplicationContext().getResources()
                            .getDisplayMetrics().density);
                    exoplayerView.setLayoutParams(params);

                    //overlays
                    overlayView.setLayoutParams(params);

                    fullscreen = false;
                } else {
                    fullscreenButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                    getBaseContext(),
                                    R.drawable.ic_fullscreen_close
                            )
                    );

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    if(getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    //player
                    ConstraintLayout.LayoutParams params = ( ConstraintLayout.LayoutParams) exoplayerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    exoplayerView.setLayoutParams(params);

                    //overlays
                    overlayView.setLayoutParams(params);

                    fullscreen = true;
                }
            }
        });
    }

    private void initializeOverlays() {
//        OverlayData overlayData = null;
        OverlayData overlayData = new OverlayData.Builder()
                .mappingType(OverlayData.MappingType.THIRDPARTY)
                .accountId(SAMPLE_ACCOUNT_ID)
                .channelId(SAMPLE_CHANNEL_ID)
                .streamId(SAMPLE_STREAM_ID)
                .thirdPartyToken(SAMPLE_TOKEN)
                .env(OverlayData.Environment.DEV)
                .deviceType(OverlayData.DeviceType.PHONE)
                .contentType(OverlayData.ContentType.LIVESTREAM)
                .build();

        overlayManager = new OverlayManager(this, R.id.wisdk_overlay_view, overlayData);

        overlayManager.addPlayerListener(new PlayerChangeListener(){

            @Nullable
            @Override
            public Long getCurrentPosition() {

                return exoplayer != null ? exoplayer.getCurrentPosition() : 0L;
            }
        });

        // Add player event listeners to determine overlay visibility.
        exoplayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "====onPlayerStateChanged playWhenReady: $playWhenReady - $playbackState");

                if(overlayManager != null)
                    overlayManager.setVisible(playWhenReady && playbackState == Player.STATE_READY);
            }
        });
    }

    private MediaSource buildMediaSource(String url) {
        String userAgent = Util.getUserAgent(getApplicationContext(), "Exo");
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getBaseContext(), userAgent);
        Uri uri = Uri.parse(url);
        switch (Util.inferContentType(uri)) {
            case C.TYPE_DASH:
                return new DashMediaSource
                        .Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource
                        .Factory(dataSourceFactory)
                        .setAllowChunklessPreparation(true)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource
                        .Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
        }
        return null;
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
