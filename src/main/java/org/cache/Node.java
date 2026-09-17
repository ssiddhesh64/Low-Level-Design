package org.cache;

class Node <K, V>{

    K key;
    V val;

    Node<K, V> next;
    Node<K, V> prev;

    public Node(K key, V val) {
        this.key = key;
        this.val = val;
    }
}
