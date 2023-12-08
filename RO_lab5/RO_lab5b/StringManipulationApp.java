import java.util.concurrent.CountDownLatch;

class StringManipulator implements Runnable {
    private StringBuilder stringBuilder;
    private final CountDownLatch latch;

    public StringManipulator(StringBuilder stringBuilder, CountDownLatch latch) {
        this.stringBuilder = stringBuilder;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // Виконуємо маніпуляції з рядком у потоці
                synchronized (stringBuilder) {
                    manipulateString();
                }

                // Чекаємо, доки всі рядки не мають однакову кількість символів A та D
                latch.await();
                break;
            }
        } catch (InterruptedException e) {
            // Обробляємо вийняття, яке виникає, коли потік перерваний
        }
    }

    // Метод для маніпуляцій із рядком
    private void manipulateString() {
        for (int i = 0; i < stringBuilder.length(); i++) {
            char currentChar = stringBuilder.charAt(i);

            // Виконуємо заміну символів у рядку
            if (currentChar == 'A') {
                stringBuilder.setCharAt(i, 'C');
            } else if (currentChar == 'C') {
                stringBuilder.setCharAt(i, 'A');
            } else if (currentChar == 'B') {
                stringBuilder.setCharAt(i, 'D');
            } else if (currentChar == 'D') {
                stringBuilder.setCharAt(i, 'B');
            }
        }
    }
}

public class StringManipulationApp {
    public static void main(String[] args) {
        // Ініціалізуємо рядки для кожного потоку
        StringBuilder str1 = new StringBuilder("ABCD");
        StringBuilder str2 = new StringBuilder("ABCD");
        StringBuilder str3 = new StringBuilder("ABCD");
        StringBuilder str4 = new StringBuilder("ABCD");

        // Ініціалізуємо CountDownLatch зі значенням 3 (три рядки)
        CountDownLatch latch = new CountDownLatch(3);

        // Створюємо потоки для кожного рядка
        Thread thread1 = new Thread(new StringManipulator(str1, latch));
        Thread thread2 = new Thread(new StringManipulator(str2, latch));
        Thread thread3 = new Thread(new StringManipulator(str3, latch));
        Thread thread4 = new Thread(new StringManipulator(str4, latch));

        // Запускаємо потоки
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            // Чекаємо завершення потоків
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            // Обробляємо вийняття, яке виникає, коли потік перерваний
            e.printStackTrace();
        }

        // Виведення результатів
        System.out.println("Result String 1: " + str1);
        System.out.println("Result String 2: " + str2);
        System.out.println("Result String 3: " + str3);
        System.out.println("Result String 4: " + str4);
    }
}
