package Apuntes_Examen.Mayusculas;

import java.io.*;
import java.util.Scanner;

public class ConvertirMayusculasMain {
    static final String COMANDO = "java";
    static final String OPCION = "-cp";
    static final String FICH_JAR = "convertir.jar";
    static final String FICH_CLASS = "ConvertirMayusculas";
    public static void main(String[] args) throws IOException {

        System.out.print("Introducir texto: ");
        String linea = new Scanner(System.in).nextLine();

        String[] parametros = { COMANDO, OPCION, FICH_JAR, FICH_CLASS };
        ProcessBuilder pb = new ProcessBuilder(parametros);
        Process p = pb.start();
        try{
            //Escribir en la escucha del proceso
            OutputStream os = p.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            PrintWriter pw = new PrintWriter(osw);
            pw.print(linea);
            pw.close();

            try{
                int codRetorno = p.waitFor();
                System.out.println( (codRetorno == 0 ? "Ejecución del subproceso realizada con exito" : "Ejecución del subproceso erronea"));
            }
            catch(InterruptedException e){
                System.err.println("Error del subproceso: " + e.getMessage());
            }

            //Ahora capturamos la cadena convertida a mayusculas.
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String lineaDevuelta = br.readLine();
            System.out.println("La línea leída: "+ linea + " Es convertida a "  + lineaDevuelta);
            br.close();
        }//fin try principal
        catch(IOException e){
            System.out.println("Error IOException");
        }
    }
}