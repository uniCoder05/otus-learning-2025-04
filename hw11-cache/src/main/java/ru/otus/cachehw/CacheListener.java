package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheListener<K, V> implements HwListener<K, V> {
    private static final Logger log = LoggerFactory.getLogger(CacheListener.class);

    @Override
    public void notify(K key, V value, String action) {
        log.info("Cache event - '{}', key = '{}', value = '{}'", action, key, value);
    }
}
