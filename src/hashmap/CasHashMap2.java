package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class CasHashMap2<K, V> implements Map<K, V> {

    private final int lockNumber = 16;
    private final HashMap<K, V> hashMap = new HashMap<>();
    private final Object[] locks = new Object[lockNumber];
    private final CounterCell[] counterCell = new CounterCell[lockNumber];

    static final class CounterCell {

        private final AtomicLong value;

        CounterCell(long initialValue) {
            this.value = new AtomicLong(initialValue);
        }

        void increment() {
            value.incrementAndGet(); // CAS 기반으로 증가
        }

        void decrement() {
            value.decrementAndGet(); // CAS 기반으로 감소
        }

        long get() {
            return value.get(); // 현재 값을 읽음
        }
    }

    public CasHashMap2() {
        for (int i = 0; i < lockNumber; i++) {
            locks[i] = new Object();
            counterCell[i] = new CounterCell(0);
        }
    }

    private int getHashIndex(Object key) {
        return Math.abs(key.hashCode() % lockNumber);
    }

    @Override
    public V get(Object key) {
        int hashIndex = getHashIndex(key);
        Object lock = locks[hashIndex];
        synchronized (lock) {
            return hashMap.get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        int hashIndex = getHashIndex(key);
        CounterCell counter = counterCell[hashIndex];
        Object lock = locks[hashIndex];
        synchronized (lock) {
            V oldValue = hashMap.put(key, value);
            if (oldValue == null) {
                counter.increment();
            }
            return oldValue;
        }
    }

    @Override
    public V remove(Object key) {
        int hashIndex = getHashIndex(key);
        CounterCell counter = counterCell[hashIndex];
        Object lock = locks[hashIndex];
        synchronized (lock) {
            V removedValue = hashMap.remove(key);
            if (removedValue != null) { // 실제로 삭제된 경우에만 크기 감소
                counter.decrement();
            }
            return removedValue;
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        int hashIndex = getHashIndex(key);
        CounterCell counter = counterCell[hashIndex];
        Object lock = locks[hashIndex];
        synchronized (lock) {
            V oldValue = hashMap.putIfAbsent(key, value);
            if (oldValue == null) { // 값이 없어서 추가된 경우에만 크기 증가
                counter.increment();
            }
            return oldValue;
        }
    }

    @Override
    public int size() {
        long totalSize = IntStream.range(0, lockNumber)
                .mapToLong(i -> counterCell[i].get())
                .sum();
        return (totalSize > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) totalSize;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    // ------- 이 아래는 구현하지 않으셔도 됩니다 ----------

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return hashMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return hashMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        hashMap.putAll(m);
    }
}
