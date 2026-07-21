### Version 1.10.19

Change log:
- Update SDK version to `1.10.19`.
- Bổ sung 2 tham số mới cho tất cả request ads (gửi lên backend): `uid` (userId) và `userImpressionLimit`.
- Đổi tên tham số `uid20` thành `uid` trong `AdsRequestData` (InStream). **Breaking change**: `.uid20(...)` không còn, dùng `.uid(...)`.
- Kế thừa toàn bộ thay đổi từ `1.10.18`.

#### 1. SDK
```gradle
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.19'
```

#### 2. Hướng dẫn cập nhật

Từ `1.10.19`, tất cả request data đều hỗ trợ 2 builder mới:

- `.uid(String)` — userId của người dùng, gửi lên backend qua param `uid`.
- `.userImpressionLimit(Int)` — giới hạn số lần hiển thị theo user, gửi lên backend qua param `uil`.

Áp dụng cho các manager: `InStreamManager`, `BannerManager`, `DisplayBannerManager`, `OverlayBannerManager`, `AdsWelcomeManager`.

##### 2.1 InStream Ads — đổi `uid20` thành `uid` và thêm `userImpressionLimit`

`AdsRequestData` không còn `.uid20(...)`. Cập nhật như sau:

```java
// Trước (1.10.18)
AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId("998989")
        .streamId("999999")
        .transId("a23334343")
        .uid20("123123123")          // <- bỏ
        .segments("123,3fef3,f3f,3f")
        .build();

// Sau (1.10.19)
AdsRequestData adsRequestData = new AdsRequestData.Builder()
        .channelId("998989")
        .streamId("999999")
        .transId("a23334343")
        .uid("123123123")            // <- thay uid20 bằng uid
        .userImpressionLimit(1)      // <- tham số mới
        .segments("123,3fef3,f3f,3f")
        .build();
```

##### 2.2 Banner Ads

```java
BannerAdsRequestData bannerAdsRequestData = new BannerAdsRequestData.Builder(adSize)
        .channelId("998989")
        .streamId("999999")
        .transId("1112222222")
        .uid("123123123")            // <- tham số mới
        .userImpressionLimit(20)     // <- tham số mới
        .color("#e5e5e5")
        .segments("a3,34,d3,d3")
        .build();
```

##### 2.3 Display Banner / Overlay Banner Ads

```java
DisplayBannerAdsRequestData bannerAdsRequestData =
        new DisplayBannerAdsRequestData.Builder()
                .channelId("998989")
                .streamId("999999")
                .adSize(BannerDisplayAdSize.HOMEPAGE_BANNER)
                .bannerDisplayType(BannerDisplayType.DISPLAY)
                .transId("1112222222")
                .uid("123123123")            // <- tham số mới
                .userImpressionLimit(20)     // <- tham số mới
                .color("#ffffff00")
                .segments("a3,34,d3,d3")
                .positionId(positionId)
                .build();
```

##### 2.4 Welcome Ads

```java
WelcomeAdsRequestData welcomeAdsRequestData = new WelcomeAdsRequestData.Builder()
        .transId("1112222222")
        .uid("123123123")            // <- tham số mới
        .userImpressionLimit(20)     // <- tham số mới
        .segments("a3,34,d3,d3")
        .build();
```

#### 3. Mô tả các tham số

1. Parameter (bổ sung so với `1.10.18`)

| Key                  | Description                                                                       |     Type |
|:---------------------|:----------------------------------------------------------------------------------|---------:|
| uid                  | User id của người dùng (thay cho `uid20`). Gửi lên backend qua param `uid`.        |   string |
| userImpressionLimit  | Giới hạn số lần hiển thị quảng cáo theo user. Gửi lên backend qua param `uil`.     |  integer |

> Lưu ý: `uid20` đã bị đổi tên thành `uid` trong `AdsRequestData` (InStream). Các DTO còn lại (`BannerAdsRequestData`, `DisplayBannerAdsRequestData`, `WelcomeAdsRequestData`) đã dùng `uid` từ trước, nay bổ sung thêm `userImpressionLimit`.
