import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerTcpChat {
    public static void main(String[] args) {
        List<Socket> clientes = new ArrayList<>();
        int numPuertoServidor;
        ServerSocket servidor;
        Socket conexion;

        if(args.length < 1){
            System.err.println("Debes determinar un numero para crear el servidor");
            System.exit(1);
        }

        numPuertoServidor = Integer.parseInt(args[0]);
        try{
            System.out.println("Creamos el servidor con el numero " + numPuertoServidor);
            servidor = new ServerSocket(numPuertoServidor);
            System.out.println("Aceptamos la conexion y esperamos a los clientes");

            while (true){
                conexion = servidor.accept();

                EnviarMensajes enviarMensajes = new EnviarMensajes(clientes);
                enviarMensajes.start();

                RecibirMensajes recibirMensajes = new RecibirMensajes(conexion, clientes);
                recibirMensajes.start();

                clientes.add(conexion);
            }

        }catch (IOException e){
            System.err.println("Error de algún tipo");
        }
    }

    static class RecibirMensajes extends Thread{
        private Socket conexion;
        List<Socket> clientes;
        public RecibirMensajes(Socket conexion, List<Socket> clientes){
            this.conexion = conexion;
            this.clientes = clientes;
        }
        @Override
        public void run() {
            InetAddress ipCliente = conexion.getInetAddress();
            try{
                Scanner scCliente = new Scanner(conexion.getInputStream());

                System.out.println("----- El cliente con IP " + ipCliente + " se ha conectado -----");

                while(true){
                    String lineaRecibida = scCliente.nextLine();
                    System.out.println("Mensaje del cliente con IP " + ipCliente + ": " + lineaRecibida);

                    synchronized (clientes){
                        for(Socket conexionesClientes : clientes){
                            if(conexion != conexionesClientes){
                                PrintWriter pW = new PrintWriter(conexionesClientes.getOutputStream(), true);
                                pW.println(ipCliente + ": " + lineaRecibida);
                            }
                        }
                    }
                }
            }catch (IOException e){
                System.err.println("Error de algun tipo");
            }catch (NoSuchElementException e){
                System.out.println("----- El cliente con IP " + ipCliente + " dejó la conversacion -----");
                clientes.remove(conexion);
            }
        }
    }

    static class EnviarMensajes extends Thread{
        List<Socket> clientes;
        public EnviarMensajes(List<Socket> clientes){
            this.clientes = clientes;
        }
        @Override
        public void run() {
            try{
                Scanner scServer = new Scanner(System.in);

                while (true){
                    String mensaje = scServer.nextLine();

                    synchronized (clientes){
                        for(Socket conexionesClientes : clientes){
                            PrintWriter pW = new PrintWriter(conexionesClientes.getOutputStream(), true);
                            pW.println("Servidor: " + mensaje);
                        }
                    }
                }
            }catch (IOException e){
                System.err.println("Error de algun tipo");
            }catch (NoSuchElementException e){

            }
        }
    }
}
