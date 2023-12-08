import java.util.Random;

public class Main {
    private static final Object lock = new Object(); // Об'єкт для синхронізації потоків
    private static int countA = 0; // Кількість символів A
    private static int countB = 0; // Кількість символів B
    private static int threadCount = 4; // Кількість потоків

    public static void main(String[] args) {
        // Створюємо та запускаємо чотири потоки
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new MyRunnable("ABCD".charAt(i)));
            threads[i].start();
        }

        // Очікуємо завершення всіх потоків
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Клас, який реалізує логіку для кожного потоку
    static class MyRunnable implements Runnable {
        private char symbol;

        public MyRunnable(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public void run() {
            String line = generateLine();
            System.out.println("Start line: " + line);

            while (true) {
                // Синхронізація потоків
                synchronized (lock) {
                    // Зміна символів в рядку
                    line = changeSymbols(line);

                    // Виведення рядку
                    System.out.println("Potik " + symbol + ": " + line);

                    // Перевірка умови завершення потоку
                    if (checkCompletion()) {
                        System.out.println("Potik " + symbol + " finished.");
                        break;
                    }
                }

                // Чекаємо невеликий випадковий проміжок часу перед наступною ітерацією
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Генерація початкового рядка
        private String generateLine() {
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                char randomChar = "ABCD".charAt(random.nextInt(4));
                builder.append(randomChar);
            }
            return builder.toString();
        }

        // Зміна символів в рядку
        private String changeSymbols(String line) {
            char[] chars = line.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == 'A') {
                    chars[i] = 'C';
                    countA++;
                } else if (chars[i] == 'C') {
                    chars[i] = 'A';
                    countA++;
                } else if (chars[i] == 'B') {
                    chars[i] = 'D';
                    countB++;
                } else if (chars[i] == 'D') {
                    chars[i] = 'B';
                    countB++;
                }
            }
            return new String(chars);
        }

        // Перевірка умови завершення потоку
        private boolean checkCompletion() {
            return countA >= 30 || countB >= 30;
        }
    }
}
