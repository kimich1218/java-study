package hashmap;

import java.util.*;

public class SpinLockHashMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> innerMap = new HashMap<>();
    private final SpinLock spinLock = new SpinLock();

    @Override
    public V get(Object key) {
        try {
            spinLock.lock();
            return innerMap.get(key);
        } finally {
            spinLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            spinLock.lock();
            return innerMap.put(key, value);
        } finally {
            spinLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            spinLock.lock();
            return innerMap.remove(key);
        } finally {
            spinLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        try {
            spinLock.lock();
            return innerMap.putIfAbsent(key, value);
        } finally {
            spinLock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            spinLock.lock();
            return innerMap.size();
        } finally {
            spinLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            spinLock.lock();
            return innerMap.isEmpty();
        } finally {
            spinLock.unlock();
        }
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
}
