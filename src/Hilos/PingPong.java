package Hilos;

import java.util.Random;

public class PingPong {
    public static void main(String[] args) throws InterruptedException{
        Jugador jugador1 = new Jugador("Santi", 1);
        Jugador jugador2 = new Jugador("Gabriel", 2);
        Partida partida = new Partida(jugador1, jugador2);
        Bola bola = new Bola(partida);

        Thread hilo1 = new Thread(new HiloJugador(jugador1, bola, partida, true));
        Thread hilo2 = new Thread(new HiloJugador(jugador2, bola, partida, false));

        System.out.println("---------\tComienza la partida\t\t----------");

        hilo1.start();
        hilo2.start();

        hilo1.join();
        hilo2.join();
    }
}

class Partida{
    private Jugador jugador1;
    private Jugador jugador2;
    private boolean isPeloteoTerminado;
    private boolean isPartidaTerminada;
    
    public Partida(Jugador jugador1, Jugador jugador2){
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.isPeloteoTerminado = false;
    }

    public synchronized boolean isPeloteoTerminado() { return this.isPeloteoTerminado; }

    public synchronized boolean isPartidaTerminada() { return this.isPartidaTerminada; }

    public synchronized void terminarPeloteo(Jugador perdedorPeloteo) {
        if(perdedorPeloteo.getIdJugador() == 1){
            jugador2.jugadorPuntua();
            System.out.println(jugador2.getNombre() + " ha ganado un punto tras " + jugador2.getNumeroGolpes() + " golpes");
        }else{
            jugador1.jugadorPuntua();
            System.out.println(jugador1.getNombre() + " ha ganado un punto tras " + jugador1.getNumeroGolpes() + " golpes");
        }
        jugador1.resetGolpes();
        jugador2.resetGolpes();

        comprobarPuntos();
        this.isPeloteoTerminado = true;
    }

    public synchronized void comprobarPuntos(){
        if(jugador1.getPuntos() >= 5){
            terminarPartida(jugador1, jugador2);
        }else if(jugador2.getPuntos() >= 5){
            terminarPartida(jugador2, jugador1);
        }else{
            System.out.println(jugador1.toString() + jugador2.toString());
        }
    }

    public synchronized void terminarPartida(Jugador ganador, Jugador perdedor){
        System.out.println("\n" + ganador.getNombre() + " ha ganado la partida con " + ganador.getPuntos() + " puntos\n" +
                perdedor.getNombre() + " ha perdido la partida con " + perdedor.getPuntos() + " puntos");
        this.isPartidaTerminada = true;
        Thread.currentThread().interrupt();
        notifyAll();
        System.out.println("---------\tFin de la partida\t----------");
        System.exit(0);
    }
    
    public synchronized void nuevoPeloteo() { this.isPeloteoTerminado = false; }
}

class Bola{
    private int numeroTurno;
    private Partida partida;

    public Bola(Partida partida) {
        this.numeroTurno = 0;
        this.partida = partida;
    }
    public synchronized void golpearBola(Jugador jugador, boolean jugadorPuntua) throws InterruptedException {
        if(!this.partida.isPartidaTerminada()){
            long tiempoBola = new Random().nextLong(1000, 2001);
            System.out.println(jugador.getNombre() + " golpea la bola");
            jugador.jugadorGolpea();
            Thread.sleep(tiempoBola);
            this.numeroTurno++;
            notify();
        }else{
            notifyAll();
        }
    }

    public synchronized void esperarBola(Jugador jugador) throws InterruptedException {
        if(!this.partida.isPartidaTerminada()){
            if(jugador.getIdJugador() == 1){
                while (this.numeroTurno %2 != 0) {
                    wait();
                }
            }else{
                while (this.numeroTurno %2 == 0) {
                    wait();
                }
            }
        }else{
            notifyAll();
        }
    }
}

class Jugador{
    private String nombre;
    private int idJugador;
    private int numeroGolpes;
    private int puntos;
    public Jugador(String nombre, int idJugador){
        this.nombre = nombre;
        this.idJugador = idJugador;
        this.numeroGolpes = 0;
        this.puntos = 0;
    }

    public String getNombre() { return this.nombre; }

    public int getIdJugador() { return this.idJugador; }

    public int getNumeroGolpes() { return this.numeroGolpes; }

    public int getPuntos() { return this.puntos; }

    public void resetGolpes() { this.numeroGolpes = 0; }
    public void jugadorGolpea(){ this.numeroGolpes++; }
    public void jugadorPuntua(){ this.puntos++; }

    @Override
    public String toString() {
        return  "\nNombre:\t" + this.nombre +
                "\nPuntos:\t" + this.puntos + "\n";
    }
}

class HiloJugador implements Runnable{
    private Jugador jugador;
    private Bola bola;
    private Partida partida;
    private boolean turno;
    public HiloJugador(Jugador jugador, Bola bola, Partida partida, boolean turno){
        this.jugador = jugador;
        this.bola = bola;
        this.partida = partida;
        this.turno = turno;
    }

    public boolean posibleGanador(){
        if(new Random().nextBoolean()){
            if(this.jugador.getNumeroGolpes() > 0){
                int ganador = new Random().nextInt(10);

                if(ganador %2 == 0){
                    if(this.turno){
                        this.partida.terminarPeloteo(this.jugador);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void run(){
        try{
            while(!partida.isPartidaTerminada()){
                do{
                    if(!this.partida.isPeloteoTerminado()){
                        if(this.partida.isPartidaTerminada()){

                        }else{
                            if(this.turno){
                                bola.golpearBola(this.jugador, posibleGanador());
                            }else{
                                bola.esperarBola(this.jugador);
                            }
                            this.turno = !this.turno;
                        }
                    }
                }while(!this.partida.isPeloteoTerminado());
                this.partida.nuevoPeloteo();

            }

        }catch(InterruptedException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}