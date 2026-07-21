package tv.wiinvent.android.wiinvent_android_sample_java.feature.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.report.ReportButtonAds;

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