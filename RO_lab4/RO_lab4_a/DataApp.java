//лаб 4 а ро

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

class Database {
    private Map<String, String> data = new HashMap<>();
    private Semaphore readLock = new Semaphore(1);
    private Semaphore writeLock = new Semaphore(1);
    private int readersCount = 0;
    private String filePath = "A:\\уник\\розп обч\\RO_lab4_a\\database.txt"; // Шлях до файлу бази даних

    public Database() {
        loadFromFile(); // Завантаження даних з файлу при створенні об'єкта
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    data.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + " - " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String findPhoneNumber(String lastName) throws InterruptedException {
        readLock.acquire();
        readersCount++;
        if (readersCount == 1) {
            writeLock.acquire();
        }
        readLock.release();

        // Зчитування з бази даних
        String phoneNumber = data.get(lastName);

        readLock.acquire();
        readersCount--;
        if (readersCount == 0) {
            writeLock.release();
        }
        readLock.release();

        return phoneNumber;
    }

    public String findNameByPhoneNumber(String phoneNumber) throws InterruptedException {
        readLock.acquire();
        readersCount++;
        if (readersCount == 1) {
            writeLock.acquire();
        }
        readLock.release();

        // Зчитування з бази даних
        String name = null;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue().equals(phoneNumber)) {
                name = entry.getKey();
                break;
            }
        }

        readLock.acquire();
        readersCount--;
        if (readersCount == 0) {
            writeLock.release();
        }
        readLock.release();

        return name;
    }

    public void addRecord(String lastName, String phoneNumber) throws InterruptedException {
        writeLock.acquire();

        // Додавання запису в базу даних
        data.put(lastName, phoneNumber);
        System.out.println("Added record: " + lastName + " - " + phoneNumber);

        // Збереження в файл
        saveToFile();

        writeLock.release();
    }

    public void removeRecord(String lastName) throws InterruptedException {
        writeLock.acquire();

        // Видалення запису з бази даних
        data.remove(lastName);
        System.out.println("Removed record: " + lastName);

        // Збереження в файл
        saveToFile();

        writeLock.release();
    }
}

class Reader implements Runnable {
    // Решта коду залишається незмінним...
}

class NameFinder implements Runnable {
    // Решта коду залишається незмінним...
}

class Writer implements Runnable {
    // Решта коду залишається незмінним...
}

public class DataApp {
    public static void main(String[] args) {
        Database database = new Database();

        // Створення та запуск потоків для різних операцій
        Thread phoneFinderThread = new Thread(new PhoneFinder(database, "Doe"), "PhoneFinder");
        Thread nameByPhoneFinderThread = new Thread(new NameByPhoneFinder(database, "123-456-789"),
                "NameByPhoneFinder");
        Thread addRecordThread = new Thread(new RecordModifier(database, "Smith", "987-654-321", true), "AddRecord");
        Thread removeRecordThread = new Thread(new RecordModifier(database, "Doe", "", false), "RemoveRecord");

        // Запуск потоків
        phoneFinderThread.start();
        nameByPhoneFinderThread.start();
        addRecordThread.start();
        removeRecordThread.start();

        // Чекаємо завершення всіх потоків
        try {
            phoneFinderThread.join();
            nameByPhoneFinderThread.join();
            addRecordThread.join();
            removeRecordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
