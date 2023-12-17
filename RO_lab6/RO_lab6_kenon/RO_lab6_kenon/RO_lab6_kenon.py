import numpy as np
import threading
import time

def generate_matrix(rows, cols):
    return np.random.rand(rows, cols)

def print_matrix(matrix):
    for row in matrix:
        print(row)

def cannon_multiply(A, B, num_threads):
    n = A.shape[0]
    block_size = n // num_threads
    result = np.zeros((n, n))

    def multiply_block(i, j):
        nonlocal result
        for k in range(n):
            result[i:i + block_size, j:j + block_size] += \
                np.dot(A[i:i + block_size, k:k + block_size], B[k:k + block_size, j:j + block_size])

    threads = []
    for i in range(0, n, block_size):
        for j in range(0, n, block_size):
            thread = threading.Thread(target=multiply_block, args=(i, j))
            threads.append(thread)

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()

    return result

if __name__ == "__main__":
    matrix_size = 4
    rows_A, cols_A, rows_B, cols_B = matrix_size, matrix_size, matrix_size, matrix_size

    matrix_A = generate_matrix(rows_A, cols_A)
    matrix_B = generate_matrix(rows_B, cols_B)

    num_threads_values = [1, 2, 4]

    print("Matrix A:")
    print_matrix(matrix_A)
    print("\nMatrix B:")
    print_matrix(matrix_B)

    for num_threads in num_threads_values:
        start_time = time.time()
        result_cannon = cannon_multiply(matrix_A, matrix_B, num_threads)
        cannon_time = time.time() - start_time

        print(f"\nCannon Algorithm ({num_threads} threads)")
        print("Result:")
        print_matrix(result_cannon)
        print("Time:", cannon_time, "seconds\n")
