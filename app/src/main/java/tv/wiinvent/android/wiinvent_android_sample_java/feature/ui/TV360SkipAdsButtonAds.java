package tv.wiinvent.android.wiinvent_android_sample_java.feature.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.ui.instream.SkipAdsButtonAds;

public class TV360SkipAdsButtonAds extends SkipAdsButtonAds {
  public TV360SkipAdsButtonAds(@Nullable Context context) {
    super(context);
    init();
  }

  public TV360SkipAdsButtonAds(@NonNull Context context, @NonNull AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TV360SkipAdsButtonAds(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override
  public void init() {
    inflate(getContext(), R.layout.layout_skip_button, this);
    this.setSkipButton(findViewById(R.id.skip_ads_button));
    if(getSkipButton() != null) {
      getSkipButton().setOnClickListener((OnClickListener) this);
    }
    this.setSkipLabel("Bỏ qua quảng cáo");
    this.setIconDrawable(R.drawable.next_white);
  }
}
