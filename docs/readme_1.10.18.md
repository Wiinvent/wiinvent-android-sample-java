### Version 1.10.18

Change log:
- Update SDK version to `1.10.18`.
- Gop thay doi tu `1.10.17`: bo sung report ads button cho Welcome Ads, InStream Ads, Display Banner va Overlay Banner.
- Update InStream lifecycle: goi `InStreamManager.pause()` trong `onPause()` va `InStreamManager.resume()` trong `onResume()`.
- Ke thua thay doi tu `1.10.16`: DisplayBanner dung `BannerDisplayAdSize.HOMEPAGE_BANNER` cho homepage thay cho `LARGE_BANNER`, va `BannerDisplayAdSize.SUBPAGE_BANNER` cho subpage thay cho `MEDIUM_BANNER`. `LARGE_BANNER` va `MEDIUM_BANNER` da deprecated.

#### 1. SDK
```gradle
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.18'
```

#### 2. Hướng dẫn cập nhật

#### 2.1 Tạo report ads button dùng chung
Custom report button cần extends `ReportButtonAds`, inflate layout của button và gọi `setReportButton(...)`.

```java
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tv.wiinvent.wiinventsdk.report.ReportButtonAds;

public class TV360ReportAdsButton extends ReportButtonAds {

    public TV360ReportAdsButton(@Nullable Context context) {
        super(context);
        init();
    }

    public TV360ReportAdsButton(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TV360ReportAdsButton(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.layout_report_ads_button, this);
        this.setReportButton(findViewById(R.id.report_ads_button));
    }
}
```

```xml
<!-- layout_report_ads_button.xml -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/report_ads_button"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:background="@drawable/report_button_bg"
        android:gravity="center"
        android:text="i"
        android:textAllCaps="false"
        android:textColor="#555"
        android:textSize="12sp"
        android:visibility="visible"
        app:layout_constraintDimensionRatio="h,1:1"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### 2.2 Tích hợp quảng cáo display dùng cho trang chủ, chi tiết (dùng cho tableview, collection)
```java
// 1. Khởi tạo DisplayBannerManager
DisplayBannerManager.Companion.getInstance().init(
        this,
        "14",
        Environment.SANDBOX,
        10,
        true
);

// 2. Thêm listener cho DisplayBannerManager
DisplayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
    @Override
    public void onHideReportButton(@NonNull String positionId, @Nullable ReportButtonAds reportButtonAds) {
        if (reportButtonAds != null) {
            reportButtonAds.hide();
        }
    }

    @Override
    public void onShowReportButton(@NonNull String positionId, @Nullable ReportButtonAds reportButtonAds) {
        if (reportButtonAds != null) {
            reportButtonAds.show(DisplayBannerActivity.this);
        }
    }

    @Override
    public void onDisplayAds(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onNoAds(String positionId, BannerAdView bannerAdView) {
        Log.d(TAG, "=========DisplayBannerManager khong co ads de show");
    }

    @Override
    public void onAdsBannerDismiss(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.GONE);
                DisplayBannerManager.Companion.getInstance().releaseBanner(bannerAdView);
            }
        });
    }

    @Override
    public void onAdsBannerError(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.GONE);
                DisplayBannerManager.Companion.getInstance().releaseBanner(bannerAdView);
            }
        });
    }

    @Override
    public void onAdsBannerClick(String positionId, String clickThroughLink) {
        Log.d(TAG, "=========DisplayBannerManager onAdsBannerClick " + clickThroughLink);
    }
});
```

```xml
<!-- 3. Thêm view banner ad và report button vào layout -->
<tv.wiinvent.wiinventsdk.ui.banner.BannerAdView
    android:id="@+id/banner_ad_display_view"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="20dp"
    android:visibility="visible"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />

<tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360ReportAdsButton
    android:id="@+id/banner_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:layout_marginEnd="10dp"
    android:visibility="gone"
    app:layout_constraintRight_toRightOf="@id/banner_ad_display_view"
    app:layout_constraintTop_toTopOf="@id/banner_ad_display_view" />
```

```java
// 4. Request quảng cáo display banner với report button
DisplayBannerAdsRequestData bannerAdsRequestData =
        new DisplayBannerAdsRequestData.Builder()
                .channelId(channelIdDefault.isEmpty() ? "998989" : channelIdDefault)
                .streamId(streamIdDefault.isEmpty() ? "999999" : streamIdDefault)
                .adSize(BannerDisplayAdSize.HOMEPAGE_BANNER)
                .bannerDisplayType(BannerDisplayType.DISPLAY)
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

BannerAdView bannerAdView = findViewById(R.id.banner_ad_display_view);
TV360ReportAdsButton reportAdsButton = findViewById(R.id.banner_report_button);

DisplayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        reportAdsButton,
        bannerAdsRequestData
);
```

Clean up khi refresh toàn bộ giá trị banner data:

```java
DisplayBannerManager.Companion.getInstance().refreshBannerData();
```

#### 2.3 Tích hợp quảng cáo overlays (dùng cho quảng cáo pause player)
```java
// 1. Khởi tạo OverlayBannerManager
OverlayBannerManager.Companion.getInstance().init(
        this,
        "14",
        Environment.SANDBOX,
        10,
        true
);

// 2. Thêm listener cho OverlayBannerManager
OverlayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
    @Override
    public void onHideReportButton(@NonNull String positionId, @Nullable ReportButtonAds reportButtonAds) {
        if (reportButtonAds != null) {
            reportButtonAds.hide();
        }
    }

    @Override
    public void onShowReportButton(@NonNull String positionId, @Nullable ReportButtonAds reportButtonAds) {
        if (reportButtonAds != null) {
            reportButtonAds.show(OverlayBannerActivity.this);
        }
    }

    @Override
    public void onDisplayAds(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onNoAds(String positionId, BannerAdView bannerAdView) {
        Log.d(TAG, "=========OverlayBannerManager khong co ads de show");
    }

    @Override
    public void onAdsBannerDismiss(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.GONE);
                OverlayBannerManager.Companion.getInstance().releaseBanner(bannerAdView, reportButtonAds);
            }
        });
    }

    @Override
    public void onAdsBannerError(
            @NonNull String positionId,
            @Nullable BannerAdView bannerAdView,
            @Nullable ReportButtonAds reportButtonAds
    ) {
        runOnUiThread(() -> {
            if (bannerAdView != null) {
                bannerAdView.setVisibility(View.GONE);
                OverlayBannerManager.Companion.getInstance().releaseBanner(bannerAdView, reportButtonAds);
            }
        });
    }

    @Override
    public void onAdsBannerClick(String positionId, String clickThroughLink) {
        Log.d(TAG, "=========OverlayBannerManager onAdsBannerClick " + clickThroughLink);
    }
});
```

```xml
<!-- 3. Thêm banner view và report button vào vị trí tương ứng trên playerView -->
<tv.wiinvent.wiinventsdk.ui.banner.BannerAdView
    android:id="@+id/banner_ad_overlay_view"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="20dp"
    android:layout_marginBottom="100dp"
    android:visibility="visible"
    app:layout_constraintBottom_toBottomOf="@id/video_frame"
    app:layout_constraintEnd_toEndOf="@id/video_frame"
    app:layout_constraintStart_toStartOf="@id/video_frame"
    app:layout_constraintWidth_percent="0.5" />

<tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360ReportAdsButton
    android:id="@+id/banner_report_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:layout_marginEnd="10dp"
    android:visibility="gone"
    app:layout_constraintRight_toRightOf="@id/banner_ad_overlay_view"
    app:layout_constraintTop_toTopOf="@id/banner_ad_overlay_view" />
```

```java
// 4. Request overlay banner với report button
DisplayBannerAdsRequestData bannerAdsRequestData =
        new DisplayBannerAdsRequestData.Builder()
                .channelId(channelIdDefault.isEmpty() ? "998989" : channelIdDefault)
                .streamId(streamIdDefault.isEmpty() ? "999999" : streamIdDefault)
                .adSize(BannerDisplayAdSize.PAUSE_BANNER)
                .bannerDisplayType(BannerDisplayType.OVERLAY)
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
TV360ReportAdsButton reportAdsButton = findViewById(R.id.banner_report_button);

OverlayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        bannerAdsRequestData,
        30,
        reportAdsButton
);
```

Clean up overlay banner:

```java
BannerAdView bannerView = findViewById(R.id.banner_ad_overlay_view);
TV360ReportAdsButton reportAdsButton = findViewById(R.id.banner_report_button);
OverlayBannerManager.Companion.getInstance().releaseBanner(bannerView, reportAdsButton);
```

#### 2.4 Tích hợp report button cho Welcome Ads
Từ `1.10.17`, `AdsWelcomeManager.requestAds(...)` nhận thêm layout/banner view cho welcome banner và `reportButtonId`.

```xml
<!-- Thêm report button vào các layout welcome: tvc, combo, banner sdk, banner detail -->
<tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360ReportAdsButton
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:layout_marginEnd="10dp"
    android:gravity="center"
    android:visibility="gone"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

