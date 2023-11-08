package Hilos;

import java.util.Random;

public class Hilo implements Runnable {
    private String nombre;
    public Hilo(String nombre){ this.nombre = nombre; }

    @Override
    public void run() {
        System.out.println("Hola soy el " + this.nombre.toLowerCase() + " y voy a comenzar");
        for(int i = 0; i < 5; i++){
            int pausa = 10 + new Random().nextInt(500 - 10);
            System.out.println("El " + this.nombre.toLowerCase() + " va a realizar una pausa de " + pausa);
            try{
                Thread.sleep(pausa);

                System.out.println("El " + this.nombre.toLowerCase() + " terminó su pausa");
            }catch(InterruptedException e){
                System.err.println("Error no deseado: " + e.getMessage());
            }
        }
    }
}

class Main{
    public static void main(String[] args){
        Thread hiloP = new Thread(new Hilo("Hilo Principal"));
        Thread hiloS = new Thread(new Hilo("Hilo Secundario"));
        hiloP.start();
        hiloS.start();

        try{
            hiloP.join();
            hiloS.join();
        }catch(InterruptedException e){
            System.err.println("Error no deseado: " + e.getMessage() + "\nHilo principal interrumpido");
        }finally {
            System.out.println("Hilo principal terminado");
        }
    }
}
