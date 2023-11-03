package Apuntes_Examen.Mayusculas;

import java.io.*;
import java.util.Scanner;

public class ConvertirMayusculasPrincipal {
    static final String COMANDO = "java";
    static final String OPCION = "-cp";
    static final String FICH_JAR = "Mayusculas/convertir.jar";
    static final String FICH_CLASS = "ConvertirMayusculas"; 
    public static void main(String[] args) throws IOException {

        System.out.print("Introducir texto: ");
        String linea = new Scanner(System.in).nextLine();
    
        String[] parametros = { COMANDO, OPCION, FICH_JAR, FICH_CLASS };
        ProcessBuilder pb = new ProcessBuilder(parametros);
        Process p = pb.start();

        BufferedReader br = null;

        try{
            //Escribir en la escucha del proceso
            OutputStream os = p.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            PrintWriter pw = new PrintWriter(osw);
            pw.print(linea);
            pw.close();
        
            try{ 
                int codRetorno = p.waitFor();
                System.out.println( (codRetorno == 0 ? "El subproceso se ha ejecutado correctamente" : "Error al ejecutar el subproceso"));
            }
            catch(InterruptedException e){
                System.err.println("Error detectado: " + e.getMessage());
            }
        
            //Ahora capturamos la cadena convertida a mayusculas.
            InputStream inSt = p.getInputStream();
            InputStreamReader inStR = new InputStreamReader(inSt);
            br = new BufferedReader(inStR);
    
            String lineaDevuelta = br.readLine();
            System.out.println("La línea leída: " + linea + "\nEs convertida a "  + lineaDevuelta);

        }//fin try principal
        catch(IOException e){
            System.out.println("Error detectado: " + e.getMessage());
        }finally {
            if(br != null){
                br.close();
            }
        }
    }
}
