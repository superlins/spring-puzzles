package org.example.sftp;

import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.ListenableMetadataStore;
import org.springframework.integration.metadata.MetadataStoreListener;
import org.springframework.integration.metadata.SimpleMetadataStore;

/**
 * @author renc
 */
public class RedisMetadataStore implements ListenableMetadataStore {

    private final ConcurrentMetadataStore delegate = new SimpleMetadataStore();

    @Override
    public String putIfAbsent(String key, String value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public void put(String key, String value) {
        delegate.put(key, value);
    }

    @Override
    public String get(String key) {
        return delegate.get(key);
    }

    @Override
    public String remove(String key) {
        return delegate.remove(key);
    }

    @Override
    public void addListener(MetadataStoreListener callback) {
        System.out.println(">>>> MetadataStoreListener addListener");
    }

    @Override
    public void removeListener(MetadataStoreListener callback) {
        System.out.println(">>>> MetadataStoreListener removeListener");
    }
}
