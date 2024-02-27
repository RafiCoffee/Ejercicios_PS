import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerTftp {
    static int MAXBYTES = 500;
    static String CODTEXTO = "UTF-8";
    static int PUERTOSERVERPRINCIPAL = 44444;
    static int PUERTOSERVERSECUNDARIO = 49999;

    public static void main(String[] args) {
        List<Usuario> listaUsuarios = new ArrayList<>();
        int puertoServer = PUERTOSERVERPRINCIPAL;
        DatagramPacket paqueteUdp;
        InetAddress ipCliente;
        int puertoCliente;

        String ficheroString = "";
        File fichero = new File("Datos", "usuarios.dat");
        try(FileReader leerFichero = new FileReader(fichero)){
            BufferedReader brFichero = new BufferedReader(leerFichero);
            while (brFichero.ready()){
                ficheroString += brFichero.readLine();
            }
            brFichero.close();
        }catch (IOException e){
            System.err.println("Error al leer el fichero");
        }

        if(!ficheroString.isEmpty()){
            String[] datosUsuarios = ficheroString.split("},");
            datosUsuarios[datosUsuarios.length - 1] = datosUsuarios[datosUsuarios.length - 1].replace("}", "");
            for(int i = 0; datosUsuarios.length > i; i++){
                String[] datosUsuario = datosUsuarios[i].split(":");
                int idUsuario = Integer.parseInt(datosUsuario[1].substring(0, datosUsuario[1].indexOf(",")));
                String nombreUsuario = datosUsuario[2].substring(0, datosUsuario[2].indexOf(","));
                nombreUsuario = nombreUsuario.substring(1, nombreUsuario.length() - 1);
                String claveUsuario = datosUsuario[3].strip();
                listaUsuarios.add(new Usuario(idUsuario, nombreUsuario, claveUsuario));
                System.out.println(listaUsuarios.get(i).toString());
            }
        }

        final AtomicInteger idUsuario = new AtomicInteger(listaUsuarios.size());

        try(DatagramSocket socket = new DatagramSocket(puertoServer)){

            int nuevoPuertoCliente = PUERTOSERVERSECUNDARIO;
            System.out.println("Servidor principal abierto en el puerto " + puertoServer);
            while (true){
                byte[] bufferEntrada = new byte[MAXBYTES];
                paqueteUdp = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteUdp);
                String infoCliente = new String(paqueteUdp.getData(), 0, paqueteUdp.getLength(), CODTEXTO);
                System.out.println("Cliente conectado con ip " + infoCliente);
                nuevoPuertoCliente++;

                ipCliente = paqueteUdp.getAddress();
                puertoCliente = paqueteUdp.getPort();

                byte[] bufferSalida = new byte[MAXBYTES];
                bufferSalida = String.valueOf(nuevoPuertoCliente).getBytes();
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, puertoCliente);
                socket.send(paqueteUdp);

                Thread hiloCliente = new Thread(new GestionarClientes(nuevoPuertoCliente, puertoCliente, ipCliente, listaUsuarios, idUsuario));
                hiloCliente.start();
            }


        }catch (IOException e){

        }
    }
}

