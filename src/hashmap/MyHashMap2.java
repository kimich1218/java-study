package hashmap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class MyHashMap2<K, V> implements Map<K, V> {

    private final HashMap<K, V> innerMap = new HashMap<>();
    private final int lockNumber = 100;
    private final ReentrantLock[] locks = new ReentrantLock[lockNumber];

    public MyHashMap2() {
        for (int i = 0; i < lockNumber; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    private ReentrantLock getLockNumber(Object key) {
        int index = Math.abs(key.hashCode() % lockNumber);
        return getReentrantLock(index);
    }

    private ReentrantLock getReentrantLock(int index) {
        return locks[index];
    }

    @Override
    public V get(Object key) {
        ReentrantLock lock = getLockNumber(key);
        lock.lock();
        try {
            return innerMap.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        ReentrantLock lock = getLockNumber(key);
        lock.lock();
        try {
            return innerMap.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        ReentrantLock lock = getLockNumber(key);
        lock.lock();
        try {
            return innerMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        ReentrantLock lock = getLockNumber(key);
        lock.lock();
        try {
            return innerMap.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (K key : innerMap.keySet()) {
            ReentrantLock lock = getLockNumber(key);
            lock.lock();
            try {
                size++;
            } finally {
                lock.unlock();
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    // ------- 이 아래는 구현하지 않으셔도 됩니다 ----------

    @Override
    public boolean containsKey(Object key) {
        return innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return innerMap.containsValue(value);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return innerMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return innerMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return innerMap.entrySet();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        innerMap.putAll(m);
    }

    public static void main(String[] args) {
        MyHashMap2<String, Integer> map = new MyHashMap2<>();
        map.put("string", 1);
        map.get("string");
    }
}
