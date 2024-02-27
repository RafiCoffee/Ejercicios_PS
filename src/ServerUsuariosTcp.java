import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerUsuariosTcp {
    public static void main(String[] args) {
        int numServidor;
        ServerSocket servidor;
        Socket conexion;

        ArrayList<User> listaUsuarios = new ArrayList<>();

        if(args.length < 1){
            System.err.println("Debes determinar un número para crear el servidor");
            System.exit(1);
        }

        numServidor = Integer.parseInt(args[0]);

        try{
            System.out.println("Creamos el servidor con el numero " + numServidor);
            servidor = new ServerSocket(numServidor);
            System.out.println("Aceptamos la conexion y esperamos al cliente");

            conexion = servidor.accept();

            ManejarPeticionesUsuarios manejoPeticiones = new ManejarPeticionesUsuarios(conexion, listaUsuarios);
            manejoPeticiones.start();

        } catch (IOException e) {
            System.err.println("Error de algún tipo");
        }
    }
}

class User implements Comparable<User>{
    private final int id;
    private String nombre;
    private String email;
    private String password;

    public User(int id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public int compareTo(User otroUsuario) {
        return Integer.compare(this.id, otroUsuario.getId());
    }
}

class ManejarPeticionesUsuarios extends Thread{
    private Socket socketComunicacion;
    private ArrayList<User> listaUsuarios;
    private AtomicInteger Idautomatica;
    private ClienteTcpApuntes clienteConectado;

    public ManejarPeticionesUsuarios(Socket conexion, ArrayList<User> listaUsuarios) {
        this.socketComunicacion = conexion;
        this.listaUsuarios = listaUsuarios;
        this.Idautomatica = new AtomicInteger(0);
    }

    public void setClienteConectado(ClienteTcpApuntes clienteConectado) {
        this.clienteConectado = clienteConectado;
    }

    @Override
    public void run() {
        InetAddress ipCliente = socketComunicacion.getInetAddress();

        try{
            System.out.println("Hilo " + this.getName() + " comienza con el cliente con IP " + ipCliente);

            Scanner scCliente = new Scanner(socketComunicacion.getInputStream());
            PrintWriter pW = new PrintWriter(socketComunicacion.getOutputStream(), true);

            System.out.println("Realiza tus peticiones");
            String peticion;
            do{
                peticion = scCliente.nextLine();
                if(!peticion.equals("fin")){
                    String[] peticionDesectructurada = peticion.split(" ");

                    switch (peticionDesectructurada[0].trim()){
                        case "reg":
                            listaUsuarios.add(new User(Idautomatica.incrementAndGet(), peticionDesectructurada[1], peticionDesectructurada[2], peticionDesectructurada[3]));
                            Collections.sort(listaUsuarios);

                            try{
                                User usuarioRegistrado = listaUsuarios.get(Idautomatica.get() - 1);
                                File crearFicheroUsuario = new File("Ficheros", usuarioRegistrado.getNombre() + ".dat");
                                FileWriter escribirFicheroUsuario = new FileWriter(crearFicheroUsuario);
                                PrintWriter pWFicheroUsuario = new PrintWriter(escribirFicheroUsuario);

                                String info = usuarioRegistrado.toString();
                                pWFicheroUsuario.println("Datos del User:\n" + info);
                                pWFicheroUsuario.close();
                            }catch (IOException e){
                                pW.println("Error al crear o escribir en el fichero del User");
                            }

                            pW.println("Te has registrado correctamente, se va a iniciar sesión automaticamente");

                            break;

                        case "log":
                            if(peticionDesectructurada[1].equals("?")){
                                String listadoIds = "";
                                for (User usuarios : listaUsuarios) {
                                    listadoIds += usuarios.getId() + " - ";
                                }

                                listadoIds = listadoIds.substring(0, listadoIds.length() - 3);
                                pW.println("Estas son las Ids disponibles en la lista:\n" + listadoIds);
                            }else{
                                try{
                                    int idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                                    pW.println(listaUsuarios.get(idProporcionada - 1).toString());
                                }catch (NumberFormatException e){
                                    System.err.println("La Id proporcionada no es correcta");
                                    pW.println("La Id proporcionada no es correcta");
                                }
                            }
                            break;

                        case "datu":
                            int idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                            break;

                        case "list":
                            idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                            listaUsuarios.remove(idProporcionada - 1);

                            pW.println("Coche eliminado con exito");
                            break;

                        case "hash5":
                            break;

                        default:
                            if(!peticionDesectructurada[0].equals("fin")){
                                pW.println("Petición proporcionada erronea");
                            }else{
                                pW.println("Saliendo del servidor");
                            }
                            break;
                    }
                }

            }while (!peticion.equals("fin"));
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException e){
            System.out.println("El cliente con la IP " + ipCliente + " ha cerrado su conexion...");
        }
    }
}
