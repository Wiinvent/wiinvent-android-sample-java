
### Version 1.10.13
Change log: Hỗ trợ quảng cáo banner

#### 1. SDK
```gralde
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.13'
```

#### 2. Hướng dẫn cập nhật
#### 2.1 Tích hợp quảng cáo display dùng cho trang chủ, chi tiết (dùng cho tableview, collection)
```java
//1. Khởi tạo DisplayBannerManager
        DisplayBannerManager.Companion.getInstance().init(
                this,
                "14",
                Environment.SANDBOX,
                10,
                true
        );

//2. Thêm listener cho DisplayBannerManager
        DisplayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
            @Override
            public void onDisplayAds(BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onDisplayAds");
        
                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onNoAds(BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager khong co ads de show 1");
            }

            @Override
            public void onAdsBannerDismiss(BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onAdsBannerDismiss");
        
                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        DisplayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerError(BannerAdView adView) {
                Log.d(TAG, "=========DisplayBannerManager onAdsWelcomeError");
        
                runOnUiThread(() -> {
                    if (adView != null) {
                        adView.setVisibility(View.GONE);
                        DisplayBannerManager.Companion.getInstance().releaseBanner(adView);
                    }
                });
            }

            @Override
            public void onAdsBannerClick(String clickThroughLink) {
                Log.d(TAG, "=========DisplayBannerManager onAdsBannerClick " + clickThroughLink);
            }
        });
```
```xml
//3. Thêm view banner ad vào layout
    <tv.wiinvent.wiinventsdk.ui.banner.BannerAdView
        android:id="@+id/banner_ad_display_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:visibility="visible"
        app:top_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginHorizontal="20dp"
    />
```

```java
//4. Request quảng cáo banner cho banner view với banner params tương ứng
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

        DisplayBannerManager.Companion.getInstance().requestAds(
                this,
                viewId,
                bannerAdsRequestData
                );

```
Clean up khi refresh toàn bộ giá trị banner data

```java
    DisplayBannerManager.Companion.getInstance().refreshBannerData();
```

#### 2.2 Tích hợp quảng cáo overlays (dùng cho quảng cáo pause player)
```java
//1. Khởi tạo OverlayBannerManager
        OverlayBannerManager.Companion.getInstance().init(
                this,
                        "14",
                Environment.SANDBOX,
                10,
                        true
);

//2. Thêm listener cho OverlayBannerManager
        OverlayBannerManager.Companion.getInstance().addBannerListener(new BannerAdEventListener() {
    @Override
    public void onDisplayAds(BannerAdView adView) {
        Log.d(TAG, "=========OverlayBannerManager onDisplayAds");

        runOnUiThread(() -> {
            if (adView != null) {
                adView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onNoAds(BannerAdView adView) {
        Log.d(TAG, "=========OverlayBannerManager khong co ads de show 1");
    }

    @Override
    public void onAdsBannerDismiss(BannerAdView adView) {
        Log.d(TAG, "=========OverlayBannerManager onAdsBannerDismiss");

        runOnUiThread(() -> {
            if (adView != null) {
                adView.setVisibility(View.GONE);
                OverlayBannerManager.Companion.getInstance().releaseBanner(adView);
            }
        });
    }

    @Override
    public void onAdsBannerError(BannerAdView adView) {
        Log.d(TAG, "=========OverlayBannerManager onAdsWelcomeError");

        runOnUiThread(() -> {
            if (adView != null) {
                adView.setVisibility(View.GONE);
                OverlayBannerManager.Companion.getInstance().releaseBanner(adView);
            }
        });
    }

    @Override
    public void onAdsBannerClick(String clickThroughLink) {
        Log.d(TAG, "=========OverlayBannerManager onAdsBannerClick " + clickThroughLink);
    }
});
```
```xml
//3. Thêm view banner ad vào vị trí tương ứng trên playerView
    <tv.wiinvent.wiinventsdk.ui.banner.BannerAdView
        android:id="@+id/banner_ad_overlay_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:visibility="visible"
        android:layout_marginBottom="100dp"
        app:layout_constraintBottom_toBottomOf="@id/video_frame"
        app:layout_constraintStart_toStartOf="@id/video_frame"
        app:layout_constraintEnd_toEndOf="@id/video_frame"
        app:layout_constraintWidth_percent="0.5"
        android:layout_marginHorizontal="20dp"
    />
```

```java
//4. Request quảng cáo banner cho banner view với banner params tương ứng
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

        OverlayBannerManager.Companion.getInstance().requestAds(
                this,
                R.id.banner_ad_overlay_view,
                bannerAdsRequestData
                );
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
| environment          | Môi trường sanbox hoặc production                                                 | constant |
| transId              | Mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin |   string |
| uid20                | Unified id 2.0 (nếu có)                                                           |   string |
| adSize               | Loại banner theo kích thước                                                       | constant |
| segments             | Segments                                                                          | constant |

2. Constant

| Key         | Description                                                                                                                                          |
|:------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------|
| env         | Environment.SANDBOX <br/> WI.Environment.PRODUCTION                                                                                                  |
| contentType | ContentType.TV <br/>WI.ContentType.FILM <br/>WI.ContentType.VIDEO <br/>WI.ContentType.SHORT_VOD                                                      |
| gender      | Gender.MALE <br/>WI.Gender.FEMALE <br/>WI.Gender.OTHER <br/>WI.Gender.NONE                                                                           |
| logLevel    | LevelLog.NONE <br/> LevelLog.BODY                                                                                                                    |
| adSize      | BannerDisplayAdSize.MEDIUM_BANNER (display banner) <br/> BannerAdSize.LARGE_BANNER (display banner) <br/> BannerAdSize.PAUSE_BANNER (overlay banner) |

