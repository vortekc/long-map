package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private int size = 0;
    private Entry<Long, V>[] buckets;
    private final int initialCapacity;
    private final float loadFactor;

    /**
     * Constructs map with default initial capacity and load factor
     * Default initial capacity will be used (16)
     * Default load factor will be used (0.75F)
     */
    public LongMapImpl () {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs map with specified initial capacity and load factor
     * @param initialCapacity initial capacity
     * @param loadFactor load factor
     * @throws IllegalArgumentException when initial capacity is negative or load factor is negative or NAN
     */
    public LongMapImpl (int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if (loadFactor <= 0.0F && Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    /**
     * Constructs map with specified initial capacity
     * @param initialCapacity initial capacity
     */
    public LongMapImpl (int initialCapacity) {
       this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Puts key-value pair in the map
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return old value for specified key
     */
    public V put(long key, V value) {
        resizeOrCreate();

        int size = buckets.length;
        int hash = Long.hashCode(key);

        Entry<Long, V> newValue = null;

        int placeInBucket = hash % size;
        if (buckets[placeInBucket] == null) {
            buckets[placeInBucket] = new Entry<>(key, value);
        } else {
            Entry<Long, V> head = buckets[placeInBucket];

            if (Objects.equals(head.key, key)) {
                newValue = new Entry<>(key, value);
            }

            while (true) {
                if (head.key != null && head.key.equals(key) && head.value.equals(value)) {
                    break;
                }

                if (head.next == null && !Objects.equals(head.key, value)) {
                    head.next = new Entry<>(key, value);
                    break;
                }

                if (head.next == null) {
                    break;
                }

                head = head.next;
            }
        }

        if (newValue != null) {
            V oldValue = buckets[placeInBucket].value;
            buckets[placeInBucket] = newValue;

            return oldValue;
        }

        this.size++;

        return null;
    }

    /**
     * Returns the value to which the specified key is mapped
     * @param key key to get associated value
     * @return value associated with specified key
     */
    public V get(long key) {
        Entry<Long, V> node = getNode(key);
        return node == null ? null : node.value;
    }

    /**
     * Removes entry from map specified by key
     * @param key key to remove associated key-value pair from this map
     * @return removed value
     */
    public V remove(long key) {
        int hash = Long.valueOf(key).hashCode();
        int size = buckets.length;

        Entry<Long, V> bucket = buckets[hash % size];
        if (bucket == null) {
            return null;
        }

        Entry<Long, V> prev = null;
        Entry<Long, V> curr = bucket;
        while(true) {
            if (curr.key.equals(key)) {
                if (prev != null) {
                    buckets[hash % size] = prev;
                    prev.next = curr.next;
                } else {
                    buckets[hash % size] = curr.next;
                }

                this.size--;
                break;
            }

            if (curr.next == null) {
                break;
            }

            prev = curr;
            curr = curr.next;
        }

        return curr.value;
    }

    /**
     * Returns true if map contains entries
     * @return true if map contains entries
     */
    public boolean isEmpty() {
        return buckets == null || size == 0;
    }

    /**
     * Returns true id map contains specified key
     * @param key key whose presence in this map is to be tested
     * @return true id map contains specified key
     */
    public boolean containsKey(long key) {
        return getNode(key) != null;
    }

    /**
     * Returns true if map contains specified value
     * @param value value whose presence in this map is to be tested
     * @return true if map contains specified value
     */
    public boolean containsValue(V value) {
        if (buckets == null || buckets.length == 0) {
            return false;
        }

        for (Entry<Long, V> bucket : buckets) {
            if (bucket == null) {
                continue;
            }

            Entry<Long, V> entry = bucket;
            while (true) {
                if (entry.value.equals(value)) {
                    return true;
                }

                if (entry.next == null) {
                    break;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    /**
     * Returns array of keys added to this map
     * @return array of keys added to this map
     */
    public long[] keys() {
        long[] keys = new long[this.size];

        if (buckets == null || buckets.length == 0) {
            return keys;
        }

        int i=0;
        for (Entry<Long, V> entry : buckets) {
            if (entry == null) {
                continue;
            }

            while (true) {
                keys[i++] = entry.key;

                if (entry.next == null) {
                    break;
                }
                entry = entry.next;
            }
        }

        return keys;
    }

    /**
     * Returns List of values added to this map
     * @return List of values added to this map
     */
    public List<V> values() {
        List<V> values = new ArrayList<>();

        if (buckets == null || buckets.length == 0) {
            return values;
        }

        int i=0;
        for (Entry<Long, V> entry : buckets) {
            if (entry == null) {
                continue;
            }

            while (true) {
                i++;
                values.add(entry.value);

                if (entry.next == null) {
                    break;
                }
                entry = entry.next;
            }
        }

        return values;
    }

    /**
     *
     * @return current count of the entries in this map
     */
    public long size() {
        return size;
    }

    /**
     * Clear all entries
     * Internal buckets table will have the same size.
     * It's expected that clear will be called to re-use map.
     */
    public void clear() {
        if (buckets != null && size != 0) {
            size = 0;
            Arrays.fill(buckets, null);
        }
    }

    /**
     * Get node for specified key
     * @param key key to find associated Entry<Long, V> in this map
     * @return node for specified key
     */
    private Entry<Long, V> getNode(Long key) {
        if (buckets == null) {
            return null;
        }

        int hash = key.hashCode();
        int size = buckets.length;

        Entry<Long, V> bucket = buckets[hash % size];

        while(true) {
            if (bucket == null) {
                return null;
            }

            if (bucket.key.equals(key)) {
                return bucket;
            }

            bucket = bucket.next;
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void resizeOrCreate() {
        if (buckets == null) {
            buckets = (Entry<Long, V>[]) new Entry[initialCapacity];
            return;
        }

        if (size > buckets.length * loadFactor) {
            size = 0;
            Entry<Long, V>[] oldBuckets = Arrays.copyOf(buckets, buckets.length);

            buckets = (Entry<Long, V>[]) new Entry[buckets.length * 2];
            Arrays.stream(oldBuckets)
                    .filter(Objects::nonNull)
                    .forEach(__ -> put(__.key, __.value));
        }
    }

    private static class Entry<Long, V> {
        Long key;
        V value;
        int hash;
        Entry<Long, V> next;

        public Entry(Long key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return Objects.equals(key, entry.key) &&
                    Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value, hash);
        }
    }
}
