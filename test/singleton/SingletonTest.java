package singleton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SingletonTest {

    @Test
    public void 인스턴스의_주소는_같다() {
        Singleton singleton1 = Singleton.getInstance();
        Singleton singleton2 = Singleton.getInstance();

        Assertions.assertEquals(singleton1, singleton2);
    }

    @Test
    public void 싱글톤은_안전하지않다() {

        Singleton[] singleton = new Singleton[10];

        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final int num = i;
            service.submit(() -> {
                singleton[num] = Singleton.getInstance();
            });
        }

        service.shutdown();

        for(Singleton s : singleton) {
            System.out.println(s.toString());
        }
    }

    @Test
    public void 싱글톤_ENUM() {
        SingletonEnum singletonEnum1 = SingletonEnum.SINGLETON;
        SingletonEnum singletonEnum2 = SingletonEnum.SINGLETON;

        System.out.println(singletonEnum1.getClass());
        System.out.println(singletonEnum2.getClass());
    }
}