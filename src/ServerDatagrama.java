import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerDatagrama {
    static int MAXBYTES = 1400;
    static String CODTEXTO = "UTF-8";

    public static void main(String[] args) {
        int puertoServer, puertoCliente;
        DatagramPacket paqueteUdp;
        InetAddress ipCliente;

        if(args.length < 1){
            System.out.println("Error al pasar los argumentos");
            System.exit(1);
        }

        puertoServer = Integer.parseInt(args[0]);
        int c = 1;

        try(DatagramSocket socketServidor = new DatagramSocket(puertoServer)){
            while (true){
                //Recibir Datos
                byte[] bufferEntrada = new byte[MAXBYTES];
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socketServidor.receive(paqueteUdp);
                String recibir = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), CODTEXTO);
                //Cuando lo recibo retengo la ip del Cliente y su puerto
                ipCliente = paqueteUdp.getAddress();
                puertoCliente = paqueteUdp.getPort();

                //Hacer algo con los datos
                System.out.println(recibir);
                recibir += "\nMensaje " + c;
                c++;

                //Enviar Datos
                byte[] bufferSalida = new byte[MAXBYTES];
                bufferSalida = recibir.getBytes();
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, puertoCliente);
                socketServidor.send(paqueteUdp);
            }
        }catch (IOException e){

        }
    }
}
