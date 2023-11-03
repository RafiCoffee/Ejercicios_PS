package Apuntes_Examen.Posible_Examen;

import java.io.*;
import java.util.Scanner;

public class Encontrar {
    public static void main(String[] args) {
        String ruta = "C:\\Users\\Rubén\\IdeaProjects\\Ejercicios_PS\\src\\Apuntes_Examen\\Posible_Examen\\";

        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce un nombre que quieras buscar");
        String nombre = sc.nextLine();

        ProcessBuilder pbType = new ProcessBuilder("cmd", "/c", "type", ruta + "nombres.dat");
        ProcessBuilder pbFindstr = new ProcessBuilder("findstr", nombre);

        try {
            Process procType = pbType.start();
            Process procFindstr = pbFindstr.start();

            Scanner inType = new Scanner(procType.getInputStream());
            PrintWriter writer = new PrintWriter(procFindstr.getOutputStream());

            boolean coincidencias = false;

            while (inType.hasNextLine()) {
                String line = inType.nextLine();
                if (line.contains(nombre)) {
                    writer.println(line);
                    coincidencias = true; // Se encontró al menos una coincidencia
                }
            }
            writer.close();
            procType.waitFor();

            if (coincidencias) {
                Scanner inFindstr = new Scanner(procFindstr.getInputStream());
                System.out.println("Mostrando información");
                while (inFindstr.hasNextLine()) {
                    System.out.println(inFindstr.nextLine());
                }
                procFindstr.waitFor();
            } else {
                System.out.println("No se encontraron coincidencias");
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}
