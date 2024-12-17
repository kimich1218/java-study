package hashmap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyHashMapTest {

    @Test
    public void 동시성_이슈_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        MyConcurrentHashMap<String, Integer> myHashMap = new MyConcurrentHashMap<>();

        int executeCount = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(executeCount);
        int num = 0;

        for (int i = 0; i < executeCount; i++) {
            int index = i;
            executorService.submit(() -> {
                myHashMap.put("String" + index, num);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        Assertions.assertEquals(myHashMap.size(), executeCount);
    }
}