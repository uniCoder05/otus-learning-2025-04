package ru.otus.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(MyCache.class);

    private final Map<MyKey<K>, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        log.info("Putting key: {} into cache", key);
        MyKey<K> myKey = new MyKey<>(key);
        cache.put(myKey, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        log.info("Removing key: {} from cache", key);
        MyKey<K> myKey = new MyKey<>(key);
        V value = cache.remove(myKey);
        notifyListeners(key, value, "remove");
    }

    @Override
    public V get(K key) {
        log.info("Getting key: {} from cache", key);
        MyKey<K> myKey = new MyKey<>(key);
        V value = cache.get(myKey);
        notifyListeners(key, value, "get");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public void clear() {
        log.info("Current cache size: {}", size());
        log.info("--- Clearing cache ---");
        cache.clear();
        log.info("Current cache size: {}", size());
    }

    @Override
    public int size() {
        return cache.size();
    }

    private void notifyListeners(K key, V value, String action) {
        MyKey<K> myKey = new MyKey<>(key);
        for (HwListener<K, V> listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (RuntimeException e) {
                log.error("Error in listener on action '{}', key '{}', value '{}'", action, myKey, value, e);
            }
        }
    }
}
