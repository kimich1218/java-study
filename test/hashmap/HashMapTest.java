package hashmap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.*;

public class HashMapTest {
//        SpinLockHashMap<String, Integer> myHashMap = new SpinLockHashMap<>();
//    ReetrantLockHashMap<String, Integer> myHashMap = new ReetrantLockHashMap<>();
    ReetrantLockHashMap2<String, Integer> myHashMap = new ReetrantLockHashMap2<>();
//    ReetrantLockHashMap3<String, Integer> myHashMap = new ReetrantLockHashMap3<>();
//    SynchronizedHashMap<String, Integer> myHashMap = new SynchronizedHashMap<>();
//    CasHashMap<String, Integer> myHashMap = new CasHashMap<>();
//    CasHashMap2<String, Integer> myHashMap = new CasHashMap2<>();
//    HashMap<String, Integer> myHashMap = new HashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    int size = 1000;
    CountDownLatch latch = new CountDownLatch(size);

    @Test
    public void 여러_스레드_동시_PUT_검증() throws InterruptedException {
        for (int i = 0; i < size; i++) {

            int value = i;
            executorService.submit(() -> {
                myHashMap.put("key" + value, value);
                latch.countDown();
            });
        }
        latch.await();

        Assertions.assertEquals(size, myHashMap.size());
    }

    @Test
    public void 여러_스레드_동시_PUT_후_GET_검증() throws InterruptedException {
        for (int i = 0; i < size; i++) {
            int value = i;
            executorService.submit(() -> {
                myHashMap.put("key" + value, value);
                latch.countDown();
            });
        }
        latch.await();

        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(i, myHashMap.get("key" + i));
        }
    }
}
