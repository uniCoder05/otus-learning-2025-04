package ru.otus.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheListener<K, V> implements HwListener<K, V> {
    private static final Logger log = LoggerFactory.getLogger(CacheListener.class);

    @Override
    public void notify(K key, V value, String action) {
        MyKey<K> myKey = new MyKey<>(key);
        log.info("Cache event - '{}', key = '{}', value = '{}'", action, myKey, value);
    }
}
