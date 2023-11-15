package Hilos;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class NumeroOculto {
    private int numeroOculto;
    private boolean juegoTerminado;

    public NumeroOculto() {
        numeroOculto = new Random().nextInt(101);
        juegoTerminado = false;
        System.out.println("El numero oculto es " + numeroOculto + "\n");
    }

    public synchronized int NumeroPropuesto(int numero, String nombreHilo) {
        if (juegoTerminado) {
            System.err.println("El " + nombreHilo + " ha propuesto un número, pero el juego ya ha terminado");
            return -1;
        } else if (numeroOculto == numero) {
            juegoTerminado = true;
            return 1;
        } else {
            return 0;
        }
    }

    public synchronized boolean getJuegoTerminado() { return this.juegoTerminado; }
    public synchronized void setJuegoTerminado(boolean terminado) { this.juegoTerminado = terminado; }
}

class HiloAdivina implements Runnable {
    private String nombreHilo;
    private NumeroOculto numeroOculto;
    private NumeroOcultoMain numerosPropuestos = new NumeroOcultoMain();

    HiloAdivina(String nombreHilo, NumeroOculto numeroOculto) {
        this.nombreHilo = nombreHilo;
        this.numeroOculto = numeroOculto;
    }

    public int generarPropuesta(){
        int propuesta;
        boolean numeroYaPropuesto = false;
        do{
            propuesta = new Random().nextInt(101);
            for(int recorrerLista : numerosPropuestos.numerosPropuestosList){
                if(recorrerLista == propuesta){
                    numeroYaPropuesto = true;
                    break;
                }else{
                    numeroYaPropuesto = false;
                }
            }

            if(!numeroYaPropuesto){
                numerosPropuestos.numerosPropuestosList.add(propuesta);
            }

        }while (numeroYaPropuesto);

        return propuesta;
    }

    @Override
    public void run() {
        System.out.println("Soy el " + this.nombreHilo + " y voy a intentar adivinar el número oculto");

        int propuesta;

        do {
            propuesta = generarPropuesta();
            System.out.println("El " + this.nombreHilo + " propone: " + propuesta);

            int resultado = numeroOculto.NumeroPropuesto(propuesta, this.nombreHilo);

            switch (resultado){
                case -1:
                    System.out.println("Juego terminado, cerrando ejecucion del " + this.nombreHilo);
                    break;

                case 1:
                    System.out.println("El " + this.nombreHilo + " adivinó el número");
                    synchronized (numeroOculto){
                        numeroOculto.setJuegoTerminado(true);
                        System.out.println("\nEl numero oculto era: " + propuesta);
                        System.out.println("Se han propuesto un total de " + numerosPropuestos.numerosPropuestosList.size() + " números en el " + this.nombreHilo + " hasta encontrar el correcto");
                    }
                    break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Error detectado: " + e.getMessage());
            }

        } while (!numeroOculto.getJuegoTerminado());
    }
}

class NumeroOcultoMain {
    public List<Integer> numerosPropuestosList = new ArrayList<Integer>();
    public static Thread[] hilos = new Thread[10];
    public static void main(String[] args) {
        NumeroOculto numeroOculto = new NumeroOculto();

        for(int i = 0; i < hilos.length; i++){
            hilos[i] = new Thread(new HiloAdivina("Hilo " + i, numeroOculto));
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