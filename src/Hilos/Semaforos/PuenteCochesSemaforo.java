package Hilos.Semaforos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class PuenteCochesSemaforo {
    public static Thread[] hilos = new Thread[10];
    public static void main(String[] args){
        Puente puente = new Puente();

        System.out.println("El puente soporta un peso máximo de 5000Kg y una capacidad para 3 coches, vamos a abrir el puente");

        for(int i = 0; i < hilos.length; i++){
            hilos[i] = new Thread(new SimulacionPuente(puente, new Coche("Coche " + (i+1))));
            hilos[i].start();
        }

        for(int i = 0; i < hilos.length; i++){
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error detectado: " + e.getMessage());
            }
        }
    }
}

class Puente{
    private int pesoMax = 5000;
    private Semaphore coches = new Semaphore(3);
    private Semaphore pesoPuente = new Semaphore(pesoMax);
    private Semaphore cumpleCondiciones = new Semaphore(1);

    public void pasarPuente(Coche coche) throws InterruptedException {
        if(pesoPuente.availablePermits() < coche.getPeso()){
            System.out.println("El " + coche.getNombre() + " esta esperando para entrar al puente debido a que su peso es de " + coche.getPeso() + "Kg" +
                    " y el puente actualmente esta soportando un peso de " + (pesoMax - pesoPuente.availablePermits()) + "Kg");
            cumpleCondiciones.acquire();
        }
        if(coches.availablePermits() == 0){
            System.out.println("El " + coche.getNombre() + " no puede entrar al puente debido a que solo esta permitida la entrada de 3 coches simultaneamente");
            cumpleCondiciones.acquire();
        }
        pesoPuente.acquire(coche.getPeso());
        coches.acquire();
        cumpleCondiciones.release();
        System.out.println("El " + coche.getNombre() + " ha entrado al puente");
        long duracionPuente = new Random().nextLong(10000, 50000);
        Thread.sleep(duracionPuente);

        System.out.println("\nEl " + coche.getNombre() + " ha dejado el puente");
        System.out.println("------------------------------\n");
        coches.release();
        pesoPuente.release(coche.getPeso());
        cumpleCondiciones.release();
    }
}

class Coche{
    private String nombre;
    private int peso;
    private long duracionPuente;

    public Coche(String nombre){
        this.nombre = nombre;
        this.peso = new Random().nextInt(800, 2000);
        this.duracionPuente = new Random().nextLong(10000, 50000);
    }

    public int getPeso() { return this.peso; }

    public long getDuracionPuente() { return this.duracionPuente; }

    public String getNombre() { return this.nombre; }
}

class SimulacionPuente implements Runnable{
    private final Puente puente;
    private Coche coche;
    private long tiempoLlegada;

    public SimulacionPuente(Puente puente, Coche coche){
        this.puente = puente;
        this.coche = coche;
        this.tiempoLlegada = new Random().nextLong(1000, 30000);
    }

    @Override
    public void run() {

        try {
            System.out.println(this.coche.getNombre() + " esta llegando al puente con un peso de " + this.coche.getPeso());
            Thread.sleep(this.tiempoLlegada);
            System.out.println("\n------------------------------");
            System.out.println(this.coche.getNombre() + " ha llegado al puente");

            puente.pasarPuente(this.coche);

        } catch (InterruptedException e) {
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}