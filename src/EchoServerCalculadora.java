import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EchoServerCalculadora {
    private static final int MAX_BYTES = 1400;
    private static final String COD_TEXTO = "UTF-8";
    private static final  String REGEX = "\\s+(\\d+)\\s+(\\+|\\-|\\*|\\:)\\s+(\\d+)$";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        int numPuertoServidor, numPuertoCliente;
        DatagramPacket paqueteUdp;
        InetAddress ipCliente;

        if(args.length < 1){
            System.err.println("Error, debes determinar un puerto");
            System.exit(1);
        }

        numPuertoServidor = Integer.parseInt(args[0]);

        try (DatagramSocket socket = new DatagramSocket(numPuertoServidor)){
            while (true){
                System.out.println("Esperando informacion del cliente");
                byte[] bufferEntrada = new byte[MAX_BYTES];
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteUdp);

                String lineaRecibida = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), COD_TEXTO);
                ipCliente = paqueteUdp.getAddress();
                numPuertoCliente = paqueteUdp.getPort();

                String fechaActual = LocalDateTime.now().format(FORMATO_FECHA);
                String respuesta = "(" + fechaActual + ") " + realizarOperacion(lineaRecibida);

                byte[] bufferSalida = new byte[MAX_BYTES];
                bufferSalida = respuesta.getBytes();
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, numPuertoCliente);

                socket.send(paqueteUdp);
            }
        }catch (SocketException e){
            System.err.println("Error en el socket servidor");
        }catch (IOException e){
            System.err.println("Error en E/S");
        }
    }

    private static String realizarOperacion(String expresion){
        Pattern patron = Pattern.compile(REGEX);
        Matcher m = patron.matcher(expresion);

        String respuesta = "";
        int resultado = 0;
        if(m.find()){
            int x = Integer.parseInt(m.group(1));
            String signo = m.group(2);
            int y = Integer.parseInt(m.group(3));

            switch (signo){
                case "+":
                    resultado = x + y;
                    break;

                case "-":
                    resultado = x - y;
                    break;

                case "*":
                    resultado = x * y;
                    break;

                default:
                    if(y == 0){
                        respuesta = "Respuesta: Error";
                        return respuesta;
                    }else{
                        resultado = x / y;
                    }
                    break;
            }

            respuesta = "Respuesta: " + resultado;
            return respuesta;
        }else{
            respuesta = "Respuesta: Error";
            return respuesta;
        }
    }
}
