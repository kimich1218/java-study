package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ReetrantLockHashMap3<K, V> implements Map<K, V> {

    private final int lockNumber = 16;
    private final HashMap<K, V>[] segmentHashMap = new HashMap[lockNumber];
    private final ReentrantLock[] segmentLock = new ReentrantLock[lockNumber];

    public ReetrantLockHashMap3() {
        for (int i = 0; i < lockNumber; i++) {
            segmentLock[i] = new ReentrantLock();
            segmentHashMap[i] = new HashMap<K, V>();
        }
    }

    private int getHashIndex(Object key) {
        return Math.abs(key.hashCode() % lockNumber);
    }

    @Override
    public V get(Object key) {
        int hashIndex = getHashIndex(key);
        ReentrantLock lock = segmentLock[hashIndex];
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];

        try {
            lock.lock();
            return hashMap.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        int hashIndex = getHashIndex(key);
        ReentrantLock lock = segmentLock[hashIndex];
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];

        try {
            lock.lock();
            return hashMap.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        int hashIndex = getHashIndex(key);
        ReentrantLock lock = segmentLock[hashIndex];
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];

        lock.lock();
        try {
            return hashMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        int hashIndex = getHashIndex(key);
        ReentrantLock lock = segmentLock[hashIndex];
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];

        lock.lock();
        try {
            return hashMap.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return IntStream.range(0, lockNumber).parallel().map(i -> {
            ReentrantLock lock = segmentLock[i];
            lock.lock();
            try {
                return segmentHashMap[i].size();
            } finally {
                lock.unlock();
            }
        }).sum();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    // ------- 이 아래는 구현하지 않으셔도 됩니다 ----------

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }
}
