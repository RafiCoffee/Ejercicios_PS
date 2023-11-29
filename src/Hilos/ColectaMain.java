package Hilos;

import java.util.Random;

public class ColectaMain {
    public static void main(String[] args) {

    }
}

class Colecta{
    private int objetivoColecta;
    private int colectaActual;

    Colecta(){
        this.objetivoColecta = 2000;
        this.colectaActual = 0;
    }

    public synchronized int getObjetivoColecta(){ return this.objetivoColecta; }
    public synchronized int getColectaActual(){ return this.colectaActual; }

    public synchronized void incrementarColectaActual(int colectaActual) {
        this.colectaActual += colectaActual;
    }

    public synchronized boolean colectaTerminada(){
        return getColectaActual() >= getObjetivoColecta();
    }
}

class Voluntario implements Runnable{
    private String nombre;
    private Colecta colecta;
    private int numeroRecolectas;
    private int totalColectaIndividual;

    Voluntario(String nombre, Colecta colecta){
        this.nombre = nombre;
        this.colecta = colecta;
        this.numeroRecolectas = 0;
        this.totalColectaIndividual = 0;
    }

    @Override
    public void run(){
        System.out.println("Soy el voluntario " + this.nombre + " y voy a comenzar a recolectar");

        while (!colecta.colectaTerminada()){
            long tiempoColecta = new Random().nextLong(10, 200);
            int recolecta = new Random().nextInt(4, 25);
            this.totalColectaIndividual += recolecta;

            try {
                Thread.sleep(tiempoColecta);

                System.out.println(this.nombre + " ha recolectado " + recolecta);
                colecta.incrementarColectaActual(recolecta);
                numeroRecolectas++;

            } catch (InterruptedException e) {
                System.err.println("Error detectado: " + e.getMessage());
            }
        }

        System.out.println("Soy " + this.nombre + " y he recolectado " + this.numeroRecolectas + " veces con un total de " + this.totalColectaIndividual);

    }
}