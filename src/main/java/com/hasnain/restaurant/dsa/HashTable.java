package com.hasnain.restaurant.dsa;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hash table using separate chaining (a linked list inside each bucket).
 *
 * - put, get, containsKey, remove: O(1) on average, O(n) in the worst case
 *   (when every key lands in the same bucket).
 * - The table doubles its capacity when size exceeds 0.75 * capacity,
 *   so chains stay short.
 * - Null keys are not allowed in put. Lookups with a null key find nothing.
 */
public class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private static final int MAX_CAPACITY = 1 << 30;

    private static class Node<K, V> {
        final K key;
        final int hash;
        V value;
        Node<K, V> next;

        Node(K key, int hash, V value, Node<K, V> next) {
            this.key = key;
            this.hash = hash;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] table;
    private int size;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.table = createTable(initialCapacity);
    }

    @SuppressWarnings("unchecked")
    private Node<K, V>[] createTable(int capacity) {
        return (Node<K, V>[]) new Node[capacity];
    }

    // ---------- hashing ----------

    /** Mixes the high bits into the low bits so poor hashCodes spread better. */
    private static int hash(Object key) {
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    /** The mask clears the sign bit, so negative hashes still give a valid index. */
    private static int indexFor(int hash, int capacity) {
        return (hash & 0x7fffffff) % capacity;
    }

    // ---------- operations ----------

    /** Inserts or overwrites. Returns the old value, or null if the key was new. */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && node.key.equals(key)) {
                V old = node.value;
                node.value = value;
                return old; // overwrite: size unchanged, no resize
            }
        }

        table[index] = new Node<>(key, hash, value, table[index]);
        size++;

        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
        return null;
    }

    /** Returns the value for the key, or null if it is missing. */
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    /** Removes the key. Returns the removed value, or null if it was missing. */
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Node<K, V> previous = null;
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.hash == hash && current.key.equals(key)) {
                if (previous == null) {
                    table[index] = current.next;   // removing the head of the chain
                } else {
                    previous.next = current.next;  // removing from the middle or end
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return table.length;
    }

    /** Removes every entry. Capacity stays the same. */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /** All keys, in no particular order. */
    public Iterable<K> keys() {
        return KeyIterator::new;
    }

    // ---------- internals ----------

    private Node<K, V> findNode(K key) {
        if (key == null) {
            return null;
        }
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    /** Doubles the capacity and moves every node to its new bucket. */
    private void resize() {
        if (table.length >= MAX_CAPACITY) {
            return; // cannot grow any more; chains will just get longer
        }
        int newCapacity = Math.min(table.length * 2, MAX_CAPACITY);
        Node<K, V>[] newTable = createTable(newCapacity);

        for (Node<K, V> head : table) {
            Node<K, V> node = head;
            while (node != null) {
                Node<K, V> next = node.next;            // save before relinking
                int index = indexFor(node.hash, newCapacity);
                node.next = newTable[index];
                newTable[index] = node;
                node = next;
            }
        }
        table = newTable;
    }

    private class KeyIterator implements Iterator<K> {
        private int bucket = 0;
        private Node<K, V> next;

        KeyIterator() {
            moveToNextNonEmptyBucket();
        }

        private void moveToNextNonEmptyBucket() {
            while (next == null && bucket < table.length) {
                next = table[bucket++];
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public K next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            K key = next.key;
            next = next.next;
            if (next == null) {
                moveToNextNonEmptyBucket();
            }
            return key;
        }
    }
}