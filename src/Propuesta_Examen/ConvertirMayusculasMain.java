package Propuesta_Examen;

import java.io.*;
import java.util.Scanner;

public class ConvertirMayusculasMain {
    static final String comando = "java";
    static final String opcion = "-cp"; //o cf
    static final String jar = "convertir.jar";
    static final String clase = "ConvertirMayusculas.class";
    public static void main(String[] args) throws IOException {
        InputStreamReader inS = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(inS);
        String linea = br.readLine();

        String[] comandoCompletoJar = {comando, opcion, jar, clase};
        ProcessBuilder pB = new ProcessBuilder(comandoCompletoJar);


        try{
            Process p = pB.start();
        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }

}
