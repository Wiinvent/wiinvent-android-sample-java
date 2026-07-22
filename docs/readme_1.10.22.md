### Version 1.10.20

Change log:
- Update SDK version to `1.10.20`.
- Bổ sung tham số `adPendingTime` cho tất cả request ads (gửi lên backend qua param `apt`).
- `ReportButtonAds` không còn là `abstract`, đã có layout mặc định. Host **không bắt buộc** phải kế thừa nữa.
- Bổ sung `InfoButtonAds` — nút thông tin quảng cáo, dùng cùng cơ chế với `ReportButtonAds`.
- **Display Banner & Overlay Banner**: SDK tự tạo và gắn report button, info button, skip button vào bên trong `BannerAdView`. **Breaking change**: `requestAds(...)` bỏ tham số `reportButton`, `releaseBanner(...)` của Overlay Banner bỏ tham số `reportButton`.
- **Welcome Ads**: `requestAds(...)` bổ sung tham số `infoAdsButtonId`. **Breaking change**.
- Đổi vị trí report button từ bên phải sang bên trái ở layout InStream và Welcome Ads; info button nằm bên trái report button.
- Cập nhật icon report/info, icon close của overlay banner và style checkbox trong màn hình report.
- Kế thừa toàn bộ thay đổi từ `1.10.19`.

Mô tả các tham số bổ sung:
- `uid` — id người dùng. Gửi lên backend qua param `uid`.
- `userImpressionLimit` — giới hạn số lần hiển thị quảng cáo theo user trong ngày. Gửi lên backend qua param `uil`.
- `adPendingTime` — thời gian chờ xuất hiện giữa 2 lần quảng cáo, đơn vị giây. Gửi lên backend qua param `apt`.

#### 1. SDK
```gradle
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.22'
```

#### 2. Hướng dẫn cập nhật

##### 2.1 Bổ sung tham số `adPendingTime`

Áp dụng cho tất cả request data: `AdsRequestData` (InStream), `BannerAdsRequestData`, `DisplayBannerAdsRequestData`, `WelcomeAdsRequestData`.

```java
AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId("998989")
        .streamId("999999")
        .transId("a23334343")
        .uid("123123123")
        .userImpressionLimit(1)
        .adPendingTime(2)            // <- tham số mới
        .segments("123,3fef3,f3f,3f")
        .build();
```

```java
DisplayBannerAdsRequestData bannerAdsRequestData =
        new DisplayBannerAdsRequestData.Builder()
                .channelId("998989")
                .streamId("999999")
                .adSize(BannerDisplayAdSize.HOMEPAGE_BANNER)
                .bannerDisplayType(BannerDisplayType.DISPLAY)
                .transId("1112222222")
                .uid("123123123")
                .userImpressionLimit(20)
                .adPendingTime(2)            // <- tham số mới
                .color("#ffffff00")
                .segments("a3,34,d3,d3")
                .positionId(positionId)
                .build();
```

> Lưu ý: `adPendingTime` không có giá trị mặc định trong constructor của các data class. Nếu đang khởi tạo request data bằng constructor trực tiếp (không qua `Builder`) thì phải truyền thêm tham số này. Dùng `Builder` thì mặc định là `null`.

##### 2.2 Report button và Info button

Từ `1.10.20`, `ReportButtonAds` là `open class` và đã có layout mặc định. Có 2 cách dùng:

**Cách 1 — dùng trực tiếp view của SDK (khuyến nghị):**

```xml
<tv.wiinvent.wiinventsdk.report.ReportButtonAds
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="24dp"
    android:visibility="gone"
    app:layout_constraintStart_toEndOf="@id/wisdk_info_button"
    app:layout_constraintTop_toTopOf="parent" />
```

**Cách 2 — custom lại giao diện:** kế thừa và override `init()`.

```java
public class TV360ReportAdsButton extends ReportButtonAds {

    public TV360ReportAdsButton(@Nullable Context context) {
        super(context);
    }

    public TV360ReportAdsButton(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public TV360ReportAdsButton(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.layout_report_ads_button, this);
        this.setReportButton(findViewById(R.id.report_ads_button));
    }
}
```

> Lưu ý: lớp cha đã gọi `init()` trong constructor, không cần tự gọi lại `init()` như bản cũ.

`InfoButtonAds` dùng y hệt cơ chế trên:

```java
public class Tv360AdsInfoButton extends InfoButtonAds {

    public Tv360AdsInfoButton(Context context) {
        super(context);
    }

    public Tv360AdsInfoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Tv360AdsInfoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.layout_info_ads_button, this);
        this.setReportButton(findViewById(R.id.info_ads_button));
    }
}
```

```xml
<!-- layout_info_ads_button.xml -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/info_ads_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/bg_ads_info"
        android:gravity="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

##### 2.3 Display Banner — bỏ report button khỏi layout và `requestAds`

SDK tự tạo report button + info button và gắn vào trong `BannerAdView`. Host **xoá report button khỏi layout**:

```xml
<!-- Layout chỉ còn BannerAdView, KHÔNG khai báo report button nữa -->
<tv.wiinvent.wiinventsdk.ui.banner.BannerAdView
    android:id="@+id/banner_ad_display_view"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="20dp"
    android:visibility="visible"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

