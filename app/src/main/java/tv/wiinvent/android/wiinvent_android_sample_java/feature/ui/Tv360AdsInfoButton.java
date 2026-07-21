package tv.wiinvent.android.wiinvent_android_sample_java.feature.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.report.InfoButtonAds;

/**
 * Nút thông tin quảng cáo - custom lại giao diện mặc định của SDK.
 * Nếu không cần custom, có thể dùng thẳng tv.wiinvent.wiinventsdk.report.InfoButtonAds trong layout.
 */
public class Tv360AdsInfoButton extends InfoButtonAds {

    public Tv360AdsInfoButton(@Nullable Context context) {
        super(context);
    }

    public Tv360AdsInfoButton(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public Tv360AdsInfoButton(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Lớp cha đã gọi init() trong constructor, không cần tự gọi lại
    @Override
    public void init() {
        inflate(getContext(), R.layout.layout_info_ads_button, this);
        this.setReportButton(findViewById(R.id.info_ads_button));
    }
}
