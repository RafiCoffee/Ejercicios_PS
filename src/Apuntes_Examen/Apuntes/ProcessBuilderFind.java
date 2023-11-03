package Apuntes_Examen.Apuntes;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderFind {
    public static void main(String[] args){

        String ruta = "C:\\Users\\Rubén\\IdeaProjects\\Ejercicios_PS\\src\\Apuntes_Examen\\Apuntes\\Apuntes_Ficheros\\";

        long inicio = System.currentTimeMillis();
        ProcessBuilder pb = new ProcessBuilder().command("cmd", "/c", "dir", ruta + "*.txt", "2000");

        try{
            Process p = pb.start();

            if(p.waitFor(2, TimeUnit.SECONDS)){
                InputStreamReader inStR = new InputStreamReader(p.getInputStream());
                BufferedReader br = new BufferedReader(inStR);

                try{
                    String linea;
                    while((linea = br.readLine()) != null){
                        System.out.println(linea);
                    }
                }catch(IOException e){
                    System.err.println("Error detectado: " + e.getMessage());
                }finally {
                    br.close();
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
