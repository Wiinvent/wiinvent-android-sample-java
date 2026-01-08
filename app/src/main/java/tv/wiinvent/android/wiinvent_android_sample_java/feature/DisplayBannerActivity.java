package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.DisplayBannerAdapter;
import tv.wiinvent.wiinventsdk.DisplayBannerManager;
import tv.wiinvent.wiinventsdk.interfaces.banner.BannerAdEventListener;
import tv.wiinvent.wiinventsdk.models.ads.DisplayBannerAdsRequestData;
import tv.wiinvent.wiinventsdk.models.type.BannerDisplayAdSize;
import tv.wiinvent.wiinventsdk.models.type.BannerDisplayType;
import tv.wiinvent.wiinventsdk.models.type.Environment;
import tv.wiinvent.wiinventsdk.ui.banner.BannerAdView;

public class DisplayBannerActivity extends AppCompatActivity {
    public static final String TAG = InStreamActivity.class.getCanonicalName();

    private String channelIdDefault = "998989";
    private String streamIdDefault = "999999";
    private String positionIdDefault = "homepage1";
    private BannerDisplayAdSize adSize = BannerDisplayAdSize.HOMEPAGE_BANNER;

    private static final String CONTENT_URL = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8";


    private ArrayList<Pair<String, BannerDisplayAdSize>> bannerParams = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_banner);

        Objects.requireNonNull(getSupportActionBar()).hide();

//        Button displayBannerButton = findViewById(R.id.display_banner);
//        displayBannerButton.setOnClickListener(v -> showDisplayBanner());

//        Button overlayBannerButton = findViewById(R.id.overlay_banner);

        initBannerList();
        initDisplayBannerManager();
        DisplayBannerAdapter displayBannerAdapter = new DisplayBannerAdapter(this, bannerParams);
        RecyclerView rvBanner = findViewById(R.id.rvBanner);
        rvBanner.setAdapter(displayBannerAdapter);
        rvBanner.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        SwipeRefreshLayout  swipeRefreshLayout = findViewById(R.id.srl_main);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDisplayBannerManager();
                DisplayBannerManager.Companion.getInstance().refreshBannerData();
                displayBannerAdapter.setBannerParams(new ArrayList<>());
                displayBannerAdapter.notifyDataSetChanged();

                displayBannerAdapter.setBannerParams(bannerParams);
                displayBannerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public void initBannerList() {
        bannerParams.add(new Pair("homepage1", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage1", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage2", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage2", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage3", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage3", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage4", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage4", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage5", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage5", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage6", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage6", BannerDisplayAdSize.SUBPAGE_BANNER));
        bannerParams.add(new Pair("homepage7", BannerDisplayAdSize.HOMEPAGE_BANNER));
        bannerParams.add(new Pair("subpage7", BannerDisplayAdSize.SUBPAGE_BANNER));
    }
    private void initDisplayBannerManager() {

        DisplayBannerManager.Companion.getInstance().init(
                this,
                "14",
                Environment.SANDBOX,
                10,
                true
        );

        DisplayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
            @Override
            public void onDisplayAds(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onDisplayAds");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onNoAds(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager khong co ads de show 1");
            }

            @Override
            public void onAdsBannerDismiss(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onAdsBannerDismiss");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        DisplayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerError(String positionId, BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onAdsWelcomeError");

                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        DisplayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerClick(String positionId, String clickThroughLink) {
                Log.d(TAG, "=========DisplayBannerManager onAdsBannerClick " + clickThroughLink);
            }
        });
    }


    public void showDisplayBanner() {
//        showDisplayBanner(
//                adSize,
//                BannerDisplayType.DISPLAY,
//                R.id.banner_ad_display_view,
//                positionIdDefault.isEmpty() ? "homepage1" : positionIdDefault
//        );
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
        DisplayBannerManager.Companion.getInstance().releaseBanner(bannerView);
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

        BannerAdView bannerAdView = findViewById(viewId);

        DisplayBannerManager.Companion.getInstance().requestAds(
                this,
                bannerAdView,
                bannerAdsRequestData
        );
    }



}
