package Hilos;

import java.util.Random;

class Hilo1 implements Runnable {
    private String nombre;
    public Hilo1(String nombre){ this.nombre = nombre; }

    @Override
    public void run() {
        System.out.println("Hola soy el hilo de la clase " + getClass().getName() + ", mi nombre es " + this.nombre.toLowerCase() + " y voy a comenzar");
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

class Hilo2 implements Runnable{
    private String nombre;
    public Hilo2(String nombre){ this.nombre = nombre; }

    @Override
    public void run() {
        System.out.println("Hola soy el hilo de la clase " + getClass().getName() + ", mi nombre es " + this.nombre.toLowerCase() + " y voy a comenzar");
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
        Thread[] hilos = new Thread[4];
        for(int i = 0; i < 4; i++){
            boolean hiloLanzado;
            if((hiloLanzado = new Random().nextBoolean())){
                hilos[i] = new Thread(new Hilo1("Hilo Lanzado " + i));
            }else{
                hilos[i] = new Thread(new Hilo2("Hilo Lanzado " + i));
            }
        }
        hilos[0].start();
        hilos[1].start();
        hilos[2].start();
        hilos[3].start();

        try{
            hilos[0].join();
            hilos[1].join();
            hilos[2].join();
            hilos[3].join();
        }catch(InterruptedException e){
            System.err.println("Error no deseado: " + e.getMessage() + "\nHilo principal interrumpido");
        }finally {
            System.out.println("Hilo principal terminado");
        }
    }
}
