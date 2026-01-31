package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Control extends Thread {

    private static final int NTHREADS = 3;
    private static final int MAXVALUE = 30000000;
    private static final int TMILISECONDS = 5000;
    private final int NDATA = MAXVALUE / NTHREADS;
    private PrimeFinderThread[] pft;
    // Se agregan estos nuevos componentes para añadir el lock y la pausa en forma de booleano 
    private final Object pauseLock = new Object();
    private volatile boolean paused = false;

    private Control() {
        this.pft = new PrimeFinderThread[NTHREADS];
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            pft[i] = new PrimeFinderThread(
                    i * NDATA,
                    (i + 1) * NDATA,
                    pauseLock,
                    this
            );
        }

        pft[i] = new PrimeFinderThread(
                i * NDATA,
                MAXVALUE + 1,
                pauseLock,
                this
        );
    }

    public static Control newControl() {
        return new Control();
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {

        for (PrimeFinderThread thread : pft) {
            thread.start();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                Thread.sleep(TMILISECONDS);

                synchronized (pauseLock) {
                    paused = true;
                }

                int totalPrimes = 0;
                for (PrimeFinderThread thread : pft) {
                    totalPrimes += thread.getPrimeCount();
                }

                System.out.println("Primos encontrados hasta ahora: " + totalPrimes);
                System.out.println("Presione ENTER para continuar...");

                br.readLine();

                synchronized (pauseLock) {
                    paused = false;
                    pauseLock.notifyAll();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
