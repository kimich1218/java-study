package hashmap;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpinLock {
    private final AtomicBoolean lock = new AtomicBoolean(false);

    /**
     * 스핀락은 lock을 얻지 못하면 블로킹 처리가 되지 않고 락을 얻을 때까지 계속해서 시도한다.
     * 이로 인해 CPU 사용량이 많아지게 되고 부하를 일으키게된다.
     * 이러한 일을 방지하기 위해 Thread.yield()을 활용했다.
     */
    public void lock(){
        while (!lock.compareAndSet(false, true)) {
            System.out.println("lock 획득 실패");
            Thread.yield();
        }
        System.out.println("lock 획득 성공");
    }

    public void unlock() {
        lock.set(false);
    }
}
