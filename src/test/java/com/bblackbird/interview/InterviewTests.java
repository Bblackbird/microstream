package com.bblackbird.interview;

import org.junit.Test;
import org.junit.Assert;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

public class InterviewTests {

    // Factorial
    @Test
    public void FactorialTest() {

        long factorial = factorialRecursive(5);

        assertEquals(120, factorial);

    }

    public long factorialImperative(long n) {
        long result = 1;
        while(n >0) {
            result *= n;
            n--;
        }
        return result;
    }

    public long factorialRecursiveDumb(long n) {
        if(n <= 1)
            return 1;
        return n * factorialRecursiveDumb(n-1);
    }

    // 5, 5 x f(4) => 24 = 120
    // 4, 4 x f(3) => 6 = 24
    // 3, 3 x f(2) => 2 = 6
    // 2, 2 x f(1) => 2
    // 1, 1


    public long factorialRecursive(long n) {
        return factorialRecursive(n, 1);
    }

    public long factorialRecursive(long n, long acc) {

        if(n <= 1)
            return acc;

        return factorialRecursive(n -1,  n * acc);
    }

    // 5
    // 4, 5
    // 3, 4*5=20
    // 2, 20*3=60
    // 1, 60*2=120

    // Fibonacci
    @Test
    public void FibonacciTest() {
        long factorial = fibonacciDynamic(6);

        assertEquals(8, factorial);
    }

    public int fibonacciDynamic(int n) {

        if(n==0) return 0;
        if(n==1) return 1;

        int[] memo = new int[n];
        memo[0] = 0;
        memo[1] = 1;

        for(int i = 2; i < n; i++) {
            memo[i] = memo[i - 1] + memo[i -2];
        }

        return memo[n - 1] + memo[n - 2];

    }

    public int fibonacciDynamic2(int n) {

        if(n==0) return 0;

        int a = 0;
        int b = 1;
        for(int i = 2; i < n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return a + b;
    }

    public long fibonacciRecursiveDumb(long n) {

        if(n == 0) return 0;
        if(n == 1) return 1;
        return fibonacciRecursiveDumb(n-1) + fibonacciRecursiveDumb(n-2);
    }

    // 6, f(5) + f(4) => 5 + 3 = 8
    // 5, f(4) + f(3) => 3 + 2 = 5
    // 4, f(3) + f(2) => 2 + 1 = 3
    // 3, f(2) + f(1) => 1 + 1 = 2
    // 2, f(1) + f(0) => 1 + 0 = 1
    // 1 => 1
    // 0 => 0

    public int fibonacciRecursive(int n) {
        return fibonacciRecursive(n, new int[n + 1]);
    }

    public int fibonacciRecursive(int n, int[] memo) {

        if(n == 0 || n == 1)
            return n;

        if(memo[n] == 0) {
            memo[n] = fibonacciRecursive(n  - 1, memo) + fibonacciRecursive(n  - 2, memo);
        }
        return memo[n];
    }

    // 6, f(5) + f(4) => 5 + 3 = 8
    // 5, f(4) + f(3) => 3 + 2 = 5
    // 4, f(3) + f(2) => 2 + 1 = 3
    // 3, f(2) + f(1) => 1 + 1 = 2
    // 2, f(1) + f(0) => 1
    // 1 => 1
    // 0 => 0

}
