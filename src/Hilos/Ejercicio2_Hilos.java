package Hilos;

public class Ejercicio2_Hilos {
    public static void main(String[] args){
        Thread hiloActual = Thread.currentThread();

        //"MIN_PRIORITY" tiene un valor entero de 1
        hiloActual.setPriority(Thread.MIN_PRIORITY);

        System.out.println("La prioridad mínima de un hilo es: " + hiloActual.getPriority());

        //"MAX_PRIORITY" tiene un valor entero de 10
        hiloActual.setPriority(Thread.MAX_PRIORITY);

        System.out.println("La prioridad máxima de un hilo es: " + hiloActual.getPriority());
    }
}
