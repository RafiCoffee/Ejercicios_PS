package Hilos;

public class BloqueoCuentasBancarias {
    public static void main(String[] args){
        Cuenta c1 = new Cuenta("Cuenta 1", 20000);
        Cuenta c2 = new Cuenta("Cuenta 2", 40000);
        Thread h1 = new Thread(new HiloTransferencia("H1", c1, c2));
        Thread h2 = new Thread(new HiloTransferencia("H2", c1, c2));

        System.out.println("Se realizaran transferencias entre estas dos cuentas\n" + c1 + "\n" + c2);

        h1.start();
        h2.start();

        try{
            h1.join();
            h2.join();
            System.out.println("Transferencias finalizadas");
            System.out.println(c1 + "\n" + c2);
        }catch (InterruptedException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}

class Cuenta {
    private String numeroCuenta;
    private double saldo;

    public Cuenta(String numeroCuenta, double saldo){
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
    }

    synchronized public double getSaldo(){ return this.saldo; }

    synchronized public void ingresaCantidad(double cantidad){ this.saldo += cantidad; }

    synchronized public void sacaCantidad(double cantidad){ this.saldo -= cantidad; }

    public String getNumeroCuenta(){ return this.numeroCuenta; }

    @Override
    public String toString() {
        return "\nCuenta:\n" +
                "Numero De Cuenta: " + this.numeroCuenta + "\n" +
                "Saldo: " + this.saldo + "\n";
    }
}

class Transferencia {
    private Cuenta c1;
    private Cuenta c2;
    Transferencia(Cuenta c1, Cuenta c2){
        this.c1 = c1;
        this.c2 = c2;
    }

    public boolean TransferenciaRealizada(double cantidad){
        synchronized (this.c1){
            synchronized (this.c2){
                if(this.c1.getSaldo() >= cantidad && cantidad > 0){
                    this.c1.sacaCantidad(cantidad);
                    this.c2.ingresaCantidad(cantidad);
                    return true;
                }else{
                    return false;
                }
            }
        }
    }
}

class HiloTransferencia implements Runnable {
    private String nombreHilo;
    private Cuenta c1;
    private Cuenta c2;
    private Transferencia t;

    HiloTransferencia(String nombreHilo, Cuenta c2, Cuenta c1){
        this.nombreHilo = nombreHilo;
        this.c1 = c1;
        this.c2 = c2;
        this.t = new Transferencia(this.c1, this.c2);
    }
    @Override
    public void run() {
        int dineroTranferir = 10;
        int contadorTransferencias = 0;

        for(int i = 0; i < 1000; i++){
            if(t.TransferenciaRealizada(dineroTranferir)){
                contadorTransferencias++;
            }else{
                System.err.println("Transferencia denegada por el " + this.nombreHilo +
                        " debido a falta de saldo");
                break;
            }
        }

        synchronized (System.out){
            System.out.println("Fin de transferencia de la cuenta " + this.c1.getNumeroCuenta() +
                    " a la cuenta " + this.c2.getNumeroCuenta() +
                    "\nRealizada por el hilo " + this.nombreHilo +
                    " (Número de transferencias realizadas: " + contadorTransferencias + ")");
        }
    }
}