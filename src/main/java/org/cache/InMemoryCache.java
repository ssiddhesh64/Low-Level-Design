package org.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryCache<K, V> implements Cache<K, V>{

    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final int capacity;
    private final Clock clock;

    private final Map<K, Node<K, V>> cache = new HashMap<>();

    public InMemoryCache(int capacity, Clock clock) {

        if(capacity <= 0) {
            throw new IllegalArgumentException("Capacity should be greater than 0");
        }

        this.clock = Objects.requireNonNull(clock, "Clock must not be null");

        this.capacity = capacity;
        head = new Node<>(null, null);
        tail = new Node<>(null, null);

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void put(K key, V val) {
        if(cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.val = val;
            removeNode(node);
            cache.remove(key);
        } else if(capacity == cache.size()) {
            Node head = this.head.next;
            removeNode(head);
            cache.remove(head.key);
        }

        Node newNode = new Node(key, val);
        insertAtTail(newNode);
        cache.put(key, newNode);
    }

    public void put(K key, V val, Duration ttl) {

        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        if(cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.val = val;
            removeNode(node);
            cache.remove(key);

        } else if(capacity == cache.size()) {
            Node head = this.head.next;
            removeNode(head);
            cache.remove(head.key);
        }

        Instant expiresAt = clock.instant().plus(ttl);
        Node newNode = new Node(key, val, expiresAt);
        insertAtTail(newNode);
        cache.put(key, newNode);
    }

    private void insertAtTail(Node node) {
        Node tail = this.tail.prev;
        tail.next = node;
        node.prev = tail;
        node.next = this.tail;
        this.tail.prev = node;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
        node.next = null;
        node.prev = null;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);

        if(node == null) {
            return null;
        }

        if(isExpired(node)) {
            removeNode(node);
            cache.remove(key);
            return null;
        }

        removeNode(node);
        insertAtTail(node);
        return node.val;
    }

    @Override
    public V remove(K key) {
        Node<K, V> node = cache.get(key);

        if(node != null) {
            if(isExpired(node)) return null;
            removeNode(node);
            cache.remove(key);
            return node.val;
        }

        return null;
    }

    @Override
    public boolean containsKey(K key) {
        Node<K, V> node = cache.get(key);

        if(node == null) {
            return false;
        }

        if(isExpired(node)) {
            cache.remove(key);
            removeNode(node);
            return false;
        }

        return true;
    }

    private boolean isExpired(Node<K, V> node) {
        return node.expiresAt != null
                && !node.expiresAt.isAfter(clock.instant());
    }

    @Override
    public void clear() {
        cache.clear();
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public int size() {
        return cache.size();
    }
}