```java
// Trước (1.10.19)
DisplayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        reportAdsButton,          // <- bỏ
        bannerAdsRequestData
);

// Sau (1.10.20)
DisplayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        bannerAdsRequestData
);
```

Clean up không đổi:

```java
DisplayBannerManager.Companion.getInstance().releaseBanner(bannerAdView);
DisplayBannerManager.Companion.getInstance().refreshBannerData();
```

##### 2.4 Overlay Banner — bỏ report button, SDK tự dựng skip button

Tương tự Display Banner, ngoài ra SDK còn tự tạo skip (close) button và hiển thị theo `skipOffSet` của campaign. Host **xoá report button và skip button khỏi layout**, chỉ giữ `BannerAdView`:

```xml
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
```

```java
// Trước (1.10.19)
OverlayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        bannerAdsRequestData,
        30,
        reportAdsButton           // <- bỏ
);

// Sau (1.10.20)
OverlayBannerManager.Companion.getInstance().requestAds(
        this,
        bannerAdView,
        bannerAdsRequestData,
        30
);
```

Clean up cũng bỏ tham số report button:

```java
// Trước (1.10.19)
OverlayBannerManager.Companion.getInstance().releaseBanner(bannerView, reportAdsButton);

// Sau (1.10.20)
OverlayBannerManager.Companion.getInstance().releaseBanner(bannerView);
```

##### 2.5 Welcome Ads — bổ sung `infoAdsButtonId`

Thêm info button với id `wisdk_info_button` vào **cả 4 layout welcome**: `wisdk_welcome_tvc_detail`, `wisdk_welcome_combo_detail`, `wisdk_welcome_banner_sdk_detail`, `wisdk_welcome_banner_detail`.

Report button đồng thời chuyển từ bên phải sang bên trái, nằm ngay sau info button:

```xml
<com.example.sample.ui.Tv360AdsInfoButton
    android:id="@+id/wisdk_info_button"
    android:layout_width="30dp"
    android:layout_height="24dp"
    android:layout_marginStart="10dp"
    android:layout_marginTop="10dp"
    android:gravity="center"
    android:visibility="gone"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />

<com.example.sample.ui.TV360ReportAdsButton
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_marginStart="10dp"
    android:layout_marginTop="10dp"
    android:gravity="center"
    android:visibility="gone"
    app:layout_constraintStart_toEndOf="@id/wisdk_info_button"
    app:layout_constraintTop_toTopOf="parent" />
```

Truyền thêm `infoAdsButtonId` khi request:

```java
AdsWelcomeManager.Companion.getInstance().requestAds(
        this,                                      // activity
        R.id.welcome_ad_view,                      // viewId
        R.layout.wisdk_welcome_tvc_detail,         // tvcLayoutId
        R.id.wisdk_exo_player_view,                // tvcPlayerViewId
        R.id.wisdk_ad_player_view,                 // tvcAdPlayerViewId
        R.id.wisdk_skip_button,                    // tvcSkipButtonId
        R.layout.wisdk_welcome_combo_detail,       // comboLayoutId
        R.layout.wisdk_welcome_banner_sdk_detail,  // bannerSdkLayoutId
        R.id.wisdk_web_view,                       // bannerSdkWebViewId
        R.layout.wisdk_welcome_banner_detail,      // bannerLayoutId
        R.id.wisdk_banner_ad_view,                 // bannerAdViewId
        R.id.wisdk_background_img,                 // backgroundImgId
        R.id.wisdk_report_button,                  // reportButtonId
        R.id.wisdk_info_button,                    // infoAdsButtonId <- tham số mới
        adsRequestData
);
```

##### 2.6 InStream Ads — đổi vị trí report button

`requestAds(...)` của `InStreamManager` không đổi. Chỉ cập nhật layout player: report button chuyển từ bên phải sang bên trái.

```xml
<!-- Trước (1.10.19): căn phải -->
<!--
<com.example.sample.ui.TV360ReportAdsButton
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_gravity="end"
    android:layout_marginEnd="10dp"
    android:layout_marginTop="10dp"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
-->

<!-- Sau (1.10.20): căn trái -->
<com.example.sample.ui.TV360ReportAdsButton
    android:id="@+id/wisdk_report_button"
    android:layout_width="30dp"
    android:layout_height="wrap_content"
    android:layout_gravity="start"
    android:layout_marginStart="10dp"
    android:layout_marginTop="10dp"
    android:gravity="center"
    android:visibility="gone"
    app:layout_constraintTop_toTopOf="parent" />
```

#### 3. Tổng hợp thay đổi resources

##### 3.1 Resources bên trong SDK (host không cần làm gì)

Các resource này đã được đóng gói sẵn trong AAR, liệt kê để tiện đối chiếu giao diện:

