package Propuesta_Examen;

import java.io.*;
import java.util.Scanner;

public class ConvertirMayusculasMain {
    static final String comando = "java";
    static final String opcion = "-cp"; //o cf
    static final String jar = "convertir.jar";
    static final String clase = "ConvertirMayusculas.class";
    public static void main(String[] args) throws IOException {

        System.out.println("Introducir texto: ");
        String linea = new Scanner(System.in).nextLine();

        String[] comandoCompletoJar = {comando, opcion, jar, clase};
        ProcessBuilder pB = new ProcessBuilder(comandoCompletoJar);


        try{
            Process p = pB.start();
            OutputStream os = p.getOutputStream();
            OutputStreamWriter osW = new OutputStreamWriter(os);
            PrintWriter pW = new PrintWriter(osW);
            pW.println(linea);
            pW.close();

            int codRetorno = p.waitFor();
            System.out.println((codRetorno == 0 ? "Ejecución del subproceso realizada con exito" : "Ejecución del subproceso erronea"));

            if(codRetorno == 0){
                InputStream is = p.getInputStream();
                InputStreamReader isR = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isR);
            }else{
                System.err.println("Saliendo del programa sin exito");
            }
        }catch (IOException | InterruptedException e){
            System.err.println("Error detectado: " + e.getMessage());
        }
    }

}
