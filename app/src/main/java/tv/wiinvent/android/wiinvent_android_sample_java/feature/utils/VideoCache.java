package tv.wiinvent.android.wiinvent_android_sample_java.feature.utils;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class VideoCache {
  private static final int MAX_VIDEO_CACHE_SIZE_IN_BYTES = 200 * 1024 * 1024;  // 200MB

  private static VideoCache instance;

  private final Cache cache;

  public static VideoCache getInstance(Context context) {
    if(instance == null)
      instance = new VideoCache(context);
    return instance;
  }

  public VideoCache(Context context) {
    cache = new SimpleCache(new File(context.getCacheDir(), "video"), new LeastRecentlyUsedCacheEvictor(MAX_VIDEO_CACHE_SIZE_IN_BYTES), new ExoDatabaseProvider(context));
  }

  public Cache getCache() {
    return cache;
  }

}
