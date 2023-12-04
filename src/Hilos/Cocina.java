package Hilos;

public class Cocina {
}

class CuboAgua{
    private String temperatura;
    private int capacidadTotal;
    private int capacidadActual;
    private boolean estaOcupado;

    public CuboAgua(String temperatura, int capacidadTotal){
        this.temperatura = temperatura;
        this.capacidadTotal = capacidadTotal;
        this.capacidadActual = this.capacidadTotal;
        this.estaOcupado = false;
    }

    public synchronized void sacarAgua(){

    }
}