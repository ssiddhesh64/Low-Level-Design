package org.cache;

import java.time.Instant;

class Node <K, V>{

    K key;
    V val;
    Instant expiresAt;

    public Node(K key, V val, Instant expiresAt) {
        this.key = key;
        this.val = val;
        this.expiresAt = expiresAt;
    }

    Node<K, V> next;
    Node<K, V> prev;

    public Node(K key, V val) {
        this.key = key;
        this.val = val;
    }
}
