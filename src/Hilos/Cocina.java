package Hilos;

import java.util.Random;
import java.util.Scanner;

public class Cocina {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        boolean seRellena = false;
        char opcion;

        do{
            System.out.print("¿Quieres contratar a alguien para que rellene los cubos?\nY/N: ");
            opcion = sc.nextLine().charAt(0);
            if(opcion != 'Y' && opcion != 'N'){
                System.out.println("Opción no válida");
            }else{
                if(opcion == 'Y'){
                    seRellena = true;
                }else{
                    seRellena = false;
                }
            }
        }while(opcion != 'Y' && opcion != 'N');

        CuboAgua aguaFria = new CuboAgua("Fría", 60000);
        CuboAgua aguaCaliente = new CuboAgua("Caliente", 30000);

        Cocinero cocinero_1 = new Cocinero("Ruben", false);
        Cocinero cocinero_2 = new Cocinero("Javi", false);
        Cocinero cocinero_3 = new Cocinero("Alba", false);
        Cocinero cocinero_4 = new Cocinero("Paula", false);
        Cocinero cocinero_5 = new Cocinero("Andres", false);
        Cocinero cocinero_6 = new Cocinero("Anabel", false);
        Cocinero cocinero_7 = new Cocinero("Anelsy", true);

        Thread hiloCocinero_1 = new Thread(new HiloCocinero(cocinero_1, aguaFria, aguaCaliente, seRellena, null, null));
        Thread hiloCocinero_2 = new Thread(new HiloCocinero(cocinero_2, aguaFria, aguaCaliente, seRellena, null, null));
        Thread hiloCocinero_3 = new Thread(new HiloCocinero(cocinero_3, aguaFria, aguaCaliente, seRellena, null, null));
        Thread hiloCocinero_4 = new Thread(new HiloCocinero(cocinero_4, aguaFria, aguaCaliente, seRellena, null, null));
        Thread hiloCocinero_5 = new Thread(new HiloCocinero(cocinero_5, aguaFria, aguaCaliente, seRellena, null, null));
        Thread hiloCocinero_6 = new Thread(new HiloCocinero(cocinero_6, aguaFria, aguaCaliente, seRellena, null, null));

        HiloCocineroRellena menteFria = new HiloCocineroRellena(aguaFria, cocinero_7);
        HiloCocineroRellena menteCaliente = new HiloCocineroRellena(aguaCaliente, cocinero_7);
        Thread hiloCocinero_7 = new Thread(new HiloCocinero(cocinero_7, aguaFria, aguaCaliente, seRellena, menteFria, menteCaliente));

        System.out.println("-----\tCUBO DE AGUA FRÍA\t\t-\t" + aguaFria.getAguaActual()/100f + "\t-----" +
                "\n-----\tCUBO DE AGUA CALIENTE\t-\t" + aguaCaliente.getAguaActual()/100f + "\t-----");

        hiloCocinero_7.setPriority(2);

        hiloCocinero_1.start();
        hiloCocinero_2.start();
        hiloCocinero_3.start();
        hiloCocinero_4.start();
        hiloCocinero_5.start();
        hiloCocinero_6.start();
        if(seRellena){
            hiloCocinero_7.start();
        }

        hiloCocinero_1.join();
        hiloCocinero_2.join();
        hiloCocinero_3.join();
        hiloCocinero_4.join();
        hiloCocinero_5.join();
        hiloCocinero_6.join();
        if(seRellena){
            hiloCocinero_7.join();
        }
    }
}

class CuboAgua{
    private String temperatura;
    private int capacidadTotal;
    private int aguaActual;
    private boolean estaVacio;

    public CuboAgua(String temperatura, int capacidadTotal){
        this.temperatura = temperatura;
        this.capacidadTotal = capacidadTotal;
        this.aguaActual = this.capacidadTotal;
        this.estaVacio = false;
    }
    
    public synchronized int getAguaActual() { return this.aguaActual; }

    public int getCapacidadTotal() { return this.capacidadTotal; }

    public String getTemperatura() { return this.temperatura; }

