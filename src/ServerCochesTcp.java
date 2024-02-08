import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerCochesTcp {
    public static void main(String[] args) {
        int numServidor;
        ServerSocket servidor;
        Socket conexion;

        ArrayList<Coche> listaCoches = new ArrayList<>();

        if(args.length < 1){
            System.err.println("Debes determinar un número para crear el servidor");
            System.exit(1);
        }

        numServidor = Integer.parseInt(args[0]);

        String ficheroString = "";
        File fichero = new File("Datos", "coches.dat");
        try(FileReader leerFichero = new FileReader(fichero)){
            BufferedReader brFichero = new BufferedReader(leerFichero);
            while (brFichero.ready()){
                ficheroString += brFichero.readLine();
            }
            brFichero.close();
        }catch (IOException e){
            System.err.println("Error al leer el fichero");
        }

        String[] datosCoches = ficheroString.split("},");
        datosCoches[datosCoches.length - 1] = datosCoches[datosCoches.length - 1].replace("}", "");
        for(int i = 0; datosCoches.length > i; i++){
            String[] datosCoche = datosCoches[i].split(":");
            int idCoche = Integer.parseInt(datosCoche[1].substring(0, datosCoche[1].indexOf(",")));
            String modeloCoche = datosCoche[2].substring(0, datosCoche[2].indexOf(","));
            modeloCoche = modeloCoche.substring(1, modeloCoche.length() - 1);
            int cilindradaCoche = Integer.parseInt(datosCoche[3].strip());
            listaCoches.add(new Coche(idCoche, modeloCoche, cilindradaCoche));
        }

        try{
            System.out.println("Creamos el servidor con el numero " + numServidor);
            servidor = new ServerSocket(numServidor);
            System.out.println("Aceptamos la conexion y esperamos al cliente");

            conexion = servidor.accept();

            ManejarPeticiones manejoPeticiones = new ManejarPeticiones(conexion, listaCoches);
            manejoPeticiones.start();

        } catch (IOException e) {
            System.err.println("Error de algún tipo");
        }
    }
}

class Coche implements Comparable<Coche>{
    private final int id;
    private String modelo;
    private int cilindrada;

    public Coche(int id, String modelo, int cilindrada) {
        this.id = id;
        this.modelo = modelo;
        this.cilindrada = cilindrada;
    }

    public int getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "id=" + id +
                ", modelo='" + modelo + '\'' +
                ", cilindrada=" + cilindrada +
                '}';
    }

    @Override
    public int compareTo(Coche otroCoche) {
        return Integer.compare(this.id, otroCoche.getId());
    }
}

class ManejarPeticiones extends Thread{
    private Socket socketComunicacion;
    private ArrayList<Coche> listaCoches;

    public ManejarPeticiones(Socket conexion, ArrayList<Coche> listaCoches) {
        this.socketComunicacion = conexion;
        this.listaCoches = listaCoches;
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
                        case "post":
                            int posibleId = 1;
                            for(int i = 0; listaCoches.size() > i; i++){
                                if(listaCoches.get(i).getId() != posibleId){
                                    listaCoches.add(new Coche(posibleId, peticionDesectructurada[1], Integer.parseInt(peticionDesectructurada[2])));
                                    posibleId = 0;
                                    break;
                                }else{
                                    posibleId++;
                                }
                            }
                            if(posibleId != 0){
                                listaCoches.add(new Coche(posibleId, peticionDesectructurada[1], Integer.parseInt(peticionDesectructurada[2])));
                            }
                            Collections.sort(listaCoches);
                            pW.println("El coche introducido se ha añadido a la lista correctamente");
                            break;

                        case "get":
                            if(peticionDesectructurada[1].equals("?")){
                                String listadoIds = "";
                                for (Coche listaCoche : listaCoches) {
                                    listadoIds += listaCoche.getId() + " - ";
                                }

                                listadoIds = listadoIds.substring(0, listadoIds.length() - 3);
                                pW.println("Estas son las Ids disponibles en la lista:\n" + listadoIds);
                            }else{
                                try{
                                    System.out.println("Entra");
                                    int idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                                    pW.println(listaCoches.get(idProporcionada - 1).toString());
                                }catch (NumberFormatException e){
                                    System.err.println("La Id proporcionada no es correcta");
                                    pW.println("La Id proporcionada no es correcta");
                                }catch (ArrayIndexOutOfBoundsException e){
                                    System.err.println("La Id proporcionada no existe");
                                    pW.println("La Id proporcionada no existe");
                                }
                            }
                            break;

                        case "put":
                            int idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                            listaCoches.get(idProporcionada - 1).setModelo(peticionDesectructurada[2]);
                            listaCoches.get(idProporcionada - 1).setCilindrada(Integer.parseInt(peticionDesectructurada[3]));

                            System.out.println(listaCoches.get(idProporcionada - 1).toString());
                            pW.println("Coche modificado con exito");
                            break;

                        case "delete":
                            idProporcionada = Integer.parseInt(peticionDesectructurada[1]);

                            listaCoches.remove(idProporcionada - 1);

                            pW.println("Coche eliminado con exito");
                            break;

                        case "help":
                            pW.println("Comandos permitidos:\n" +
                                    "\"post \'modelo\' \'cilindrada\'\" --> Introducir un coche en la lista\n" +
                                    "\"get \'id\'\" --> Imprimir informacion de un coche en especifico\n" +
                                    "\"get ?\" --> Imprimir los ids disponibles\n" +
                                    "\"put \'id\' \'modelo\' \'cilindrada\'\" --> Editar la información de un coche en especifico\n" +
                                    "\"delete \'id\'\" --> Eliminar un coche en especifico");
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

            File crearFichero = new File("Datos", "coches.dat");
            try(FileWriter escribirFichero = new FileWriter(crearFichero);){
                PrintWriter pWFichero = new PrintWriter(escribirFichero);

                String info = "";
                for(Coche coche : listaCoches){
                    info += "\t{\n" +
                            "\t\t\"id\":" + coche.getId() +
                            ",\n\t\t\"modelo\":\"" + coche.getModelo() + '\"' +
                            ",\n\t\t\"cilindrada\":" + coche.getCilindrada() +
                            "\n\t},\n";
                }

                info = info.substring(0, info.length() - 2);
                pWFichero.println(info);
            }catch (IOException e){
                System.err.println("Error al crear o escribir el fichero");
            }
        }catch (IOException e){
            System.err.println("Error de algun tipo");
        }catch (NoSuchElementException e){
            System.out.println("El cliente con la IP " + ipCliente + " ha cerrado su conexion...");
        }
    }
}
