package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.OverlayBannerManager;
import tv.wiinvent.wiinventsdk.interfaces.banner.BannerAdEventListener;
import tv.wiinvent.wiinventsdk.models.ads.DisplayBannerAdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.BannerDisplayAdSize;
import tv.wiinvent.wiinventsdk.models.type.BannerDisplayType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.banner.BannerAdView;

public class OverlayBannerActivity extends AppCompatActivity {
    public static final String TAG = OverlayBannerActivity.class.getCanonicalName();

    private PlayerView playerView = null;
    private ExoPlayer player = null;

    private String channelIdDefault = "998989";
    private String streamIdDefault = "999999";
    private String positionIdDefault = "homepage1";
    private BannerDisplayAdSize adSize = BannerDisplayAdSize.HOMEPAGE_BANNER;

    private static final String CONTENT_URL = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlays_banner);
        playerView = findViewById(R.id.player_view);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Button overlayBannerButton = findViewById(R.id.overlay_banner);
        overlayBannerButton.setOnClickListener(v -> loadPlayer());

        initOverlayBannerManager();
        initializePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        OverlayBannerManager.Companion.getInstance().release();

    }

    private void initOverlayBannerManager() {

        OverlayBannerManager.Companion.getInstance().init(
                this,
                "14",
                Environment.SANDBOX,
                10,
                true
        );

        OverlayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
            @Override
            public void onDisplayAds(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========OverlayBannerManager onDisplayAds");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onNoAds(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========OverlayBannerManager khong co ads de show 1");
            }

            @Override
            public void onAdsBannerDismiss(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========OverlayBannerManager onAdsBannerDismiss");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        OverlayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerError(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========OverlayBannerManager onAdsWelcomeError");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        OverlayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerClick(String positionId, String clickThroughLink) {
                Log.d(TAG, "=========OverlayBannerManager onAdsBannerClick " + clickThroughLink);
            }
        });
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(getBaseContext()).build();
        playerView.setPlayer(player);
        playerView.setUseController(true);

        player.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && !player.getPlayWhenReady()) {
                    Log.e(TAG, "ExoPlayer is paused");
                    showOverlayBanner();
                } else if (playbackState == Player.STATE_READY && player.getPlayWhenReady()) {
                    dismissOverlayBanner();
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                if (!playWhenReady && player.getPlaybackState() == Player.STATE_READY) {
                    Log.d(TAG, "ExoPlayer is paused");
                    showOverlayBanner();
                } else if (player.getPlaybackState() == Player.STATE_READY && playWhenReady) {
                    dismissOverlayBanner();
                }
            }
        });

        // loadPlayer();
    }

    public void showOverlayBanner() {
        showDisplayBanner(
                BannerDisplayAdSize.PAUSE_BANNER,
                BannerDisplayType.OVERLAY,
                R.id.banner_ad_overlay_view,
                ""
        );
    }

    public void dismissOverlayBanner() {
        BannerAdView bannerView = findViewById(R.id.banner_ad_overlay_view);
        OverlayBannerManager.Companion.getInstance().releaseBanner(bannerView);
    }

    public void showDisplayBanner(
            BannerDisplayAdSize adSize,
            BannerDisplayType displayType,
            int viewId,
            String positionId
    ) {
        DisplayBannerAdsRequestData bannerAdsRequestData =
                new DisplayBannerAdsRequestData.Builder()
                        .channelId(channelIdDefault.isEmpty() ? "998989" : channelIdDefault)
                        .streamId(streamIdDefault.isEmpty() ? "999999" : streamIdDefault)
                        .adSize(adSize)
                        .bannerDisplayType(displayType)
                        .title("Day la title")
                        .category("category 1, category 2")
                        .transId("1112222222")
                        // .age(30)
                        // .gender(Gender.FEMALE)
                        .uid20("123123123")
                        .color("#ffffff00")
                        .segments("a3,34,d3,d3")
                        .positionId(positionId)
                        .build();

        BannerAdView bannerAdView = findViewById(R.id.banner_ad_overlay_view);
        OverlayBannerManager.Companion.getInstance().requestAds(
                this,
                bannerAdView,
                bannerAdsRequestData,
                30
        );
    }


    private void loadPlayer() {
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(getBaseContext());
        MediaSource mediaSource = buildMediaSource(dataSourceFactory, CONTENT_URL);

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(DataSource.Factory dataSourceFactory, String url) {
        Uri uri = Uri.parse(url);
        int type = Util.inferContentType(uri);

        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(uri));

            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .setAllowChunklessPreparation(true)
                        .createMediaSource(MediaItem.fromUri(uri));

            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(uri));

            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(uri));

            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }


    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
