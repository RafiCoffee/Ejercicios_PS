package Apuntes_Examen.Mayusculas;

import java.io.*;

public class ConvertirMayusculas {

    public static void main(String[] args)  {

        try{
            // System.out.println("Soy proceso que convierte a mayusculas");
            InputStreamReader entrada = new InputStreamReader(System.in);
            BufferedReader teclado = new BufferedReader (entrada);
            String cadena = teclado.readLine();
            System.out.println(cadena.toUpperCase());
        }
        catch(IOException e){
            System.out.println ("Hay algún tipo de error con los flujos");
        }

    }

}