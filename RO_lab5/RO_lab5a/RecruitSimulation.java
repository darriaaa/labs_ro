//ро лаб 5а

import java.util.concurrent.Semaphore;

class Recruit implements Runnable {
    private static final int MAX_RECRUITS = 100;
    private static final int GROUP_SIZE = 50;

    private static Semaphore semaphore = new Semaphore(GROUP_SIZE, true);

    private int id;

    public Recruit(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // Захоплюємо дозвіл від семафора
                semaphore.acquire();
                System.out.println("Rookie " + id + " is moving...");

                // Симулюємо рішення рекрута повернути направо чи наліво
                boolean turnRight = Math.random() < 0.5;

                // Симулюємо рух рекрута
                Thread.sleep(100);

                // Звільняємо дозвіл семафора
                semaphore.release();

                // Перевіряємо, чи рекрут бачить спиною сусіда
                boolean seesBack = Math.random() < 0.5;

                if (turnRight && seesBack || !turnRight && !seesBack) {
                    System.out.println("Rookie " + id + " made the correct move!");
                } else {
                    System.out.println("Rookie " + id + " made a mistake, turning around...");
                }
            }
        } catch (InterruptedException e) {
            // Потік перервано
        }
    }

    public static int getMaxRecruits() {
        return MAX_RECRUITS;
    }
}

public class RecruitSimulation {
    public static void main(String[] args) {
        
        // Створюємо та запускаємо кілька потоків для рекрутів
        for (int i = 0; i < Recruit.getMaxRecruits(); i++) {
            new Thread(new Recruit(i)).start();
        }
    }
}
