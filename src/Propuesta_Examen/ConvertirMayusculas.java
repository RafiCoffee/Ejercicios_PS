package Propuesta_Examen;

import java.io.*;
import java.util.Scanner;

public class ConvertirMayusculas {
    static final String comando = "java";
    static final String opcion = "-cp"; //o cf
    static final String jar = "convertir.jar";
    static final String clase = "ConvertirMayusculas.class";
    public static void main(String[] args) throws IOException {

        try{
            InputStreamReader entrada = new InputStreamReader(System.in);
            BufferedReader teclado = new BufferedReader(entrada);
            String cadena = teclado.readLine();
            System.out.println(cadena.toUpperCase());
        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }

}