package Hilos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static Hilos.SupermercadoMain.euros;

public class SupermercadoMain {
    public static DecimalFormat euros = new DecimalFormat("#.##");
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int numeroClientes = 0;

        do{
            System.out.print("¿Cuantos clientes quieres que entren a la tienda?\n" +
                    "\tNúmero de Clientes: ");
            numeroClientes = sc.nextInt();

        }while(numeroClientes <= 0);
        Thread[] hilosClientes = new Thread[30];
        List<Caja> listaCajas = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            listaCajas.add(new Caja(i));
        }
        Supermercado supermercado = new Supermercado(listaCajas);

        for (int i = 0; i < 30; i++) {
            Cliente cliente = new Cliente("Cliente " + (i + 1));
            hilosClientes[i] = new Thread(new SimulacionClientes(supermercado, cliente));
            hilosClientes[i].start();
        }

        for (int i = 0; i < 30; i++) {
            hilosClientes[i].join();
        }

        System.out.println(supermercado.toString());

    }

    public void menu(){
        System.out.print("---------- BIENVENIDO AL SIMULADOR DE SUPERMERCADO ----------\n" +
                "1- Comienza la simulación\n" +
                "2- Define cuantos clientes quieres que entren\n" +
                "3- Define cuantas cajas quieres usar\n" +
                "4- Define cuantos clientes caben en cada caja\n" +
                "5- Salir Del Programa\n\n" +
                "Opción: ");
    }
}

class Supermercado {
    private List<Caja> listaCajas;
    private Semaphore cajas = new Semaphore(1);

    public Supermercado(List<Caja> listaCajas){
        this.listaCajas = listaCajas;
    }

    public void elegirCaja(Cliente cliente) throws InterruptedException {
        int cajaMenosCola = 0;
        do{
            for(int i = 0; i < this.listaCajas.size(); i++){
                if(listaCajas.get(cajaMenosCola).getCaja().availablePermits() < listaCajas.get(i).getCaja().availablePermits()){
                    cajaMenosCola = i;
                }
            }
            if(cajaMenosCola == 0 && listaCajas.get(0).getCaja().availablePermits() == 0){
                if(cajas.availablePermits() == 0){
                    System.out.println("Todas las cajas llenas, " + cliente.getNombre() + " esperando...");
                }
                cajas.acquire();
            }
        }while(cajaMenosCola == 0 && listaCajas.get(0).getCaja().availablePermits() == 0);

        listaCajas.get(cajaMenosCola).cobrar(cliente, cajas);
    }

    @Override
    public String toString() {
        String infoCaja = "";

        for(int i = 0; i < this.listaCajas.size(); i++){
            Caja caja = listaCajas.get(i);
            infoCaja += ("Caja " + caja.getIdCaja() + ":" +
                    "\n\tClientes pasados por caja: " + caja.getClientesPasados() +
                    "\n\tTotal Cobrado: " + euros.format(caja.getTotalCobrado()) + "€\n");
        }

        return "Supermercado cerrado:\n" + infoCaja;
    }
}

class Caja{
    private int idCaja;
    private int clientesPasados;
    private float totalCobrado;
    private Semaphore caja = new Semaphore(3);

    public Caja(int idCaja){
        this.idCaja = idCaja;
        this.clientesPasados = 0;
        this.totalCobrado = 0;
    }

    public void cobrar(Cliente cliente, Semaphore cajas) throws InterruptedException {
        caja.acquire();
        this.clientesPasados++;
        System.out.println(cliente.getNombre() + " está siendo atendido en la caja " + this.idCaja);
        long tiempoCobro = new Random().nextLong(3000, 5001);
        Thread.sleep(tiempoCobro);

        this.totalCobrado += cliente.getTotalCompra();
        System.out.println("\n" + cliente.getNombre() + " ha terminado en la caja " + this.idCaja +
                "\nEsta caja lleva por ahora " + euros.format(this.totalCobrado) + "€");
        System.out.println("----------------------------------------\n");
        caja.release();
        cajas.release();
    }

    public int getIdCaja() { return this.idCaja; }

    public int getClientesPasados() { return this.clientesPasados; }

    public float getTotalCobrado() { return this.totalCobrado; }

    public Semaphore getCaja() { return this.caja; }
}

class Cliente{
    private String nombre;
    private float totalCompra;

    public Cliente(String nombre){
        this.nombre = nombre;
        this.totalCompra = new Random().nextFloat(0.01f, 101.0f);
    }

    public String getNombre() { return this.nombre; }

    public float getTotalCompra() { return this.totalCompra; }
}

class SimulacionClientes implements Runnable {
    private Supermercado supermercado;
    private Cliente cliente;
    private long tiempoCompra;

    public SimulacionClientes(Supermercado supermercado, Cliente cliente) {
        this.supermercado = supermercado;
        this.cliente = cliente;
        this.tiempoCompra = new Random().nextLong(4000, 8001);
    }

    @Override
    public void run() {
        try {
            System.out.println("El " + this.cliente.getNombre() + " esta comprando");
            Thread.sleep(tiempoCompra);
            System.out.println("\n----------------------------------------\n" +
                    "El " + this.cliente.getNombre() + " terminó su compra y se dirige a pagar");
            supermercado.elegirCaja(this.cliente);
        } catch (InterruptedException e) {
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}
