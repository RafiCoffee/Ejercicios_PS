package Apuntes_Examen.Apuntes;

import java.io.*;
public class Apuntes_Runtime_Ping {
    public static void main(String[] args) throws IOException {

        InputStream inSt = null;
        BufferedReader br = null;
        Runtime rT = Runtime.getRuntime();
        Process p = null;

        try{
            p = rT.exec("ping 8.8.8.8");

            inSt = p.getInputStream();
            InputStreamReader inStR = new InputStreamReader(inSt);

            br = new BufferedReader(inStR);

            for(int i = 0; i < 10; i++){
                System.out.println("Saludo " + br.readLine());
            }

        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
            System.exit(-1);
        }finally {
            if(br != null){
                br.close();
            }
        }

        if(p != null){
            p.destroy();
            System.out.println("El ping a muerto");
        }

        try{
            System.out.println("A esperar a que termine el proceso ping");
            p.waitFor();

            System.out.println("El proceso ping ha terminado");

        }catch (InterruptedException e){
            System.err.println("Error detectado: "+ e.getMessage());
            System.exit(-1);
        }

        System.out.println("Estado de termino: " + p.exitValue());
        System.exit(0);
    }
}
