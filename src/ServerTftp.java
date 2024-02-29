import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerTftp {
    static int MAXBYTES = 500;
    static String CODTEXTO = "UTF-8";
    static int PUERTOSERVERPRINCIPAL = 44444;
    static int PUERTOSERVERSECUNDARIO = 49999;

    public static void main(String[] args) {
        final List<Usuario> listaUsuarios = new ArrayList<>();
        int puertoServer = PUERTOSERVERPRINCIPAL;
        DatagramPacket paqueteUdp;
        InetAddress ipCliente;
        int puertoCliente;

        obtenerUsuarios(listaUsuarios);

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

                byte[] bufferSalida;
                bufferSalida = String.valueOf(nuevoPuertoCliente).getBytes();
                paqueteUdp = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, puertoCliente);
                socket.send(paqueteUdp);

                Thread hiloCliente = new Thread(new GestionarClientes(nuevoPuertoCliente, puertoCliente, ipCliente, listaUsuarios, idUsuario));
                hiloCliente.start();
            }


        }catch (IOException e){
            System.err.println("Error con el servidor");
        }
    }

    private static void obtenerUsuarios(List<Usuario> listaUsuarios){
        StringBuilder ficheroString = new StringBuilder();
        File fichero = new File("Datos", "usuarios.dat");
        try(FileReader leerFichero = new FileReader(fichero)){
            BufferedReader brFichero = new BufferedReader(leerFichero);
            while (brFichero.ready()){
                ficheroString.append(brFichero.readLine());
            }
            brFichero.close();
        }catch (IOException e){
            System.err.println("Error al leer el fichero o no existe");
        }

        if(!ficheroString.isEmpty()){
            String[] datosUsuarios = ficheroString.toString().split("},");
            datosUsuarios[datosUsuarios.length - 1] = datosUsuarios[datosUsuarios.length - 1].replace("}", "");
            for (String usuarioString : datosUsuarios) {
                String[] datosUsuario = usuarioString.split(":");
                int idUsuario = Integer.parseInt(datosUsuario[1].substring(0, datosUsuario[1].indexOf(",")));
                String nombreUsuario = datosUsuario[2].substring(0, datosUsuario[2].indexOf(","));
                nombreUsuario = nombreUsuario.substring(1, nombreUsuario.length() - 1);
                String claveUsuario = datosUsuario[3].substring(1, datosUsuario[3].length() - 2);
                listaUsuarios.add(new Usuario(idUsuario, nombreUsuario, claveUsuario));
            }
        }
    }
}

