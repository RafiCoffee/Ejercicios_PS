package Hilos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuenteCoches {
    public static Thread[] hilos = new Thread[10];
    public static void main(String[] args){
        Puente puente = new Puente();

        System.out.println("El puente soporta un peso máximo de 5000Kg y una capacidad para 3 coches, vamos a abrir el puente\n" +
                "El puente tiene un peso actual de " + puente.getPesoActual() + " y hay " + puente.getCochesActual() + " coches encima");

        for(int i = 0; i < hilos.length; i++){
            hilos[i] = new Thread(new SimulacionPuente(puente, new Coche(), "Coche " + (i+1)));
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
    private int pesoMax;
    private int pesoActual;
    private int cochesMax;
    private int cochesActual;
    private List<Coche> listaCoches;

    public Puente(){
        this.pesoMax = 5000;
        this.pesoActual = 0;
        this.cochesMax = 3;
        this.cochesActual = 0;
        this.listaCoches = new ArrayList<Coche>();
    }

    public boolean sePermitePaso(Coche coche){

        synchronized (listaCoches){
            if(getCochesActual() == this.cochesMax){
                return false;
            }else{
                if(getPesoActual() == this.pesoMax || (getPesoActual() + coche.getPeso()) > this.pesoMax){
                    return false;
                }else{
                    setCochesActual(true);
                    setPesoActual(coche.getPeso(), true);
                    listaCoches.add(coche);
                    return true;
                }
            }
        }
    }

    public void finalizarPaso(Coche coche){
        setCochesActual(false);
        setPesoActual(coche.getPeso(), false);

        System.out.println("El puente tiene un peso encima de " + getPesoActual() + " y hay " + getCochesActual() + " coches");

        synchronized (this) {
            this.notify();
        }
    }

    public synchronized int getPesoActual(){ return this.pesoActual; }
    public synchronized void setPesoActual(int pesoCoche, boolean entra){
        if(entra){
            this.pesoActual += pesoCoche;
        }else{
            this.pesoActual -= pesoCoche;
        }
    }
    public synchronized int getCochesActual(){ return this.cochesActual; }
    public synchronized void setCochesActual(boolean entra){
        if(entra){
            this.cochesActual++;
        }else{
            this.cochesActual--;
            listaCoches.remove(0);
        }
    }
}

class Coche{
    private int peso;
    private long duracionPuente;

    public Coche(){
        this.peso = new Random().nextInt(800, 2000);
        this.duracionPuente = new Random().nextLong(10000, 50000);
    }

    public int getPeso() { return this.peso; }

    public long getDuracionPuente() { return this.duracionPuente; }
}

class SimulacionPuente implements Runnable{
    private final Puente puente;
    private Coche coche;
    private String nombre;
    private long tiempoLlegada;

    public SimulacionPuente(Puente puente, Coche coche, String nombre){
        this.puente = puente;
        this.coche = coche;
        this.nombre = nombre;
        this.tiempoLlegada = new Random().nextLong(1000, 30000);
    }

    @Override
    public void run() {

        boolean sobrePuente = false;

        try {
            System.out.println(this.nombre + " esta llegando al puente con un peso de " + this.coche.getPeso());
            Thread.sleep(this.tiempoLlegada);
            System.out.println(this.nombre + " ha llegado al puente");

            synchronized (puente) {
                do {
                    sobrePuente = puente.sePermitePaso(this.coche);

                    if (!sobrePuente) {
                        System.out.println(this.nombre + " aún no puede entrar al puente\n" +
                                "El puente tiene un peso encima de " + puente.getPesoActual() + " y hay " + puente.getCochesActual() + " coches");
                        puente.wait();
                    } else {
                        break;
                    }
                } while (!sobrePuente);
            }

            System.out.println(this.nombre + " ha entrado al puente");
            Thread.sleep(this.coche.getDuracionPuente());

            System.out.println(this.nombre + " ha dejado el puente");
            puente.finalizarPaso(this.coche);
        } catch (InterruptedException e) {
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}
