import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class EchoServerSaludo {
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

        try (DatagramSocket socket = new DatagramSocket(numPuertoServidor)){
            while(true){
                System.out.println("Esperando algún datagrama");
                byte[] bufferEntrada = new byte[MAXBYTES];
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteUdp);
                String lineaRecibida = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(),CODTEXTO);
                ipCliente = paqueteUdp.getAddress();
                numPuertoCliente = paqueteUdp.getPort();

                String respuesta = "";
                if(lineaRecibida.startsWith("Hola, soy ") && lineaRecibida.endsWith(" y quiero saludarle")){
                    String lineaAux = lineaRecibida.substring(9);
                    String[] cadenas = lineaAux.split(" ");
                    respuesta = "Muchas gracias " + cadenas[1] + ", yo también le saludo a usted";
                }else{
                    respuesta = "#"+lineaRecibida+"#";
                }

                byte []bufferSalida = new byte[MAXBYTES]; 
                bufferSalida = respuesta.getBytes();
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, numPuertoCliente);

                socket.send(paqueteUdp);
            }
        }
        catch(SocketException e){
            System.out.println("Error en el socket servidor");
        }catch(IOException e){
            System.out.println("Error en E/S");
        }
    }
}
