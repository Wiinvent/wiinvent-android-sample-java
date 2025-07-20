
### Version 1.10.6
Change log: hỗ trợ thêm 2 quảng cáo liên tiếp & welcome chạy vast tag

#### 1. SDK
```gralde
implementation 'tv.wiinvent:wiinvent-sdk-android:1.10.6'
```

#### 2. Hướng dẫn cập nhật
#### 2.1 Hai quảng cáo chạy liên tiếp cập nhật config sau:
    Chỉnh sửa init bỏ biến alwaysCustomSkip

    ==> InStreamManager.Companion.getInstance().init(requireContext(), "14", DeviceType.TV, Environment.SANDBOX, 5, 10, 5, 2500, LevelLog.BODY, 8);

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