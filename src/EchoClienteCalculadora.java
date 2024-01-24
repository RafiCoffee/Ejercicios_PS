import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EchoClienteCalculadora {
    private static final int MAXBYTES=1400;
    private static final String CODTEXTO = "UTF-8";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        if(args.length < 2){
            System.err.println("Error, debes pasar un host y un puerto");
            System.exit(1);
        }

        byte[] buffer = new byte[MAXBYTES];

        String hostServidor = args[0];
        int puertoServidor = Integer.parseInt(args[1]);

        try (DatagramSocket socketCliente = new DatagramSocket()){
            while (true){
                System.out.print("Introduce una operación con el siguiente patron.\t(x +/-/*/: y)" +
                        "\n----> ");
                String linea = sc.nextLine();
                buffer = linea.getBytes();
                InetAddress ipServidor = InetAddress.getByName(hostServidor);
                DatagramPacket datagramaUdp = new DatagramPacket(buffer, buffer.length, ipServidor, puertoServidor);
                socketCliente.connect(ipServidor, puertoServidor);
                socketCliente.send(datagramaUdp);

                byte[] bufferRespuesta = new byte[MAXBYTES];
                System.out.println("ESperando respuesta...");

                datagramaUdp = new DatagramPacket(bufferRespuesta,bufferRespuesta.length, ipServidor, puertoServidor);
                socketCliente.receive(datagramaUdp);

                String respuestaServidor = new String(datagramaUdp.getData(), 0, datagramaUdp.getLength());
                System.out.println("Respuesta Del Servidor:" +
                        "\n----> " + respuestaServidor);
            }
        }catch (SocketException e){
            System.out.println("Problemas con los socket");
        }catch (IOException e){
            System.out.println("Problemas con las IO");
        }
    }
}