    public synchronized int interactuarCubo(int aguaNecesaria, Cocinero cocinero, boolean alguienRellena) throws InterruptedException {
        if(!alguienRellena){
            if(!isEmpty()){
                if(this.aguaActual < aguaNecesaria){
                    int aux = aguaNecesaria - this.aguaActual;
                    aguaNecesaria -= aux;
                    System.out.println("No habia suficiente agua para realizar por completo el plato");
                }

                this.aguaActual -= aguaNecesaria;

                System.out.println("Plato realizado por " + cocinero.getNombre() + " con " + aguaNecesaria/100f + " litros de agua " + this.temperatura.toLowerCase());

                if (this.temperatura.equals("Caliente")){
                    System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t-\t" + getAguaActual()/100f + "\t-----\n");
                }else{
                    System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t\t-\t" + getAguaActual()/100f + "\t-----\n");
                }

                return aguaNecesaria;
            }else{
                return 0;
            }
        }else{
            if(cocinero.getRellena()){
                meterAgua(cocinero);

                return 1;
            }else{
                while(isEmpty() || this.aguaActual < aguaNecesaria){
                    System.out.println(cocinero.getNombre() + " esta esperando a que haya suficiente agua " + this.temperatura.toLowerCase());
                    wait();
                }

                this.aguaActual -= aguaNecesaria;

                System.out.println("Plato realizado por " + cocinero.getNombre() + " con " + aguaNecesaria/100f + " litros de agua " + this.temperatura.toLowerCase());

                if (this.temperatura.equals("Caliente")){
                    System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t-\t" + getAguaActual()/100f + "\t-----\n");
                }else{
                    System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t\t-\t" + getAguaActual()/100f + "\t-----\n");
                }

                notify();
                return aguaNecesaria;
            }
        }
    }

    public synchronized void meterAgua(Cocinero cocinero) throws InterruptedException {
        while (this.aguaActual >= this.capacidadTotal || (this.aguaActual + 5000) >= this.capacidadTotal){
            System.out.println("El cubo de agua " + this.temperatura.toLowerCase() + " esta lleno y " + cocinero.getNombre() + " no puede rellenarlo");
            wait();
        }

        this.aguaActual += 5000;

        System.out.println("El cocinero " + cocinero.getNombre() + " ha rellenado con 50 litros el cubo de agua " + this.temperatura.toLowerCase());

        if (this.temperatura.equals("Caliente")){
            System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t-\t" + getAguaActual()/100f + "\t-----\n");
        }else{
            System.out.println("-----\tCUBO DE AGUA " + temperatura.toUpperCase() + "\t\t-\t" + getAguaActual()/100f + "\t-----\n");
        }

        notifyAll();
    }
    
    public synchronized boolean isEmpty(){
        if(this.aguaActual <= 0){
            if(!this.estaVacio){
                System.out.println("-----\tCUBO DE AGUA " + this.temperatura.toUpperCase() + " VACÍO");
                this.estaVacio = true;
            }
            return true;
        }else{
            return false;
        }
    }
}

class Cocinero{
    private String nombre;
    private int aguaFriaUtilizada;
    private int aguaCalienteUtilizada;
    private int numeroPlatos;
    private int numeroRellenos;
    private boolean rellena;
    
    public Cocinero(String nombre, boolean rellena){
        this.nombre = nombre;
        this.aguaFriaUtilizada = 0;
        this.aguaCalienteUtilizada = 0;
        this.numeroPlatos = 0;
        this.numeroRellenos = 0;
        this.rellena = rellena;
    }

    public String getNombre() { return this.nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAguaFriaUtilizada() { return this.aguaFriaUtilizada; }

    public void setAguaFriaUtilizada(int aguaFria) {
        this.aguaFriaUtilizada += aguaFria;
        platoHecho();
    }

    public int getCalienteFriaUtilizada() { return this.aguaCalienteUtilizada; }

    public void setAguaCalienteUtilizada(int aguaCaliente) {
        this.aguaCalienteUtilizada += aguaCaliente;
        platoHecho();
    }
    
    public int getNumeroPlatos() { return this.numeroPlatos; }
    
    public void platoHecho(){ this.numeroPlatos++; }

    public void setNumeroRellenos(int relleno){ this.numeroRellenos += relleno; }

    public boolean getRellena(){ return this.rellena; }

    public String toString(){
        return "\nCocinero " + this.nombre +
                "\nTotal de platos realizados: " + this.numeroPlatos +
                "\nAgua fría utilizada: " + this.aguaFriaUtilizada/100f + " litros" +
                "\nAgua caliente utilizada: " + this.aguaCalienteUtilizada/100f + " litros";
    }
}

class HiloCocinero implements Runnable{
    private Cocinero cocinero;
    private CuboAgua cuboAguaFria;
    private CuboAgua cuboAguaCaliente;
    private boolean alguienRellena;

