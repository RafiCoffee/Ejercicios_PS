package Apuntes_Examen.Apuntes;

import java.io.*;
public class Apuntes_Runtime {
    public static void main(String[] args) throws IOException{

        BufferedReader br = null;
        Runtime rT = Runtime.getRuntime();

        try{
            Process p = rT.exec("cmd /c dir");

            InputStream inSt = p.getInputStream();
            InputStreamReader inStR = new InputStreamReader(inSt);

            br = new BufferedReader(inStR);

            String linea = br.readLine();
            while (linea != null){
                System.out.println(linea);
                linea = br.readLine();
            }

        }catch (IOException e){
            System.err.println("Error detectado: " + e.getMessage());
        }finally {
            if(br != null){
                br.close();
            }
        }
    }
}
