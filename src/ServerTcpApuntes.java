import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerTcpApuntes {
    public static void main(String[] args) {
        int numServidor;
        ServerSocket servidor;
        Socket conexion;

        if(args.length < 1){
            System.err.println("Debes determinar un número para crear el servidor");
            System.exit(1);
        }

        numServidor = Integer.parseInt(args[0]);
        try{
            System.out.println("Creamos el servidor con el numero " + numServidor);
            servidor = new ServerSocket(numServidor);
            System.out.println("Aceptamos la conexion y esperamos al cliente");

            while (true){
                conexion = servidor.accept();

                HiloServidor hiloServidor = new HiloServidor(conexion);
                hiloServidor.start();
            }

        }catch (IOException e){
            System.err.println("Error de algún tipo");
        }
    }
}

class HiloServidor extends Thread{
    private Socket conexion;
    public HiloServidor(Socket conexion){
        this.conexion = conexion;
    }
    @Override
    public void run() {
        InetAddress ipCliente = conexion.getInetAddress();
        try{
            System.out.println("Hilo " + this.getName() + " comienza");
            Scanner scCliente = new Scanner(conexion.getInputStream());
            PrintWriter pW = new PrintWriter(conexion.getOutputStream());

            String lineaRecibida;
            while ((lineaRecibida = scCliente.nextLine()) != null && !lineaRecibida.isEmpty()){
                System.out.println("Esta es la linea recibida del cliente con IP " + ipCliente + " :\n" + lineaRecibida);

                pW.println(lineaRecibida);
                pW.flush();
            }
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException e){
            System.out.println("El cliente con la IP " + ipCliente + " ha cerrado su conexion...");
        }
    }
}
