package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 제가 알고 있는 이론으로는 자바에서 크게 두 가지로 나뉩니다.
 * 첫번째는 Lock을 사용하여 다른 사용자가 임계영역에 접근하지 못하는 것입니다.
 * 두번째는 CAS를 활용한 원자적 연산을 이용하는 것입니다.
 * 이론적으로 CAS 연산은 Lock을 사용하지 않아 블로킹 처리가 되지 않습니다.
 * 하지만 충돌이 많이 일어날 시 루프를 계속 돌아야하기 떄문에 CPU 자원의 낭비가 있습니다.
 * 따라서 충돌이 많이 나지 않는 상태를 가정하여 lock을 활용하는 것 보다는 CAS 연산을 사용하여 구현하기를 생각했습니다.
 * 또한 실제 실무에서도 충돌이 빈번하게 일어나는 것보다는 나지 않는 경우가 더 많아 CAS 연산을 많이 사용하는 것으로 들었습니다.
 *
 * 처음에는 HashMap의 Value 값을 Atomic으로 지정하였습니다.
 * 그래서 각 Key에 대한 Value 값을 비교하여 만약 원래 값이 같으면 해당 로직을 수행하고 같지 않으면 루프를 돌게 하였습니다.
 * 테스트를 해본 결과 동시성 해결이 되지 않았습니다. 그 이유는 HashMap 객체의 변화를 감지하는게 아닌 단순 값을 비교하기 때문입니다.
 *
 * 두 번째로 HashMap에 Atomic을 설정했습니다.
 * 따라서 객체의 참조 주소 값을 비교하여 CAS를 수행해야 됩니다.
 * 원래 객체와 그 객체를 복사한 새로운 객체를 생성하였으며, CAS 연산을 수행해 원래 객체의 참조 주소 값이 변하지 않으면 새로운 객체로 변경하였습니다.
 * 동시성 이슈는 해결을 하였지만 여러 가지 단점이 보였습니다.
 * 일단 단순 메서드에 Synchorinzed를 비교해 본 결과 속도는 CAS 연산을 이용한 동시서 해결이 10배 이상 느려졌습니다.
 * 그 이유는 객체를 복사하는 과정 속에서 오버헤드가 발생한 것 같습니다.
 * 또한 객체의 복사로 인해 힙 영역의 메모리가 사용되며 그로인한 GC가 빈번하게 발생할 수 있다는 생각이 들었습니다.
 * 그래서 객체에 Atomic을 걸지 않고 다른 방법이 무엇이 있을까 생각해 보았지만 아직까지 답을 찾지는 못하였습니다.
 */
public class MyConcurrentHashMap<K, V> implements Map<K, V> {

    private final AtomicReference<HashMap<K, V>> innerMap = new AtomicReference<>(new HashMap<>());
    @Override
    public V get(Object key) {
        return innerMap.get().get(key);
    }

    @Override
    public V put(K key, V value) {
        while (true) {
            HashMap<K, V> currentMap = innerMap.get();
            HashMap<K, V> newMap = new HashMap<>(currentMap);
            newMap.put(key, value);
            if (innerMap.compareAndSet(currentMap, newMap)) {
                return currentMap.get(key);
            }
        }
    }

    @Override
    public V remove(Object key) {
        while (true) {
            HashMap<K, V> currentMap = innerMap.get();
            HashMap<K, V> newMap = new HashMap<>(currentMap);
            currentMap.remove(key);
            if (innerMap.compareAndSet(currentMap, newMap)) {
                return null;
            }
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        while (true) {
            HashMap<K, V> currentMap = innerMap.get();
            HashMap<K, V> newMap = new HashMap<>(currentMap);

            if (!newMap.containsKey(key)) {
                newMap.put(key, value);
                if (innerMap.compareAndSet(currentMap, newMap)) {
                    return null;
                }
            } else {
                return currentMap.get(key);
            }
        }
    }

    @Override
    public int size() {
        return innerMap.get().size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.get().isEmpty();
    }

    // ------- 이 아래는 구현하지 않아도 됩니다 ----------

    @Override
    public boolean containsKey(Object key) {
        return innerMap.get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return innerMap.get().containsValue(value);
    }

    @Override
    public void clear() {
        innerMap.get().clear();
    }

    @Override
    public Set<K> keySet() {
        return innerMap.get().keySet();
    }

    @Override
    public Collection<V> values() {
        return innerMap.get().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return innerMap.get().entrySet();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        innerMap.get().putAll(m);
    }

    public static void main(String[] args) {
        MyConcurrentHashMap<String, Integer> map = new MyConcurrentHashMap<>();
        map.put("String1", 1);
        map.get("String1");
    }
}