class Usuario{
    private final int id;
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
    public String getUsername() {
        return this.username;
    }
    public String getClave() {
        return this.clave;
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
    static final int MAXBYTES = 500;
    static String CODTEXTO = "UTF-8";
    static String ruta = "Ficheros\\";
    private Temporizador temp;
    private int puertoServer;
    private int puertoCliente;
    private InetAddress ipCliente;
    private final List<Usuario> listaUsuarios;
    private final AtomicInteger idUsuario;
    private byte[] bufferEntrada;
    private byte[] bufferSalida;
    public GestionarClientes(int puertoServer, int puertoCliente, InetAddress ipCliente, List<Usuario> listaUsuarios, AtomicInteger idUsuario){
        this.puertoServer = puertoServer;
        this.puertoCliente = puertoCliente;
        this.ipCliente = ipCliente;
        this.listaUsuarios = listaUsuarios;
        this.idUsuario = idUsuario;

        this.bufferEntrada = new byte[MAXBYTES];
        this.bufferSalida = new byte[MAXBYTES];

        this.temp = new Temporizador(this.bufferSalida, this.ipCliente, this.puertoCliente);
    }
    @Override
    public void run() {
        DatagramPacket paquete;
        int idUsuarioLogueado = -1;

        System.out.println("Nuevo Servidor Con Puerto: " + puertoServer);


        Thread hiloTemporizador = new Thread(temp);
        hiloTemporizador.start();

        String explicacionInicio = "Bienvenid@\n" +
                "Para iniciar sesión introduce este comando: log username password\n" +
                "Si no estas registrado introduce este comando: reg username password password\n" +
                "Para terminar la conexion en cualquier momento introduce: \"Fin transmision\"";

        try(DatagramSocket socketCliente = new DatagramSocket(puertoServer)){
            String respuesta = "";
            String lineaCliente = "";
            String terminarConexion = "Fin transmision";
            boolean finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
            boolean sesionIniciada = false;
            while (!finFtp){

                if(!sesionIniciada){
                    this.bufferSalida = explicacionInicio.getBytes();
                    paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                    socketCliente.send(paquete);
                }

                lineaCliente = "";
                terminarConexion = "Fin transmision";
                finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                sesionIniciada = false;
                while (!finFtp && !sesionIniciada){
                    paquete = new DatagramPacket(this.bufferEntrada, this.bufferEntrada.length);
                    socketCliente.receive(paquete);
                    lineaCliente = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                    System.out.println(this.puertoServer + "" + this.ipCliente + " => " + lineaCliente);

                    this.temp.resetTemporizador();

                    String[] parametros = lineaCliente.split(" ");
                    switch (parametros[0]){
                        case "log":
                            if(parametros.length != 3) {
                                respuesta = "Numero de parametros incorrecto, intentalo de nuevo";
                                break;
                            }

                            boolean coincidencia = false;
                            for (Usuario usuario : listaUsuarios) {
                                if (usuario.getUsername().equals(parametros[1]) && usuario.getClave().equals(parametros[2])) {
                                    coincidencia = true;
                                    idUsuarioLogueado = usuario.getId();
                                    break;
                                }
                            }

                            if(!coincidencia){
                                respuesta = "No se ha encontrado ningun usuario con los parametros proporcionados";
                                break;
                            }

                            respuesta = "Iniciando sesion\n";
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

                                String carpetaUsuario = ruta + "carpeta_" + nuevoUsuario.getUsername();
                                try {
                                    ProcessBuilder pbMkdir = new ProcessBuilder().command("cmd", "/c", "mkdir", carpetaUsuario);
                                    Process procesoMkdir = pbMkdir.start();
                                    int resultadoMkdir = procesoMkdir.waitFor();

                                    if (resultadoMkdir == 0) {
                                        listaUsuarios.add(nuevoUsuario);
                                        respuesta = "Usuario registrado correctamente";
                                    } else {
                                        respuesta = "Error al crear la carpeta para el usuario";
                                    }
                                } catch (IOException | InterruptedException e) {
                                    respuesta = "Error al ejecutar el comando mkdir";
                                }
                            }
                            break;

                        default:
                            if(finFtp){
                                System.out.println("Cerrando el servidor con puerto " + this.puertoServer);
                            }else{
                                respuesta = "Comando erroneo";
                                System.out.println(respuesta);
                            }
                            break;
                    }

                    if(!sesionIniciada){
                        this.bufferSalida = respuesta.getBytes();
                        paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                        socketCliente.send(paquete);
                    }
                }

                if(sesionIniciada){
                    Usuario usuarioLogueado = listaUsuarios.get(idUsuarioLogueado);
                    String explicacionSesionIniciada = "Bienvenid@ " + usuarioLogueado.getUsername() + "\n" +
                            "Para ver los ficheros que hay en tu carpeta: list\n" +
                            "Para obtener un archivo de tu carpeta: get <nombre_archivo_extension>\n" +
                            "Para crear un archivo en tu carpeta: post <nombre_archivo_extension>\n" +
                            "Para eliminar un archivo de tu carpeta: remove <nombre_archivo_extension>\n" +
                            "Para cerrar sesion: \"Disconnect\"";
                    respuesta += explicacionSesionIniciada;

                    this.bufferSalida = respuesta.getBytes();
                    paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                    socketCliente.send(paquete);

                    lineaCliente = "";
                    terminarConexion = "Fin Transmision";
                    String cerrarSesion = "Disconnect";
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", "")) || lineaCliente.replaceAll(" ", "").equalsIgnoreCase(cerrarSesion);
                    while (!finFtp){
                        paquete = new DatagramPacket(this.bufferEntrada, this.bufferEntrada.length);
                        socketCliente.receive(paquete);
                        lineaCliente = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);
                        finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", "")) || lineaCliente.replaceAll(" ", "").equalsIgnoreCase(cerrarSesion);
                        System.out.println(this.puertoServer + "" + this.ipCliente + " => " + lineaCliente);

                        this.temp.resetTemporizador();

                        respuesta = "";
                        String[] parametros = lineaCliente.split(" ");
                        String carpetaUsuario = ruta + "carpeta_" + usuarioLogueado.getUsername();
                        String ficheroUsuario;

                        switch (parametros[0]){
                            case "list":
                                respuesta = ejecutarComando(carpetaUsuario, "dir");
                                break;

                            case "get":
                                boolean errorGet = false;
                                ficheroUsuario = carpetaUsuario + "\\" + parametros[1];
                                respuesta = parametros[1] + "\n";
                                String resultadoGet = devolverFichero(ficheroUsuario);
                                if(resultadoGet.equals("Error al leer el fichero o no existe")){
                                    errorGet = true;
                                    respuesta = resultadoGet;
                                    respuesta += explicacionSesionIniciada;
                                }

                                if(!errorGet){
                                    respuesta += resultadoGet;
                                    respuesta += "\n¿Deseas modificar el fichero?\nS/N";

                                    boolean modifica = lineaCliente.equalsIgnoreCase("S");
                                    boolean noModifica = lineaCliente.equalsIgnoreCase("N");
                                    while(!modifica && !noModifica){
                                        this.bufferSalida = respuesta.getBytes();
                                        paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                                        socketCliente.send(paquete);

                                        paquete = new DatagramPacket(this.bufferEntrada, this.bufferEntrada.length);
                                        socketCliente.receive(paquete);
                                        lineaCliente = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);
                                        System.out.println(this.puertoServer + "" + this.ipCliente + " => " + lineaCliente);

                                        this.temp.resetTemporizador();

                                        modifica = lineaCliente.equalsIgnoreCase("S");
                                        noModifica = lineaCliente.equalsIgnoreCase("N");

                                        if (!modifica && !noModifica){
                                            respuesta = "Respuesta erronea\n¿Deseas modificar el fichero?\nS/N";
                                        }
                                    }

                                    if(modifica){
                                        String explicacionModificarFichero = "Se le ira mostrando linea por linea el contenido del fichero\n" +
                                                "Para modificar el contenido de una linea simplemente escriba su nuevo contenido\n" +
                                                "Si desea pasar a la siguiente linea sin modificar la actual introduce: Down\n" +
                                                "Para terminar de editar introduzca: \"Fin Edicion\"\nPulse enter para comenzar la edición";

                                        this.bufferSalida = explicacionModificarFichero.getBytes();
                                        paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                                        socketCliente.send(paquete);

                                        paquete = new DatagramPacket(this.bufferEntrada, this.bufferEntrada.length);
                                        socketCliente.receive(paquete);

                                        this.temp.resetTemporizador();

                                        respuesta = modificarFichero(ficheroUsuario, socketCliente) + "\n" + explicacionSesionIniciada;
                                    }else{
                                        respuesta = explicacionSesionIniciada;
                                    }
                                }
                                break;

                            case "post":
                                ficheroUsuario = carpetaUsuario + "\\" + parametros[1];
                                respuesta = ejecutarComando(ficheroUsuario, "echo.>");
                                break;

                            case "remove":
                                ficheroUsuario = carpetaUsuario + "\\" + parametros[1];
                                respuesta = ejecutarComando(ficheroUsuario, "del");
                                break;

                            default:
                                if(finFtp){
                                    if(lineaCliente.equalsIgnoreCase(cerrarSesion)){
                                        System.out.println("Cerrando Sesion");
                                        respuesta = explicacionInicio;
                                    }else{
                                        System.out.println("Cerrando el servidor con puerto " + this.puertoServer);
                                    }
                                    sesionIniciada = false;
                                }else{
                                    respuesta = "Comando erroneo";
                                    System.out.println(respuesta);
                                }
                                break;
                        }

                        this.bufferSalida = respuesta.getBytes();
                        paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                        socketCliente.send(paquete);

                    }
                    finFtp = lineaCliente.replaceAll(" ", "").equalsIgnoreCase(terminarConexion.replaceAll(" ", ""));
                }
            }

