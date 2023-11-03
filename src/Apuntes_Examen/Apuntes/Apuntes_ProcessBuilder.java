package Apuntes_Examen.Apuntes;

import java.io.*;
public class Apuntes_ProcessBuilder {
    public static void main(String[] args) throws IOException{

        String ruta = "C:\\Users\\Rubén\\IdeaProjects\\Ejercicios_PS\\src\\Apuntes_Examen\\Apuntes\\Apuntes_Ficheros\\";
        ProcessBuilder pB = new ProcessBuilder().command("cmd", "/c", "type", ruta + "ficheroPrueba.txt");
        pB.redirectErrorStream(true);

        try{
            Process p = pB.start();
            System.out.println("Soy el proceso padre y creare el type\n");

            InputStream inSt = p.getInputStream();
            InputStreamReader inStR = new InputStreamReader(inSt);
            BufferedReader br = new BufferedReader(inStR);

            try{
                String linea;
                while((linea = br.readLine()) != null){
                    System.out.println(linea);
                }
            }catch(IOException e){
                System.err.println("Error al leer el fichero: " + e.getMessage());
            }finally {
                br.close();
            }

            int exitCode = p.waitFor();
            System.out.println("El proceso a terminado con elcodigo de salida " + exitCode);

        }catch(Exception e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }
}
