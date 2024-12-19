package hashmap;

import java.util.*;

public class SynchronizedHashMap<K, V> implements Map<K, V> {
    private final HashMap<K, V> innerMap = new HashMap<>();

    @Override
    public synchronized V get(Object key) {
        return innerMap.get(key);
    }

    @Override
    public synchronized V put(K key, V value) {
        return innerMap.put(key, value);
    }

    @Override
    public synchronized V remove(Object key) {
        return innerMap.remove(key);
    }

    @Override
    public synchronized V putIfAbsent(K key, V value) {
        return innerMap.putIfAbsent(key, value);
    }

    @Override
    public synchronized int size() {
        return innerMap.size();
    }

    @Override
    public synchronized boolean isEmpty() {
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
