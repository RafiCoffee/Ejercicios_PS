package Apuntes_Examen.Posible_Examen;

import java.io.*;
import java.util.Scanner;

public class Encontrar {
    public static void main(String[] args){
        ProcessBuilder pbType = new ProcessBuilder("type", "nombres.dat");
        ProcessBuilder pbFindstr = new ProcessBuilder("findstr", "Paula");

        try {
            Process procType = pbType.start();
            Process procFindstr = pbFindstr.start();

            Scanner inType = new Scanner(procType.getInputStream());
            Scanner inFindstr = new Scanner(procFindstr.getInputStream());
            PrintWriter writer = new PrintWriter(procFindstr.getOutputStream());
            while (inType.hasNextLine()) {
                writer.println(inType.nextLine());
            }
            writer.close();
            procType.waitFor();

            System.out.println("Mostrando informacion");
            while (inFindstr.hasNextLine()) {
                System.out.println(inFindstr.nextLine());
            }
            procFindstr.waitFor();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