| Resource                                     | Trạng thái | Mô tả                                                             |
|:---------------------------------------------|:-----------|:------------------------------------------------------------------|
| `layout/report_ads_button.xml`               | Mới        | Layout mặc định của `ReportButtonAds` (`ImageView` id `report_ads_button`) |
| `layout/info_ads_button.xml`                 | Mới        | Layout mặc định của `InfoButtonAds` (`ImageView` id `info_ads_button`) |
| `drawable/ic_report_ads.xml`                 | Mới        | Icon report quảng cáo                                              |
| `drawable/ic_info_ads.xml`                   | Mới        | Icon thông tin quảng cáo                                           |
| `drawable/ic_close_banner.xml`               | Mới        | Icon close/skip của overlay banner                                 |
| `drawable/close.xml`                         | **Đã xoá** | Thay bằng `ic_close_banner.xml`                                    |
| `drawable/ic_report_checkbox.xml`            | Mới        | Selector checkbox trong màn hình report                            |
| `drawable/ic_report_checkbox_selected.xml`   | Mới        | Trạng thái checked                                                 |
| `drawable/ic_report_checkbox_unselected.xml` | Mới        | Trạng thái unchecked                                               |
| `layout/report_item.xml`                     | Sửa        | Gắn `android:button="@drawable/ic_report_checkbox"`                |

> Nếu app đang tham chiếu trực tiếp `@drawable/close` của SDK thì phải đổi sang `@drawable/ic_close_banner`.

##### 3.2 Resources phía host cần thêm / cập nhật

| Resource                                   | Việc cần làm | Chi tiết                                                                    |
|:-------------------------------------------|:-------------|:-----------------------------------------------------------------------------|
| `layout/layout_info_ads_button.xml`        | **Thêm mới** | Layout cho custom info button (xem mục 2.2). Chỉ cần khi tự custom `InfoButtonAds`. |
| `drawable/bg_ads_info.xml`                 | **Thêm mới** | Icon cho info button (nếu custom)                                            |
| `layout/layout_report_ads_button.xml`      | Cập nhật     | Đổi `TextView` → `ImageView`, `wrap_content` → `match_parent`, dùng `android:src` thay `android:background` |
| `layout/wisdk_welcome_tvc_detail.xml`      | Cập nhật     | Thêm `wisdk_info_button`; report button chuyển sang trái                      |
| `layout/wisdk_welcome_combo_detail.xml`    | Cập nhật     | Thêm `wisdk_info_button`; report button chuyển sang trái                      |
| `layout/wisdk_welcome_banner_sdk_detail.xml` | Cập nhật   | Thêm `wisdk_info_button`; report button chuyển sang trái                      |
| `layout/wisdk_welcome_banner_detail.xml`   | Cập nhật     | Thêm `wisdk_info_button`; report button chuyển sang trái                      |
| Layout player InStream                     | Cập nhật     | Report button chuyển sang trái (xem mục 2.6)                                  |
| Layout Display Banner                      | Cập nhật     | **Xoá** report button khỏi layout (xem mục 2.3)                               |
| Layout item Display Banner (recycler/list) | Cập nhật     | **Xoá** report button khỏi layout item                                        |
| Layout Overlay Banner                      | Cập nhật     | **Xoá** report button và skip button khỏi layout (xem mục 2.4)                |

Mẫu `layout_report_ads_button.xml` sau khi cập nhật:

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/report_ads_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/ic_report_ads"
        android:gravity="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### 4. Mô tả các tham số

1. Parameter (bổ sung so với `1.10.19`)

| Key             | Description                                                                       |    Type |
|:----------------|:----------------------------------------------------------------------------------|--------:|
| uid                 | id người dùng. Gửi lên backend qua param `uid`.                               |  string |
| userImpressionLimit | giới hạn số lần hiển thị quảng cáo theo user trong ngày. Gửi lên backend qua param `uil`. | integer |
| adPendingTime   | thời gian chờ xuất hiện giữa 2 lần quảng cáo, đơn vị giây. Gửi lên backend qua param `apt`. | integer |
| infoAdsButtonId | Id của info button trong layout welcome ads                                        | integer |
| infoButtonAds   | Button thông tin quảng cáo, extends từ `InfoButtonAds`                             |    view |

2. Tổng hợp thay đổi API

| Manager               | Thay đổi                                                                       |
|:----------------------|:-------------------------------------------------------------------------------|
| InStreamManager       | Request data thêm `adPendingTime`. Signature `requestAds(...)` không đổi, chỉ đổi vị trí report button trong layout. |
| BannerManager         | Request data thêm `adPendingTime`. Signature `requestAds(...)` không đổi.       |
| DisplayBannerManager  | Request data thêm `adPendingTime`. `requestAds(...)` **bỏ** `reportButton`.     |
| OverlayBannerManager  | Request data thêm `adPendingTime`. `requestAds(...)` và `releaseBanner(...)` **bỏ** `reportButton`. |
| AdsWelcomeManager     | Request data thêm `adPendingTime`. `requestAds(...)` **thêm** `infoAdsButtonId`. |
