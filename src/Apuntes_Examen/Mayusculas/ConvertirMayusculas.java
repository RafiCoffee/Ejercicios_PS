package Apuntes_Examen.Mayusculas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * @author: Santi PSP 2022/2023
 * Convierte una cadena leía desde su flujo de entrada y la 
 * devuelve a su flujo de salida convertida en mayúsculas.
 */
public class ConvertirMayusculas {

    public static void main(String[] args) throws IOException {

        InputStreamReader inStR = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (inStR);

        try{
            System.out.println("Soy proceso que convierte a mayusculas");

            String linea = br.readLine();
            System.out.println(linea.toUpperCase());
        }
        catch(IOException e){
            System.out.println ("Hay algún tipo de error con los flujos");
        }finally{
            br.close();
        }
    
    }

}
