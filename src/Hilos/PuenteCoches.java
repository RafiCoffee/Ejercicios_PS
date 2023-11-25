package Hilos;

import java.util.Random;

public class PuenteCoches {
}

class Puente{
    private int pesoMax;
    private int pesoActual;
    private int cochesMax;
    private int cochesActual;

    public Puente(){
        this.pesoMax = 5000;
        this.pesoActual = 0;
        this.cochesMax = 3;
        this.cochesActual = 0;
    }

    public boolean sePermitePaso(Coche coche){

        if(getCochesActual() == this.cochesMax){
            return false;
        }else{
            if(getPesoActual() == this.pesoMax || (getPesoActual() + coche.getPeso()) > this.pesoMax){
                return false;
            }else{
                setCochesActual(true);
                setPesoActual(coche.getPeso(), true);
                return true;
            }
        }
    }

    public void finalizarPaso(Coche coche){
        setCochesActual(false);
        setPesoActual(coche.getPeso(), false);
    }

    public int getPesoActual(){ return this.pesoActual; }
    public void setPesoActual(int pesoCoche, boolean entra){ this.pesoActual = entra ? + pesoCoche : - pesoCoche; }
    public int getCochesActual(){ return this.cochesActual; }
    public void setCochesActual(boolean entra){ this.cochesActual = entra ? ++this.cochesActual : --this.cochesActual; }
}

class Coche{
    private int peso;
    private long duracionPuente;

    public Coche(){
        this.peso = new Random().nextInt(800, 2000);
        this.duracionPuente = new Random().nextLong(10, 50);
    }

    public int getPeso() { return this.peso; }

    public long getDuracionPuente() { return this.duracionPuente; }
}

class SimulacionPuente implements Runnable{
    private Puente puente;
    private long tiempoLlegada;

    public SimulacionPuente(Puente puente){
        this.puente = puente;
        this.tiempoLlegada = new Random().nextLong(1, 30);
    }

    @Override
    public void run() {

    }
}
