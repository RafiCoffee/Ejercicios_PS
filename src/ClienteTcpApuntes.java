import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClienteTcpApuntes {
    public static void main(String[] args) {
        int numServidor;
        String host;
        Socket conexionServidor;

        if(args.length < 2){
            System.err.println("Debes determinar un número y un host para conectarte al servidor");
            System.exit(1);
        }

        numServidor = Integer.parseInt(args[0]);
        host = args[1];
        try{
            System.out.println("Conectando con el servidor con el puerto " + numServidor);
            conexionServidor = new Socket(host, numServidor);
            recibirRespuestasServidor respuestasServidor = new recibirRespuestasServidor(conexionServidor);
            respuestasServidor.start();

            System.out.println("Cliente conectado al servidor " + numServidor);
            Scanner scCliente = new Scanner(System.in);
            PrintWriter pW = new PrintWriter(conexionServidor.getOutputStream());

            System.out.println("Comienza la conversacion");
            while (true){
                String lineaEnviada;
                lineaEnviada = scCliente.nextLine();

                pW.println(lineaEnviada);
                pW.flush();
            }
        } catch (ConnectException e){
            System.err.println("El servidor se encuentra cerrado");
        } catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException ignored){
            System.out.println("Saliendo del servidor con numero " + numServidor);
        }
    }
}
class recibirRespuestasServidor extends Thread{
    private Socket conexion;
    public recibirRespuestasServidor(Socket conexion){
        this.conexion = conexion;
    }
    @Override
    public void run() {
        try{
            Scanner scServidor = new Scanner(conexion.getInputStream());

            String lineaRecibida;
            while ((lineaRecibida = scServidor.nextLine()) != null && !lineaRecibida.isEmpty()){
                System.out.println(lineaRecibida);
            }
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }
    }
}