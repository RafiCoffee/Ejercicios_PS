package Apuntes_Examen.Apuntes;

import java.io.*;
public class Apuntes_EscribirFichero {
    public static void main(String[] args) throws IOException {

        String ruta = "C:\\Users\\Rubén\\IdeaProjects\\Ejercicios_PS\\src\\Apuntes_Examen\\Apuntes\\Apuntes_Ficheros\\";
        String nombreFichero = "ficheroPrueba";
        InputStreamReader inStR = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(inStR);
        PrintWriter pW = null;

        try{

            /*System.out.println("¿Que nombre quieres ponerle a este fichero?");
            nombreFichero = br.readLine();*/
            pW = new PrintWriter(ruta + nombreFichero + ".txt");

            System.out.println("Escribe que quieres que contenga el fichero, si no escribes nada terminaras");
            String linea;
            while ((linea = br.readLine()) != null && !linea.isEmpty()){
                pW.println(linea);
            }

            System.out.println("Fichero escrito con exito");
            pW.flush();
        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }finally {
            if(pW != null){
                pW.close();
            }
            br.close();
        }
    }
}
