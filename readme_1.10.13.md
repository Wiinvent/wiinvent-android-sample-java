
### Version 1.10.6
Change log: Hỗ trợ quảng cáo banner

#### 1. SDK
```gralde
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.6'
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
```

#### 2.2 Welcome
#### 2.2.1 Bổ sung layout trong main activity:
```xml
    <tv.wiinvent.wiinventsdk.ui.welcomead.WelcomeAdView
        android:id="@+id/welcome_ad_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone"
        />
```
#### 2.2.1 Copy các layout từ project sample sang project của TV360:
    wisdk_welcome_tvc_detail, wisdk_welcome_combo_detail, wisdk_welcome_banner_sdk_detail

#### 2.2.3 Triển khai code như hàm initWelcome trong file MainActivity