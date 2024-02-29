import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClienteTftp {
    static int MAXBYTES = 500;
    static String CODTEXTO = "UTF-8";

    public static void main(String[] args) throws UnknownHostException {
        Scanner sc = new Scanner(System.in);
        int puertoServidor;
        String host;
        DatagramPacket paqueteUdp;
        InetAddress ipServidor;

        if(args.length < 2){
            System.err.println("Error al pasar los argumentos");
            System.exit(1);
        }

        host = args[0];
        puertoServidor = Integer.parseInt(args[1]);
        InetAddress miIp = InetAddress.getByName(host);

        try(DatagramSocket socketCliente = new DatagramSocket()){
            String miIpString = miIp.toString().substring(miIp.toString().indexOf('/') + 1);
            byte[] bufferInicioConexion = new byte[MAXBYTES];
            bufferInicioConexion = miIpString.getBytes();
            ipServidor = InetAddress.getByName(host);
            paqueteUdp = new DatagramPacket(bufferInicioConexion, bufferInicioConexion.length, ipServidor, puertoServidor);
            socketCliente.send(paqueteUdp);

            byte[] bufferRedireccion = new byte[MAXBYTES];
            paqueteUdp = new DatagramPacket(bufferRedireccion, bufferRedireccion.length);
            socketCliente.receive(paqueteUdp);
            String nuevoPuertoString = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), CODTEXTO);
            int nuevoPuerto = Integer.parseInt(nuevoPuertoString);

            String lineaCliente = "";
            String terminarConexion = "Fin transmision";
            boolean finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
            while (!finFtp){
                String respuesta = "";
                lineaCliente = "";
                finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                while (!finFtp){
                    byte[] bufferEntrada = new byte[MAXBYTES];
                    paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length, ipServidor, nuevoPuerto);
                    socketCliente.receive(paqueteUdp);
                    respuesta = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), CODTEXTO);
                    if(respuesta.equals("Cerrando el servidor por falta de actividad")){
                        System.out.println("El servidor se ha cerrado por falta de actividad");
                        System.exit(0);
                    }
                    System.out.println(respuesta);

                    System.out.print("\n==> ");
                    lineaCliente = sc.nextLine();
                    byte[] bufferSalida = new byte[MAXBYTES];
                    bufferSalida = lineaCliente.getBytes();
                    paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipServidor, nuevoPuerto);
                    socketCliente.send(paqueteUdp);

                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                }

            }

        } catch (IOException e){
            System.err.println("Error al intentar conectarse con el servidor");
        }catch (NoSuchElementException e){
            System.err.println("No se ha enviado o recibido nada");
        }finally{
            System.out.println("Saliendo del servidor");
        }
    }
}
