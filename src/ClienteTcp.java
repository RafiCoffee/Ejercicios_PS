/**
 * Versión echo con TCP para el cliente pero 
 * utilizando las clases Scanner y PrinterWriter
 * Mucho más sencillo que utilizar los InputStream y OutputStream
 * @author: Santiago Rodenas Herráiz
 * @version: 4/12/2021
 * @param: Acepta el puerto e ip del servidor
 * 
 */

//package com.github.srodenas.echotcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteTcp {
    public static final Scanner ioS = new Scanner(System.in);
    public static final String CODTEXTO = "UTF-8";
    public static void main(String[] args) {
        int numPuertoServidor;
        String hostServidor;
        Socket socketComunicacion;
        InetAddress ipServidor;
        String echo;
        


        if (args.length <2){
            System.out.println("Error, debes pasar el puerto del servidor y host servidor");
            System.exit(1);
        }

        numPuertoServidor= Integer.parseInt(args[0]);  //puerto servidor
        hostServidor = args[1];         //ip servidor
        try{
            //Creamos el socket del servidor.

            
                socketComunicacion = new Socket(hostServidor, numPuertoServidor);
                ipServidor=socketComunicacion.getInetAddress();
                System.out.printf("Cliente conectado con servidor %s...%n",ipServidor.getHostAddress());
               
                /*
                Ahora conectamos nuestro socket con los input/output
                */
                /*----------- CON ESTOS FLUJOS, FUNCIONA BIEN, PERO PREFIERO UTILIZAR LOS SCANNER Y PRINTWRITER
                InputStream is = socketComunicacion.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, CODTEXTO);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socketComunicacion.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, CODTEXTO);
                BufferedWriter bw = new BufferedWriter(osw);

                /*
                Ahora debemos de leer des input y mandar al output modificado.
                */

                Scanner br = new Scanner (socketComunicacion.getInputStream());
                PrintWriter pw = new PrintWriter(socketComunicacion.getOutputStream());
                System.out.print(">");

                while ( (echo=ioS.nextLine()).length()>0 ){
                 
                    pw.println(echo);
                    pw.flush();
                    System.out.println("Ya he mandado al socket");
                    System.out.printf("Respuesta: %s%n", br.nextLine());
                    System.out.print(">");
                }

/*                  -------- OPCIÓN SIN SCANNER Y PRINTWRITER
                System.out.print(">");
                String echo = ioS.nextLine();
                
                
                
                bw.write(echo);
                bw.newLine();
                bw.flush();

                String lineaRecibida;
                System.out.println("Ya he mandado al socket");
                System.out.printf("Respuesta: %s%n", br.readLine());
              /*  while ( (lineaRecibida=br.readLine())!=null && lineaRecibida.length()!=0){
                    lineaRecibida = "#"+lineaRecibida+"#";
                    bw.write(lineaRecibida);  //mandamos la linea recibida modificada
                    bw.newLine();  //mandamos también un salto de línea
                    bw.flush(); //limpiamos el buffer para que se mande inmediatamente.
                    System.out.printf("Respuesta: %s%n", br.readLine());

                }
                
                //ya hemos mandado la respuesta.
                socketComunicacion.close();
           */

        }  
        catch (UnknownHostException ex){
            System.out.printf("Servidor desconocido %s%n", hostServidor);
            ex.printStackTrace();
            System.exit(2);
        } 
        catch (IOException e){
            System.out.println("Error en flujo de E/S");
            e.printStackTrace();
            System.exit(1);
        }
       


     }   //fin main
}  //fin clase