            synchronized (listaUsuarios){
                actualizarListaUsuarios(listaUsuarios);
            }

        }catch (IOException e){
            System.err.println("Error al intentar comunicarse con el cliente con ip " + this.ipCliente);
        }
    }

    private void actualizarListaUsuarios(List<Usuario> listaUsuarios){
        File crearFichero = new File("Datos", "usuarios.dat");
        try(FileWriter escribirFichero = new FileWriter(crearFichero)){
            PrintWriter pWFichero = new PrintWriter(escribirFichero);

            StringBuilder info = new StringBuilder();
            for(Usuario usuario : listaUsuarios){
                info.append("\t{\n" + "\t\t\"id\":")
                        .append(usuario.getId())
                        .append(",\n\t\t\"username\":\"")
                        .append(usuario.getUsername())
                        .append('\"').append(",\n\t\t\"clave\":\"")
                        .append(usuario.getClave()).append('\"').append("\n\t},\n");
            }

            info = new StringBuilder(info.substring(0, info.length() - 2));
            pWFichero.println(info);
        }catch (IOException e){
            System.err.println("Error al crear o escribir el fichero");
        }
    }

    private String ejecutarComando(String ruta, String comando) throws IOException {
        StringBuilder respuesta = new StringBuilder();
        BufferedReader br = null;
        try {
            ProcessBuilder pb = new ProcessBuilder().command("cmd", "/c", comando, ruta);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            int resultadoP = p.waitFor();

            if (resultadoP == 0) {
                InputStream inSrt = p.getInputStream();
                InputStreamReader inSrtR = new InputStreamReader(inSrt);
                br = new BufferedReader(inSrtR);

                respuesta = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    respuesta.append(linea);
                }

                if(respuesta.isEmpty()){
                    if(comando.equals("del")){
                        respuesta = new StringBuilder("Fichero eliminado con exito");
                    }else{
                        respuesta = new StringBuilder("Fichero creado con exito");
                    }
                }

            } else {
                respuesta = new StringBuilder("Error al realizar la accion");
            }

        }catch (InterruptedException e){
            respuesta = new StringBuilder("El proceso fue interrumpido, intentalo de nuevo");
            System.err.println(respuesta);
        }catch (IOException e){
            respuesta = new StringBuilder("Error al ejecutar el comando");
            System.err.println(respuesta);
        }finally{
            if (br != null) {
                br.close();
            }
        }

        return respuesta.toString();
    }

    private String devolverFichero(String ruta) throws IOException {
        StringBuilder ficheroString = new StringBuilder();
        try (FileReader leerFichero = new FileReader(ruta); BufferedReader brFichero = new BufferedReader(leerFichero)) {

            while (brFichero.ready()) {
                ficheroString.append(brFichero.readLine()).append("\n");
            }

        } catch (IOException e) {
            ficheroString = new StringBuilder("Error al leer el fichero o no existe");
            System.err.println(ficheroString);
        }

        return ficheroString.toString();
    }

    private String modificarFichero(String ruta, DatagramSocket socketCliente) throws IOException {

        List<String> lineasFichero = new ArrayList<>();
        String respuesta = "";
        String lineaFichero;
        String terminarEdicion = "Fin Edicion";
        String lineaSiguiente = "Down";
        BufferedReader brFichero = null;
        PrintWriter pW = null;
        DatagramPacket paquete;

        try(FileReader leerFichero = new FileReader(ruta)){

            brFichero = new BufferedReader(leerFichero);
            boolean finEdicion = respuesta.replaceAll(" ", "").equalsIgnoreCase(terminarEdicion.replaceAll(" ", ""));
            boolean siguienteLinea = respuesta.replaceAll(" ", "").equalsIgnoreCase(lineaSiguiente.replaceAll(" ", ""));
            while (!finEdicion){

                if((lineaFichero = brFichero.readLine()) == null){
                    lineaFichero = "--- Linea Vacia ---";
                }

                this.bufferSalida = lineaFichero.getBytes();
                paquete = new DatagramPacket(this.bufferSalida, this.bufferSalida.length, this.ipCliente, this.puertoCliente);
                socketCliente.send(paquete);

                if(lineaFichero.equals("--- Linea Vacia ---")){
                    lineaFichero = "\n";
                }

                paquete = new DatagramPacket(this.bufferEntrada, this.bufferEntrada.length);
                socketCliente.receive(paquete);
                respuesta = new String(paquete.getData(), 0, paquete.getLength(), CODTEXTO);

                this.temp.resetTemporizador();

                finEdicion = respuesta.replaceAll(" ", "").equalsIgnoreCase(terminarEdicion.replaceAll(" ", ""));
                siguienteLinea = respuesta.replaceAll(" ", "").equalsIgnoreCase(lineaSiguiente.replaceAll(" ", ""));

                if(!finEdicion){
                    if(siguienteLinea){
                        respuesta = lineaFichero;
                    }

                    lineasFichero.add(respuesta);
                }
            }

        }catch (IOException e){
            System.err.println("Error al leer el fichero o no existe");
        }finally {
            if (brFichero != null) {
                brFichero.close();
            }
        }

        try(FileWriter escribirFichero = new FileWriter(ruta)){

            pW = new PrintWriter(escribirFichero, true);
            for (String linea : lineasFichero) {
                pW.println(linea);
            }

        }catch (IOException e){
            System.err.println("Error al escribir el fichero o no existe");
        }finally {
            if (pW != null) {
                pW.close();
            }
        }

        respuesta = "Fichero Editado con exito:\n" + devolverFichero(ruta);
        return respuesta;
    }
}

