package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {

    private int a, b;
    private List<Integer> primes;

    private final Object pauseLock;
    private final Control control;

    public PrimeFinderThread(int a, int b, Object pauseLock, Control control) {
        this.a = a;
        this.b = b;
        this.pauseLock = pauseLock;
        this.control = control;
        this.primes = new LinkedList<>();
    }

    @Override
    public void run() {
        for (int i = a; i < b; i++) {

            synchronized (pauseLock) {
                while (control.isPaused()) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            if (isPrime(i)) {
                primes.add(i);
            }
        }
    }

    boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public int getPrimeCount() {
        return primes.size();
    }
}
