package singleton;

public class DoubleCheckedSingleton {
    private static volatile DoubleCheckedSingleton INSTANCE;

    public DoubleCheckedSingleton() {}

    public static DoubleCheckedSingleton getInstance() {
        if (INSTANCE == null) {
            synchronized (DoubleCheckedSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DoubleCheckedSingleton();
                }
            }
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        DoubleCheckedSingleton doubleCheckedSingleton = new DoubleCheckedSingleton();
        DoubleCheckedSingleton instance = doubleCheckedSingleton.getInstance();
        System.out.println(instance);
    }
}
