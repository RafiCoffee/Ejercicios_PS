package Hilos;

public class Ejercicio1_Hilos {
    public static void main(String[] args){
        Thread hiloActual = Thread.currentThread();

        System.out.println("El id de este hilo es " + hiloActual.getId() + "\n" +
                "El nombre de este hilo es " + hiloActual.getName() + "\n" +
                "Su estado es " + hiloActual.getState() + "\n" +
                "Su estado isAlive es " + hiloActual.isAlive());
    }
}
