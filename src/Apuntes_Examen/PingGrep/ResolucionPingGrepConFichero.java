package Apuntes_Examen.PingGrep;
import java.io.*;
import java.util.Scanner;
public class ResolucionPingGrepConFichero {
    public static void main(String[] args) {
        try {
            // Paso 1
            ProcessBuilder pbPing = new ProcessBuilder("ping", "www.google.com", "-n", "2");
            ProcessBuilder pbGrep = new ProcessBuilder("findstr", "rtt");
            File destino = new File("/tmp/salida.out");
            pbPing.redirectOutput(destino);
            // Paso 2
            Process procPing = pbPing.start();
            int codigoPing = procPing.waitFor();

            if (codigoPing == 0) {
                // Paso 3
                Process procGrep = pbGrep.start();
                Scanner scPing = new Scanner(new FileInputStream(destino));
                PrintWriter pingToGrep = new PrintWriter(procGrep.getOutputStream());
                // Paso 4
                while (scPing.hasNextLine()) {
                    String line = scPing.nextLine();
                    pingToGrep.println(line);
                }
                pingToGrep.close();
                // Paso 5
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(destino));
                    String lineasVacias;
                    lineasVacias = reader.readLine();
                    while ((lineasVacias = reader.readLine()) != null) { System.out.println(lineasVacias); }
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error al leer o editar el fichero: " + e.getMessage());
                }
                // Paso 6
                Scanner scGrep = new Scanner(procGrep.getInputStream());
                while (scGrep.hasNextLine()) {
                    String line = scGrep.nextLine();
                    System.out.println(line);
                }
            } else {
                System.err.println("Error al ejecutar el comando ping.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al ejecutar el programa: " + e.getMessage());
        }
    }
}