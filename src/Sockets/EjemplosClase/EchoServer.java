package Sockets.EjemplosClase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class EchoServer {
    private static final int MAXBYTES=1400;
    private static final String CODTEXTO="UTF-8";
    public static void main(String[] args)  {
        int numPuertoServidor, numPuertoCliente;
        DatagramPacket paqueteUdp;
        InetAddress ipCliente;
        
        

        if (args.length < 1){
            System.out.println("Error, debes pasar el puerto");
            System.exit(1);
        }

        numPuertoServidor = Integer.parseInt(args[0]);
        
        //creamos el socket asociado al puerto. Se pone a la escucha.
        try (DatagramSocket socket = new DatagramSocket(numPuertoServidor)){
            while(true){
                System.out.println("Esperando algún datagrama");
                byte[] bufferEntrada = new byte[MAXBYTES];  //creamos el buffer
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteUdp); //recivimos un datagrama paqueteUdp
                //SACAMOS LOS DATOS DEL PAQUETE, DESPLAZAMIENTO 0, TAMAÑO A LEER Y CODIFICACIÓN
                String lineaRecibida = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(),CODTEXTO);
                ipCliente = paqueteUdp.getAddress(); //sacamos la ip del cliente.
                numPuertoCliente = paqueteUdp.getPort();//sacamos el puerto origen del cliente
                String lineaReplicar="#"+lineaRecibida+"#";  //modicamos la respuesta con dos #

                //ahora debemos mandar la respuesta en un nuevo datagrama al Cliente
                //preparamos otro buffer para enviar, 
                //podría el mismo por el que hemos recibido, pero utilizamos otro.
                byte []bufferSalida = new byte[MAXBYTES]; 
                bufferSalida = lineaReplicar.getBytes();  //ponemos en el buffer el mensaje a mandar
                //Creamos el datagrama con los datos, la longitud, la ip del cliente y su puerto
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, numPuertoCliente);
                //Ahora debemos enviarlo a nuestro socket.
                socket.send(paqueteUdp);
            } //fin del while
        }//fin del try
        catch(SocketException e){
            System.out.println("Error en el socket servidor");
        }catch(IOException e){
            System.out.println("Error en E/S");
        }
    }
}
