package Apuntes_Examen.Apuntes;

import java.io.*;
import java.util.Scanner;
public class Apuntes_Nslookup {
    public static void main(String[] args){
        ProcessBuilder pb = new ProcessBuilder().command("nslookup");
        //pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        Scanner sc = new Scanner(System.in);

        String linea;
        System.out.println("Introducir el nombre del dominio");
        try{
            while((linea = sc.nextLine()) != null && !linea.isEmpty()){
                Process p = pb.start();

                try(PrintWriter pW = new PrintWriter(p.getOutputStream())){
                    Scanner scP = new Scanner(p.getInputStream());

                    System.out.println(linea);
                    pW.println(linea);
                    pW.flush();

                    while(scP.hasNextLine()){
                        String mostrarLinea = scP.nextLine();
                        System.out.println(mostrarLinea);
                    }
                }try{
                    p.waitFor();
                }catch(InterruptedException e){
                    System.err.println("Error detectado: " + e.getMessage());
                }finally {
                    System.out.println("Introducir nombre del dominio");
                }
            }
        }catch(IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}
