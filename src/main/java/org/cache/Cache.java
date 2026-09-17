package org.cache;

public interface Cache<K, V> {

    void put(K key, V val);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);

    void clear();

    int size();
}
