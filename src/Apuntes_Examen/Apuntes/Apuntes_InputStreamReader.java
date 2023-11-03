package Apuntes_Examen.Apuntes;

import java.io.*;
public class Apuntes_InputStreamReader {
    public static void main(String[] args) throws IOException{

        //InputStreamReader funciona como un Scanner en este contexto
        InputStreamReader inStR = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(inStR);

        try{

            String mensaje;

            System.out.println("Introduce texto: ");
            mensaje = br.readLine();
            System.out.println("El texto introducido es: ''" + mensaje + "''");
        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }finally {
            br.close();
        }
    }
}
