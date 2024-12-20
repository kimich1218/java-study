package hashmap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 테스트코드에서 실패하는 이유는 무엇일까?
 * 키 값에 따라 분할을 한다는 것은 서로 다른 키 값은 동시에 hashmap에 접근할 수 있다.
 * 즉, 동시에 값을 넣는 과정에서 결국 hashmap의 put에 접근하게 된다.
 * hashmap 자체는 스레드로부터 안전하지 않기 때문에 예키치 못한 충돌이 발생하게 되므로 동시성 보장이 안 될 수 있다.
 */
public class ReetrantLockHashMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> innerMap = new HashMap<>();
    private final int lockNumber = 16;
    private final ReentrantLock[] segmentLock = new ReentrantLock[lockNumber];

    public ReetrantLockHashMap() {
        for (int i = 0; i < lockNumber; i++) {
            segmentLock[i] = new ReentrantLock();
        }
    }

    private ReentrantLock getLockNumber(Object key) {
        int index = Math.abs(key.hashCode() % lockNumber);
        return segmentLock[index];
    }

    @Override
    public V get(Object key) {
        ReentrantLock lock = getLockNumber(key);
        try {
            lock.lock();
            return innerMap.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        ReentrantLock lock = getLockNumber(key);
        try {
            lock.lock();
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
        for (ReentrantLock lock : segmentLock) {
            lock.lock(); // 모든 락 획득
        }
        try {
            size = innerMap.size();
        } finally {
            for (ReentrantLock lock : segmentLock) {
                lock.unlock(); // 모든 락 해제
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
}
