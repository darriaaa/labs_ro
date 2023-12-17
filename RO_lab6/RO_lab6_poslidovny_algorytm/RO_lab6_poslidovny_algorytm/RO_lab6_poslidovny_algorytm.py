import threading
import numpy as np
import time

# Генерація випадкових матриць
def generate_matrix(rows, cols):
    return np.random.rand(rows, cols)

# Послідовний алгоритм множення матриць
def sequential_multiply(A, B):
    return np.dot(A, B)

# Паралельний алгоритм множення матриць для заданої кількості потоків
def parallel_multiply(A, B, num_threads):
    result = np.zeros((A.shape[0], B.shape[1]))

    def multiply_chunk(start, end):
        for i in range(start, end):
            result[i, :] = np.dot(A[i, :], B)

    threads = []
    chunk_size = A.shape[0] // num_threads

    for i in range(num_threads):
        start = i * chunk_size
        end = start + chunk_size if i < num_threads - 1 else A.shape[0]
        thread = threading.Thread(target=multiply_chunk, args=(start, end))
        threads.append(thread)

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()

    return result

# Вивід матриці у вигляді таблиці
def print_matrix(matrix):
    for row in matrix:
        print(row)

if __name__ == "__main__":
    # Розміри матриць
    matrix_size = 3
    rows_A, cols_A, rows_B, cols_B = matrix_size, matrix_size, matrix_size, matrix_size

    # Генерація матриць
    matrix_A = generate_matrix(rows_A, cols_A)
    matrix_B = generate_matrix(rows_B, cols_B)

    # Послідовний алгоритм
    start_time = time.time()
    result_sequential = sequential_multiply(matrix_A, matrix_B)
    sequential_time = time.time() - start_time

    # Вивід результатів
    print("Sequential Algorithm (1 thread)")
    print_matrix(result_sequential)
    print("Time:", sequential_time, "seconds\n")

    # Паралельний алгоритм для 2 потоків
    start_time = time.time()
    result_parallel_2 = parallel_multiply(matrix_A, matrix_B, num_threads=2)
    parallel_time_2_threads = time.time() - start_time

    # Вивід результатів
    print("Parallel Algorithm (2 threads)")
    print_matrix(result_parallel_2)
    print("Time:", parallel_time_2_threads, "seconds\n")

    # Паралельний алгоритм для 4 потоків
    start_time = time.time()
    result_parallel_4 = parallel_multiply(matrix_A, matrix_B, num_threads=4)
    parallel_time_4_threads = time.time() - start_time

    # Вивід результатів
    print("Parallel Algorithm (4 threads)")
    print_matrix(result_parallel_4)
    print("Time:", parallel_time_4_threads, "seconds")