class Usuario{
    private int id;
    private String username;
    private String clave;
    public Usuario(int id, String username, String clave){
        this.id = id;
        this.username = username;
        this.clave = clave;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClave() {
        return this.clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", clave='" + clave + '\'' +
                '}';
    }
}

class GestionarClientes extends Thread{
    static int MAXBYTES = 500;
    static String CODTEXTO = "UTF-8";
    private int puertoServer;
    private int puertoCliente;
    private InetAddress ipCliente;
    private List<Usuario> listaUsuarios;
    private final AtomicInteger idUsuario;
    public GestionarClientes(int puertoServer, int puertoCliente, InetAddress ipCliente, List<Usuario> listaUsuarios, AtomicInteger idUsuario){
        this.puertoServer = puertoServer;
        this.puertoCliente = puertoCliente;
        this.ipCliente = ipCliente;
        this.listaUsuarios = listaUsuarios;
        this.idUsuario = idUsuario;
    }
    @Override
    public void run() {
        DatagramPacket paquete;
        int idUsuarioLogueado = -1;

        System.out.println("Nuevo Servidor Con Puerto: " + puertoServer);

        String explicacionInicio = "Bienvenido\n" +
                "Para iniciar sesión introduce este comando: log username password\n" +
                "Si no estas registrado introduce este comando: reg username password password\n" +
                "Para terminar la conexion en cualquier momento introduce: \"Fin trasmision\"";

        try(DatagramSocket socketCliente = new DatagramSocket(puertoServer)){
            String lineaCliente = "";
            String terminarConexion = "Fin trasmision";
            boolean finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
            while (!finFtp){
                byte[] bufferSalidaInicial = new byte[MAXBYTES];
                bufferSalidaInicial = explicacionInicio.getBytes();
                paquete = new DatagramPacket(bufferSalidaInicial, bufferSalidaInicial.length, this.ipCliente, this.puertoCliente);
                socketCliente.send(paquete);

                puertoCliente = paquete.getPort();
                ipCliente = paquete.getAddress();

                lineaCliente = "";
                terminarConexion = "Fin trasmision";
                finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                boolean sesionIniciada = false;
                while (!finFtp && !sesionIniciada){
                    byte[] bufferEntrada = new byte[MAXBYTES];
                    paquete = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                    socketCliente.receive(paquete);
                    lineaCliente = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                    System.out.println(this.puertoServer + "" + this.ipCliente + " => " + lineaCliente);

                    String respuesta = "";
                    String[] parametros = lineaCliente.split(" ");
                    switch (parametros[0]){
                        case "log":
                            if(parametros.length != 3) {
                                respuesta = "Numero de parametros incorrecto, intentalo de nuevo";
                                break;
                            }

                            boolean coincidencia = false;
                            for(int i = 0; i < listaUsuarios.size(); i++){
                                Usuario usuario = listaUsuarios.get(i);
                                if(usuario.getUsername().equals(parametros[1]) && usuario.getClave().equals(parametros[2])){
                                    coincidencia = true;
                                    idUsuarioLogueado = usuario.getId();
                                    break;
                                }
                            }

                            if(!coincidencia){
                                respuesta = "No se ha encontrado ningun usuario con los parametros proporcionados";
                                break;
                            }

                            respuesta = "Iniciando sesion, bienvenido " + parametros[1];
                            sesionIniciada = true;
                            break;

                        case "reg":
                            if(parametros.length != 4){
                                respuesta = "Numero de parametros incorrecto, intentalo de nuevo";
                                break;
                            }

                            if(!parametros[2].equals(parametros[3])){
                                respuesta = "Las contraseñas no coinciden, intentalo de nuevo";
                                break;
                            }

                            synchronized (idUsuario){
                                Usuario nuevoUsuario = new Usuario(idUsuario.getAndIncrement(), parametros[1], parametros[2]);
                                listaUsuarios.add(nuevoUsuario);
                                respuesta = "Usuario registrado correctamente";
                            }
                            break;

                        default:
                            if(finFtp){
                                System.out.println("Cerrando el servidor con puerto " + this.puertoServer);
                            }else{
                                System.out.println("Comando erroneo");
                            }
                            break;
                    }

                    byte[] bufferSalida = new byte[MAXBYTES];
                    bufferSalida = respuesta.getBytes();
                    paquete = new DatagramPacket(bufferSalida, bufferSalida.length, this.ipCliente, this.puertoCliente);
                    socketCliente.send(paquete);
                }

                if(sesionIniciada){
                    Usuario usuarioLogueado = listaUsuarios.get(idUsuarioLogueado);
                    String explicacionSesionIniciada = "Bienvenido " + usuarioLogueado.getUsername() + "\n" +
                            "Para ver los ficheros que hay en tu carpeta: list\n" +
                            "Para obtener un archivo de tu carpeta: get <nombre_archivo_extension>\n" +
                            "Para crear un archivo en tu carpeta: post <nombre_archivo_extension>\n" +
                            "Para eliminar un archivo de tu carpeta: remove <nombre_archivo_extension>\n" +
                            "Para cerrar sesion: \"Disconnect\"";

                    byte[] bufferSalidaSesionIniciada = new byte[MAXBYTES];
                    bufferSalidaSesionIniciada = explicacionSesionIniciada.getBytes();
                    paquete = new DatagramPacket(bufferSalidaSesionIniciada, bufferSalidaSesionIniciada.length, this.ipCliente, this.puertoCliente);
                    socketCliente.send(paquete);

                    lineaCliente = "";
                    terminarConexion = "Disconnect";
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                    while (!finFtp){
                        byte[] bufferEntrada = new byte[MAXBYTES];
                        paquete = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                        socketCliente.receive(paquete);
                        lineaCliente = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);
                        finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                        System.out.println(this.puertoServer + "" + this.ipCliente + " => " + lineaCliente);

                        String respuesta = "";
                        String[] parametros = lineaCliente.split(" ");
                        switch (parametros[0]){
                            case "list":
                                break;

                            default:
                                if(finFtp){
                                    System.out.println("Cerrando Sesion");
                                    respuesta = explicacionInicio;
                                }else{
                                    System.out.println("Comando erroneo");
                                }
                                break;
                        }

                        byte[] bufferSalida = new byte[MAXBYTES];
                        bufferSalida = respuesta.getBytes();
                        paquete = new DatagramPacket(bufferSalida, bufferSalida.length, this.ipCliente, this.puertoCliente);
                        socketCliente.send(paquete);
                    }

                    terminarConexion = "Fin trasmision";
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                }
            }

            File crearFichero = new File("Datos", "usuarios.dat");
            try(FileWriter escribirFichero = new FileWriter(crearFichero);){
                PrintWriter pWFichero = new PrintWriter(escribirFichero);

                String info = "";
                for(Usuario usuario : listaUsuarios){
                    info += "\t{\n" +
                            "\t\t\"id\":" + usuario.getId() +
                            ",\n\t\t\"username\":\"" + usuario.getUsername() + '\"' +
                            ",\n\t\t\"clave\":" + usuario.getClave() +
                            "\n\t},\n";
                }

                info = info.substring(0, info.length() - 2);
                pWFichero.println(info);
            }catch (IOException e){
                System.err.println("Error al crear o escribir el fichero");
            }

        }catch (IOException e){

        }
    }
}
