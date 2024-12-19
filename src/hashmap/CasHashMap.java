package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class CasHashMap<K, V> implements Map<K, V> {

    private final int lockNumber = 16;
    private final HashMap<K, V>[] segmentHashMap = new HashMap[lockNumber];
    private final Object[] locks = new Object[lockNumber];
    private final CounterCell[] counterCell = new CounterCell[lockNumber];

    static final class CounterCell {

        private final AtomicLong value;

        CounterCell(long initialValue) {
            this.value = new AtomicLong(initialValue);
        }

        void increment() {
            value.incrementAndGet();
        }

        void decrement() {
            value.decrementAndGet();
        }

        long get() {
            return value.get();
        }
    }

    public CasHashMap() {
        for (int i = 0; i < lockNumber; i++) {
            segmentHashMap[i] = new HashMap<>();
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
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];

        synchronized (locks[hashIndex]) {
            return hashMap.get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        int hashIndex = getHashIndex(key);
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];
        CounterCell counter = counterCell[hashIndex];
        synchronized (locks[hashIndex]) {
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
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];
        CounterCell counter = counterCell[hashIndex];

        synchronized (locks[hashIndex]) {
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
        HashMap<K, V> hashMap = segmentHashMap[hashIndex];
        CounterCell counter = counterCell[hashIndex];

        synchronized (locks[hashIndex]) {
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
