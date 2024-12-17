package concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentTest {

    int numberOfThreads = 10;
    int numberOfCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    /**
     * CountDownLatch는 정해진 카운트가 완료될 때까지 대기 상태를 유지한다.
     * await() 메서드를 호출한 스레드는 모든 카운트가 완료될 때까지 블로킹된다.
     * 이 코드는 지정된 수(numberOfThreads)만큼 작업이 완료되면 다음 단계로 진행하도록 보장한다.
     * 주의: CountDownLatch는 스레드 실행을 제어하지 않으며, 단지 완료된 작업의 카운트를 관리한다.
     */
    @Test
    public void CountDownLatch() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfCount; i++) {
            executorService.submit(countDownLatch::countDown);
        }
        countDownLatch.await();
        assertEquals(0, countDownLatch.getCount());
    }

    /**
     * Semaphore는 허가(permit) 수를 관리하며, 동시에 실행 가능한 작업 수를 제한한다.
     * 모든 허가를 소진한 상태에서 acquire()를 호출하면 해당 스레드는 허가가 반환될 때까지 대기 상태가 된다.
     * release() 메서드를 통해 허가를 반환하거나 추가할 수 있다.
     * Semaphore는 동시 실행 가능한 스레드의 개수를 제한하는 데 초점을 둔다.
     */
    @Test
    public void Semaphore() throws InterruptedException {

        Semaphore semaphore = new Semaphore(numberOfThreads);
        for (int i = 0; i < numberOfCount; i++) {
            executorService.submit(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException("message: " + e.getMessage());
                }
            });
        }
        Thread.sleep(100);
        assertFalse(semaphore.tryAcquire());
    }

    /**
     * CyclicBarrier는 지정된 스레드 수가 모두 await()를 호출해야만 다음 단계로 넘어갈 수 있다.
     * 예를 들어, 10개의 스레드를 지정했지만 1개의 스레드가 await()를 호출하지 않으면,
     * 이미 await()를 호출한 9개의 스레드는 대기 상태로 유지된다.
     * CountDownLatch와의 차이점은, CyclicBarrier는 모든 스레드가 도달해야 넘어가므로,
     * 지정된 수의 스레드가 모두 실행되었는지 디버깅하는 데 유용하다.
     */
    @Test
    public void CyclicBarrier() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);
        int limitCount = 3;
        for (int i = 0; i < numberOfCount - limitCount; i++) {
            executorService.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }
        Thread.sleep(1000);
        assertEquals(numberOfThreads - limitCount, cyclicBarrier.getNumberWaiting());
    }
}
