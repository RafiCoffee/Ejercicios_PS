import java.io.IOException;
import java.io.PrintWriter;
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
            conexionServidor = new Socket(host, numServidor);

            System.out.println("Cliente conectado al servidor " + numServidor);
            Scanner scCliente = new Scanner(System.in);
            Scanner scServidor = new Scanner(conexionServidor.getInputStream());
            PrintWriter pW = new PrintWriter(conexionServidor.getOutputStream());

            String lineaEnviada;
            System.out.print("Comunicate con el servidor ==> ");
            while ((lineaEnviada = scCliente.nextLine()) != null && !lineaEnviada.isEmpty()){
                pW.println(lineaEnviada);
                pW.flush();

                String lineaDevuelta;
                lineaDevuelta = scServidor.nextLine();

                System.out.println("El servidor te devuelve --> " + lineaDevuelta);
                System.out.print("Comunicate con el servidor ==> ");
            }
        }catch (IOException e){
            System.err.println("Error de algún tipo");
        }catch (NoSuchElementException ignored){
            System.out.println("Saliendo del servidor con numero " + numServidor);
        }
    }
}
