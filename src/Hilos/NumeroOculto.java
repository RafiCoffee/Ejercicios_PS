package Hilos;

import java.util.Random;

/*public class NumeroOculto {

    public int NumeroPropuesto(int numero){


        return 0;
    }

}*/

class HiloAdivina implements Runnable {

    private String nombreHilo;

    HiloAdivina(String nombreHilo){
        this.nombreHilo = nombreHilo;
    }

    @Override
    public void run() {
        System.out.println("Soy el hilo " + this.nombreHilo + "y voy a intentar adivinar el número oculto");

        int numeroPropuesto = new Random().nextInt(101);
        NumeroOcultoMain numOcul = new NumeroOcultoMain();

        numOcul.NumeroPropuesto(numeroPropuesto);
    }
}

class NumeroOcultoMain{

    public static void main(String[] args) {
        int numAleatorio = new Random().nextInt(101);
        System.out.println("El numero oculto es: ");

        
    }

    public int NumeroPropuesto(int numero){


        return 0;
    }
}