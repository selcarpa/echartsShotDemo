package one.tain.echartsshotdemo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("NullableProblems")
@Component
@Slf4j
public class OptionsCache {
    private static final LoadingCache<String, Collection<String>> guavaCache
            = CacheBuilder.newBuilder()
            .concurrencyLevel(8)
            .expireAfterWrite(3600, TimeUnit.SECONDS)
            .initialCapacity(10)
            .maximumSize(3000)
            .removalListener(notification -> log.info("{}: remove: {}, cause：{}", "OptionsCache", notification.getKey(), notification.getCause()))
            .build(new CacheLoader<String, Collection<String>>() {
                @Override
                public Collection<String> load(String key) {
                    return null;
                }
            });


    public Collection<String> get(String key) throws ExecutionException {
        Collection<String> s = guavaCache.get(key);
        guavaCache.invalidate(key);
        return s;
    }


    public void put(String key, Collection<String> value) {
        guavaCache.put(key, value);
    }
}
