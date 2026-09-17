package org.cache;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCache<K, V> implements Cache<K, V>{

    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final int capacity;

    private final Map<K, Node<K, V>> cache = new HashMap<>();

    public InMemoryCache(int capacity) {

        if(capacity <= 0) {
            throw new IllegalArgumentException("Capacity should be greater than 0");
        }
        this.capacity = capacity;
        head = new Node<>(null, null);
        tail = new Node<>(null, null);

        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void put(K key, V val) {
        if(cache.containsKey(key)) {
            Node node = cache.get(key);
            node.val = val;
            removeNode(node);
            insertAtTail(node);
            return;
        }

        if(capacity == cache.size()) {
            Node head = this.head.next;
            removeNode(head);
            cache.remove(head.key);
        }

        Node newNode = new Node(key, val);
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
        if(cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            removeNode(node);
            insertAtTail(node);
            return node.val;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if(cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            removeNode(node);
            cache.remove(key);
            return node.val;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return cache.containsKey(key);
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
