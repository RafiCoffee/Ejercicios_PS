import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerTcpChat {
    public static void main(String[] args) {
        int numPuertoServidor;
        ServerSocket servidor;
        Socket conexion;

        if(args.length < 1){
            System.err.println("Debes determinar un número para crear el servidor");
            System.exit(1);
        }

        numPuertoServidor = Integer.parseInt(args[0]);
        try{
            System.out.println("Creamos el servidor con el numero " + numPuertoServidor);
            servidor = new ServerSocket(numPuertoServidor);
            System.out.println("Aceptamos la conexion y esperamos a los clientes");

            while (true){
                conexion = servidor.accept();

                ServidorHilo hiloServidor = new ServidorHilo(conexion);
                hiloServidor.start();
                recibirRespuestasClientes respuestasCliente = new recibirRespuestasClientes(conexion);
                respuestasCliente.start();
            }

        }catch (IOException e){
            System.err.println("Error de algún tipo");
        }
    }
}

class ServidorHilo extends Thread{
    private Socket conexion;
    public ServidorHilo(Socket conexion){
        this.conexion = conexion;
    }
    @Override
    public void run() {
        InetAddress ipCliente = conexion.getInetAddress();
        try{
            Scanner scServer = new Scanner(System.in);
            PrintWriter pW = new PrintWriter(conexion.getOutputStream());

            System.out.println("El cliente con IP " + ipCliente + " se ha conectado");

            while (true){
                String respuesta;
                respuesta = scServer.nextLine();

                pW.println("\t" + respuesta);
                pW.flush();
            }
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException e){
            System.out.println("El cliente con la IP " + ipCliente + " ha cerrado su conexion...");
        }
    }
}

class recibirRespuestasClientes extends Thread{
    private Socket conexion;
    public recibirRespuestasClientes(Socket conexion){
        this.conexion = conexion;
    }
    @Override
    public void run() {
        try{
            Scanner scCliente = new Scanner(conexion.getInputStream());
            PrintWriter pW = new PrintWriter(conexion.getOutputStream());

            String lineaRecibida;
            while((lineaRecibida = scCliente.nextLine()) != null && !lineaRecibida.isEmpty()){
                System.out.println(lineaRecibida);

                pW.println(conexion.getInetAddress() + ": " + lineaRecibida);
                pW.flush();
            }
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException e){
            System.out.println("El cliente dejó la conversacion");
        }
    }
}
