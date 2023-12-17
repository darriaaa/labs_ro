import numpy as np
import threading
import time

def generate_matrix(rows, cols):
    return np.random.rand(rows, cols)

def print_matrix(matrix):
    for row in matrix:
        print(row)

def sequential_fox_multiply(A, B, num_blocks):
    n = A.shape[0]
    block_size = n // num_blocks
    result = np.zeros((n, n))

    for i in range(num_blocks):
        for j in range(num_blocks):
            for k in range(num_blocks):
                result[i * block_size:(i + 1) * block_size, j * block_size:(j + 1) * block_size] += \
                    np.dot(A[i * block_size:(i + 1) * block_size, k * block_size:(k + 1) * block_size],
                           B[k * block_size:(k + 1) * block_size, j * block_size:(j + 1) * block_size])

    return result

def parallel_fox_multiply(A, B, num_blocks, num_threads):
    n = A.shape[0]
    block_size = n // num_blocks
    result = np.zeros((n, n))

    def multiply_block(i, j):
        for k in range(num_blocks):
            result[i * block_size:(i + 1) * block_size, j * block_size:(j + 1) * block_size] += \
                np.dot(A[i * block_size:(i + 1) * block_size, k * block_size:(k + 1) * block_size],
                       B[k * block_size:(k + 1) * block_size, j * block_size:(j + 1) * block_size])

    threads = []
    for i in range(num_blocks):
        for j in range(num_blocks):
            thread = threading.Thread(target=multiply_block, args=(i, j))
            threads.append(thread)

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()

    return result

if __name__ == "__main__":
    matrix_size = 6
    rows_A, cols_A, rows_B, cols_B = matrix_size, matrix_size, matrix_size, matrix_size

    matrix_A = generate_matrix(rows_A, cols_A)
    matrix_B = generate_matrix(rows_B, cols_B)

    num_blocks = 2

    # Sequential Fox Algorithm
    start_time = time.time()
    result_sequential_fox = sequential_fox_multiply(matrix_A, matrix_B, num_blocks)
    sequential_fox_time = time.time() - start_time

    print("Sequential Fox Algorithm (1 block)")
    print_matrix(result_sequential_fox)
    print("Time:", sequential_fox_time, "seconds\n")

    # Parallel Fox Algorithm for 2 threads
    start_time = time.time()
    result_parallel_fox_2_threads = parallel_fox_multiply(matrix_A, matrix_B, num_blocks, num_threads=2)
    parallel_fox_time_2_threads = time.time() - start_time

    print("Parallel Fox Algorithm (2 threads)")
    print_matrix(result_parallel_fox_2_threads)
    print("Time:", parallel_fox_time_2_threads, "seconds\n")

    # Parallel Fox Algorithm for 4 threads
    start_time = time.time()
    result_parallel_fox_4_threads = parallel_fox_multiply(matrix_A, matrix_B, num_blocks, num_threads=4)
    parallel_fox_time_4_threads = time.time() - start_time

    print("Parallel Fox Algorithm (4 threads)")
    print_matrix(result_parallel_fox_4_threads)
    print("Time:", parallel_fox_time_4_threads, "seconds")

