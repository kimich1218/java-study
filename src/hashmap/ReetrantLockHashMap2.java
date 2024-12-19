package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ReetrantLockHashMap2<K, V> implements Map<K, V> {

    private final HashMap<K, V> innerMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public V get(Object key) {
        lock.lock();
        try {
            return innerMap.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        lock.lock();
        try {
            return innerMap.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        lock.lock();
        try {
            return innerMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        lock.lock();
        try {
            return innerMap.putIfAbsent(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock(); // 모든 락 획득
        try {
            return innerMap.size();
        } finally {
            lock.unlock(); // 모든 락 해제
        }
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
}
