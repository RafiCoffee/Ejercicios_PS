package Apuntes_Examen.Apuntes;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderFind_ScannerPrintWriter {
    public static void main(String[] args){

        String ruta = "C:\\Users\\Rubén\\IdeaProjects\\Ejercicios_PS\\src\\Apuntes_Examen\\Apuntes\\Apuntes_Ficheros\\";

        long inicio = System.currentTimeMillis();
        ProcessBuilder pb = new ProcessBuilder().command("cmd", "/c", "dir", ruta + "*.txt");

        try{
            Process p = pb.start();

            if(p.waitFor(2, TimeUnit.SECONDS)){

                String linea;
                Scanner sc = new Scanner(p.getInputStream());

                while((linea = sc.nextLine()) != null){

                    if(!sc.hasNextLine()){
                        break;
                    }

                    try(PrintWriter pW = new PrintWriter(p.getOutputStream())){
                        System.out.println(linea);
                        pW.println(linea);
                        pW.flush();
                    }try{
                        p.waitFor();
                    }catch(InterruptedException e){
                        System.err.println("Error detectado: " + e.getMessage());
                    }
                }
            }else{
                p.destroy();
                System.out.println("El tiempo de espera ha transcurrido. Se ha matado al proceso hijo");
            }
        }catch(IOException | InterruptedException e){
            System.err.println("Error detectado: " + e.getMessage());
        }finally {
            long fin = System.currentTimeMillis();
            long tiempoTranscurrido = fin - inicio;
            tiempoTranscurrido = tiempoTranscurrido / 1000;
            System.out.println("Tiempo transcurrido: " + tiempoTranscurrido + " segundos");
        }
    }
}