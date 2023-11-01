package Apuntes_Examen.PingGrep;

import java.io.*;
import java.util.Scanner;
public class ResolucionPingFindstr {
    public static void main(String[] args) {
        try {
            // Paso 1
            ProcessBuilder pbPing = new ProcessBuilder("ping", "www.google.com", "-n", "2");
            ProcessBuilder pbFindstr = new ProcessBuilder("findstr", ".");
            // Paso 2
            Process procPing = pbPing.start();
            Process procFindstr = pbFindstr.start();
            // Paso 3
            Scanner inFromPing = new Scanner(procPing.getInputStream());
            PrintWriter outToFindstr = new PrintWriter(procFindstr.getOutputStream());
            // Paso 4
            inFromPing.nextLine();
            while (inFromPing.hasNextLine()) {
                String line = inFromPing.nextLine();
                outToFindstr.println(line + ".");
            }
            outToFindstr.close();
            // Paso 5
            Scanner inFromFindstr = new Scanner(procFindstr.getInputStream());
            // Paso 6
            while (inFromFindstr.hasNextLine()) {
                String line = inFromFindstr.nextLine();
                line = line.substring(0, line.length() - 1);
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error al ejecutar el programa: " + e.getMessage());
        }
    }
}