class Temporizador implements Runnable {
    private long tiempoEspera;
    private boolean reset;
    private byte[] bufferSalida;
    private InetAddress ipCliente;
    private int puertoCliente;

    public Temporizador(byte[] bufferSalida, InetAddress ipCliente, int puertoCliente) {
        this.tiempoEspera = 90000;
        this.reset = false;
        this.bufferSalida = bufferSalida;
        this.ipCliente = ipCliente;
        this.puertoCliente = puertoCliente;
    }

    @Override
    public void run() {
        DatagramPacket paquete;
        try {
            DatagramSocket socketCliente = new DatagramSocket();
            while (true) {
                Thread.sleep(tiempoEspera);

                if (reset) {
                    reset = false;
                } else {
                    String cerrarServidor = "Cerrando el servidor por falta de actividad";
                    System.out.println(cerrarServidor);
                    bufferSalida = cerrarServidor.getBytes();
                    paquete = new DatagramPacket(bufferSalida, bufferSalida.length, ipCliente, puertoCliente);
                    socketCliente.send(paquete);
                    System.exit(0);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Temporizador interrumpido");
        } catch (SocketException e) {
            System.out.println("Error al crear el socket");
        } catch (IOException e) {
            System.out.println("Error al comunicarse con el cliente");
        }
    }

    public void resetTemporizador() {
        reset = true;
    }
}