```xml
<!-- Layout welcome banner detail cần WelcomeBannerAdView -->
<tv.wiinvent.wiinventsdk.ui.welcomead.WelcomeBannerAdView
    android:id="@+id/wisdk_banner_ad_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

```java
AdsWelcomeManager.Companion.getInstance().requestAds(
        this, // activity
        R.id.welcome_ad_view, // viewId
        R.layout.wisdk_welcome_tvc_detail, // tvcLayoutId
        R.id.wisdk_exo_player_view, // tvcPlayerViewId
        R.id.wisdk_ad_player_view, // tvcAdPlayerViewId
        R.id.wisdk_skip_button, // tvcSkipButtonId
        R.layout.wisdk_welcome_combo_detail, // comboLayoutId
        R.layout.wisdk_welcome_banner_sdk_detail, // bannerSdkLayoutId
        R.id.wisdk_web_view, // bannerSdkWebViewId
        R.layout.wisdk_welcome_banner_detail, // bannerLayoutId
        R.id.wisdk_banner_ad_view, // bannerAdViewId
        R.id.wisdk_background_img, // backgroundImgId
        R.id.wisdk_report_button, // reportButtonId
        adsRequestData
);
```

#### 2.5 Tích hợp report button cho InStream Ads
Thêm report button vào layout player:

```xml
<tv.wiinvent.android.wiinvent_android_sample_java.feature.ui.TV360ReportAdsButton
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_gravity="end"
    android:layout_marginTop="10dp"
    android:layout_marginEnd="10dp"
    android:gravity="center"
    android:visibility="gone"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

Khai báo và mapping view:

```java
private TV360ReportAdsButton reportButton = null;

reportButton = findViewById(R.id.wisdk_report_button);
```

Update `WiAdsLoaderListener` để SDK điều khiển trạng thái report button:

```java
InStreamManager.Companion.getInstance().setLoaderListener(new InStreamManager.WiAdsLoaderListener() {
    @Override
    public void showReportButton(@NonNull String campaignId) {
        if (reportButton != null) {
            reportButton.show(InStreamActivity.this);
        }
    }

    @Override
    public void hideReportButton(@NonNull String campaignId) {
        if (reportButton != null) {
            reportButton.hide();
        }
    }

    @Override
    public void resumeSkipButton() {
    }

    @Override
    public void pauseSkipButton() {
    }

    // Implement các callback còn lại theo logic hiện tại của app.
});
```

Truyền `reportButton` khi request InStream ads:

```java
InStreamManager.Companion.getInstance().requestAds(
        adsRequestData,
        adPlayerView,
        player,
        registerFriendlyObstruction(),
        reportButton
);
```

Đăng ký report button là friendly obstruction:

```java
FriendlyObstruction reportButtonOb = InStreamManager.Companion.getInstance().createFriendlyObstruction(
        reportButton,
        FriendlyObstructionPurpose.OTHER,
        "Report Button"
);
friendlyObstructionList.add(reportButtonOb);
```

Từ `1.10.18`, cần đồng bộ lifecycle của InStreamManager:

```java
@Override
protected void onPause() {
    super.onPause();

    if (skipButton != null) {
        skipButton.pause();
    }

    InStreamManager.Companion.getInstance().pause();
}

@Override
protected void onResume() {
    super.onResume();

    if (skipButton != null) {
        skipButton.resume();
    }

    InStreamManager.Companion.getInstance().resume();
}
```

#### 3. Mô tả các tham số
1. Parameter

| Key                  | Description                                                                       |     Type |
|:---------------------|:----------------------------------------------------------------------------------|---------:|
| tenantId / accountId | id đối tác được cung cấp bởi wiinvent                                             |  integer |
| positionId           | Id vị trí quảng cáo, dùng để phân biệt các vị trí quảng cáo trong một màn hình    |   string |
| channelId            | Danh sách id của category của nội dung & cách nhau bằng dấu ","                   |   string |
| category             | Danh sach tiêu đề category của nội dung & cách nhau bằng dấu ","                  |   string |
| streamId             | Id nội dung                                                                       |   string |
| title                | Tiêu đề của nội dung                                                              |   string |
| loadTimeout          | Banner Load Timeout                                                               |  integer |
| environment          | Môi trường sandbox hoặc production                                                | constant |
| transId              | Mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin |   string |
| uid20                | Unified id 2.0 (nếu có)                                                           |   string |
| adSize               | Loại banner theo kích thước                                                       | constant |
| segments             | Segments                                                                          | constant |
| reportButtonAds      | Button report quảng cáo, extends từ `ReportButtonAds`                             |     view |
| reportButtonId       | Id của report button trong layout welcome ads                                     |  integer |

2. Constant

| Key         | Description                                                                                                                                                                               |
|:------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| env         | Environment.SANDBOX <br/> Environment.PRODUCTION                                                                                                                                          |
| contentType | ContentType.TV <br/> ContentType.FILM <br/> ContentType.VIDEO <br/> ContentType.SHORT_VOD                                                                                                |
| gender      | Gender.MALE <br/> Gender.FEMALE <br/> Gender.OTHER <br/> Gender.NONE                                                                                                                     |
| logLevel    | LevelLog.NONE <br/> LevelLog.BODY                                                                                                                                                         |
| adSize      | BannerDisplayAdSize.SUBPAGE_BANNER (display banner for subpage) <br/> BannerDisplayAdSize.HOMEPAGE_BANNER (display banner for homepage) <br/> BannerDisplayAdSize.PAUSE_BANNER (overlay banner) |
