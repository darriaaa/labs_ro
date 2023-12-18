import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Garden {
    private final int rows;
    private final int cols;
    private final int[][] plants;
    private final ReadWriteLock lock;

    public Garden(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.plants = new int[rows][cols];
        this.lock = new ReentrantReadWriteLock();
    }

    // Додати методи-геттери для rows та cols
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void waterPlants(int row, int col) {
        lock.writeLock().lock();
        try {
            if (isValidPosition(row, col)) {
                // Поливаємо рослину за координатами (row, col)
                plants[row][col]++;
                System.out.println("Садівник поливає рослину на позиції (" + row + ", " + col + ")");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void changePlantState(int row, int col, int newState) {
        lock.writeLock().lock();
        try {
            if (isValidPosition(row, col)) {
                // Змінюємо стан рослини за координатами (row, col)
                plants[row][col] = newState;
                System.out.println("Природа змінює стан рослини на позиції (" + row + ", " + col + ")");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void printGardenToFile(String fileName) {
        lock.readLock().lock();
        try {
            FileWriter writer = new FileWriter(fileName, true);
            try {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        writer.write(plants[i][j] + " ");
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void printGardenToConsole() {
        lock.readLock().lock();
        try {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    System.out.print(plants[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}

class Gardener implements Runnable {
    private final Garden garden;
    private final Random random;

    public Gardener(Garden garden) {
        this.garden = garden;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            int row = random.nextInt(garden.getRows());
            int col = random.nextInt(garden.getCols());
            garden.waterPlants(row, col);

            try {
                Thread.sleep(1000); // Почекати 1 секунду перед наступним поливом
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Nature implements Runnable {
    private final Garden garden;
    private final Random random;

    public Nature(Garden garden) {
        this.garden = garden;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            int row = random.nextInt(garden.rows);
            int col = random.nextInt(garden.cols);
            int newState = random.nextInt(10); // Генеруємо випадковий новий стан рослини
            garden.changePlantState(row, col, newState);

            try {
                Thread.sleep(2000); // Почекати 2 секунди перед наступною зміною стану
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class GardenMonitor1 implements Runnable {
    private final Garden garden;
    private final String fileName;

    public GardenMonitor1(Garden garden, String fileName) {
        this.garden = garden;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        while (true) {
            garden.printGardenToFile(fileName);

            try {
                Thread.sleep(3000); // Почекати 3 секунди перед наступним виведенням в файл
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class GardenMonitor2 implements Runnable {
    private final Garden garden;

    public GardenMonitor2(Garden garden) {
        this.garden = garden;
    }

    @Override
    public void run() {
        while (true) {
            garden.printGardenToConsole();

            try {
                Thread.sleep(4000); // Почекати 4 секунди перед наступним виведенням на консоль
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class GardenAppp {
    public static void main(String[] args) {
        Garden garden = new Garden(5, 5);

        Thread gardenerThread = new Thread(new Gardener(garden));
        Thread natureThread = new Thread(new Nature(garden));
        Thread monitor1Thread = new Thread(new GardenMonitor1(garden, "garden_state.txt"));
        Thread monitor2Thread = new Thread(new GardenMonitor2(garden));

        gardenerThread.start();
        natureThread.start();
        monitor1Thread.start();
        monitor2Thread.start();
    }
}
