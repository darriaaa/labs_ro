//лаб 4а

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.*;

class Database {
    private final Map<String, String> data = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public void findPhonesByLastName(String lastName) {
        readLock.lock();
        try {
            String phoneNumber = data.get(lastName);
            if (phoneNumber != null) {
                System.out.println("Phones for last name " + lastName + ": " + phoneNumber);
            } else {
                System.out.println("No record found for last name " + lastName);
            }
        } finally {
            readLock.unlock();
        }
    }

    public void findNamesByPhone(String phone) {
        readLock.lock();
        try {
            String fullName = findNameByPhone(phone);
            if (fullName != null) {
                System.out.println("Full names for phone " + phone + ": " + fullName);
            } else {
                System.out.println("No record found for phone " + phone);
            }
        } finally {
            readLock.unlock();
        }
    }

    private String findNameByPhone(String phone) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue().equals(phone)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addOrUpdateRecord(String fullName, String phone) {
        writeLock.lock();
        try {
            String existingPhone = data.get(fullName);

            // Перевірка чи запис існує вже в базі даних
            if (existingPhone != null) {
                // Запис існує, запитати користувача, чи перезаписати
                System.out.println("Record for '" + fullName + "' already exists with phone: " + existingPhone);
                System.out.println("Do you want to update it? (yes/no): ");

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<String> userInputFuture = executorService.submit(() -> {
                    Scanner scanner = new Scanner(System.in);
                    return scanner.nextLine().trim().toLowerCase();
                });

                try {
                    String userInput = userInputFuture.get(5, TimeUnit.SECONDS); // Очікувати введення 5 секунд
                    if (userInput.equals("yes")) {
                        // Перезаписати існуючий запис
                        data.put(fullName, phone);
                        System.out.println("Record updated successfully.");
                    } else {
                        // Не перезаписувати, вивести повідомлення
                        System.out.println("Record not updated.");
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    executorService.shutdown();
                }
            } else {
                // Додати новий запис
                data.put(fullName, phone);
                System.out.println("New record added successfully.");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteRecord(String fullName) {
        writeLock.lock();
        try {
            if (data.containsKey(fullName)) {
                data.remove(fullName);
                System.out.println("Record for '" + fullName + "' deleted successfully.");
            } else {
                System.out.println("No record found for '" + fullName + "'. Deletion aborted.");
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void loadDataFromFile(String filename) {
        writeLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    data.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public void saveDataToFile(String filename) {
        readLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + " - " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    public void printFileContents(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class MultithreadedDatabaseApp {
    public static void main(String[] args) {
        Database database = new Database();

        // Ініціалізація даними з файлу
        database.loadDataFromFile("dat.txt");

        // Вивід вмісту файлу у консоль
        System.out.println("File Contents:");
        database.printFileContents("dat.txt");

        // Приклад використання потоків
        Thread thread1 = new Thread(() -> database.findPhonesByLastName("Bob"));
        Thread thread2 = new Thread(() -> database.findNamesByPhone("555-5678"));
        Thread thread3 = new Thread(() -> database.addOrUpdateRecord("Barbara Red", "543-5764"));
        Thread thread4 = new Thread(() -> database.deleteRecord("Bob Williams"));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        // Очікування завершення потоків
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Збереження даних в файл
        database.saveDataToFile("dat.txt");

        // Вивід оновленого вмісту файлу у консоль
        System.out.println("\nUpdated File Contents:");
        database.printFileContents("dat.txt");
    }
}
