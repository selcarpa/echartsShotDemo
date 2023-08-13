package one.tain.echartsshotdemo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("NullableProblems")
@Component
@Slf4j
public class OptionsCache {
    private static final LoadingCache<String, String> guavaCache
            = CacheBuilder.newBuilder()
            .concurrencyLevel(8)
            .expireAfterWrite(3600, TimeUnit.SECONDS)
            .initialCapacity(10)
            .maximumSize(3000)
            .removalListener(notification -> log.info("{}: remove: {}, cause：{}", "OptionsCache", notification.getKey(), notification.getCause()))
            .build(new CacheLoader<String, String>() {
                @Override
                public String load( String key) {
                    return null;
                }
            });


    public String get(String key) throws ExecutionException {
        String s = guavaCache.get(key);
        guavaCache.invalidate(key);
        return s;
    }


    public void put(String key, String value) {
        guavaCache.put(key, value);
    }
}