    private HiloCocineroRellena menteFria;
    private HiloCocineroRellena menteCaliente;

    public HiloCocinero(Cocinero cocinero, CuboAgua cuboAguaFria, CuboAgua cuboAguaCaliente, boolean alguienRellena, HiloCocineroRellena menteFria, HiloCocineroRellena menteCaliente){
        this.cocinero = cocinero;
        this.cuboAguaFria = cuboAguaFria;
        this.cuboAguaCaliente = cuboAguaCaliente;
        this.alguienRellena = alguienRellena;
        this.menteFria = menteFria;
        this.menteCaliente = menteCaliente;
    }
    
    @Override
    public void run() {
        if(this.cocinero.getRellena()){
            System.out.println("Soy el cocinero " + this.cocinero.getNombre() + " y voy a encargarme de rellenar los cubos de agua");

            Thread HilomenteFria = new Thread(this.menteFria);
            Thread HilomenteCaliente = new Thread(this.menteCaliente);

            HilomenteFria.start();
            HilomenteCaliente.start();

            try {
                HilomenteFria.join();
                HilomenteCaliente.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }else{
            System.out.println("Soy el cocinero " + this.cocinero.getNombre() + " y voy a comenzar a preparar platos");

            int aguaNecesaria = 0;
            boolean necesitaFria;

            while(!cuboAguaFria.isEmpty() || !cuboAguaCaliente.isEmpty()){

                if(cuboAguaCaliente.isEmpty()){
                    necesitaFria = true;
                }else if (cuboAguaFria.isEmpty()){
                    necesitaFria = false;
                }else{
                    necesitaFria = new Random().nextBoolean();
                }
                aguaNecesaria = new Random().nextInt(25, 2001);
                int aguaUtilizada = 0;

                if(necesitaFria){
                    try {
                        aguaUtilizada = cuboAguaFria.interactuarCubo(aguaNecesaria, this.cocinero, alguienRellena);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(aguaUtilizada != 0){
                        this.cocinero.setAguaFriaUtilizada(aguaUtilizada);
                    }else{
                        System.out.println("El plato que iba a realizar " + this.cocinero.getNombre() + " no es posible ya que el cubo de agua fría esta vacío");
                    }
                }else{
                    try {
                        aguaUtilizada = cuboAguaCaliente.interactuarCubo(aguaNecesaria, this.cocinero, alguienRellena);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(aguaUtilizada != 0){
                        this.cocinero.setAguaCalienteUtilizada(aguaUtilizada);
                    }else{
                        System.out.println("El plato que iba a realizar " + this.cocinero.getNombre() + " no es posible ya que el cubo de agua caliente esta vacío");
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println(this.cocinero.toString());
    }
}

class HiloCocineroRellena implements Runnable {
    private CuboAgua cubo;
    private Cocinero cocinero;

    public HiloCocineroRellena(CuboAgua cubo, Cocinero cocinero) {
        this.cubo = cubo;
        this.cocinero = cocinero;
    }

    @Override
    public void run() {
        try {
            if (cubo.getTemperatura().equals("Fría")) {
                this.cocinero.setNumeroRellenos(cubo.interactuarCubo(0, this.cocinero, true));

                while (true) {
                    Thread.sleep(4000);
                    this.cocinero.setNumeroRellenos(cubo.interactuarCubo(0, this.cocinero, true));
                }
            } else {
                this.cocinero.setNumeroRellenos(cubo.interactuarCubo(0, this.cocinero, true));

                while (true) {
                    Thread.sleep(4000);
                    this.cocinero.setNumeroRellenos(cubo.interactuarCubo(0, this.cocinero, true));
                }
            }

        } catch (InterruptedException e) {
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}