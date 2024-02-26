import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteDatagrama {
    static int MAXBYTES = 1400;
    static String CODTEXTO = "UTF-8";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int puertoServidor;
        String host;
        DatagramPacket paqueteUdp;
        InetAddress ipServidor;

        if(args.length < 2){
            System.out.println("Error al pasar los argumentos");
            System.exit(1);
        }

        host = args[0];
        puertoServidor = Integer.parseInt(args[1]);

        try(DatagramSocket socketCliente = new DatagramSocket()){
            while (true){
                //Enviar Datos Al Servidor
                String linea = sc.nextLine();
                byte[] bufferSalida = new byte[MAXBYTES];
                bufferSalida = linea.getBytes();
                ipServidor = InetAddress.getByName(host);
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipServidor, puertoServidor);
                socketCliente.send(paqueteUdp);

                //Recibir Datos Del Servidor
                String respuesta;
                byte[] bufferEntrada = new byte[MAXBYTES];
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socketCliente.receive(paqueteUdp);
                respuesta = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), CODTEXTO);
                System.out.println(respuesta);
            }
        }catch (IOException e){

        }
    }
}
