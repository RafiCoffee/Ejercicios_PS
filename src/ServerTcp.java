/**
 * Versión echo con TCP pero 
 * utilizando las clases Scanner y PrinterWriter
 * Mucho más sencillo que utilizar los InputStream y OutputStream
 * @author: Santiago Rodenas Herráiz
 * @version: 4/12/2021
 * @param: Acepta el puerto 
 * 
 */

//package com.github.srodenas.echotcp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class ServerTcp {
    public static final String CODTEXTO = "UTF-8";
    public static void main(String[] args) {
        int numPuertoServidor;
        ServerSocket socketServidor;
        Socket socketComunicacion=null;
        InetAddress ipCliente;
        boolean noConectado=false;


        if (args.length <1){
            System.out.println("Error, debes pasar el puerto del servidor");
            System.exit(1);
        }

        numPuertoServidor= Integer.parseInt(args[0]);
        try{
            //Creamos el socket del servidor.
            System.out.println("Esperando cliente .....");
            socketServidor = new ServerSocket(numPuertoServidor);
            while(!noConectado) {
                socketComunicacion = socketServidor.accept();  //aceptamos la comunicación con el cliente
                ipCliente = socketComunicacion.getInetAddress();
          //    String hostCliente = ipCliente.getHostAddress();
                System.out.printf("Conexión establecida con cliente con ip ......%s%n", ipCliente);
                /*
                Ahora conectamos nuestro socket con los input/output
                */


                Scanner br = new Scanner (socketComunicacion.getInputStream());
                PrintWriter pw = new PrintWriter(socketComunicacion.getOutputStream());

                System.out.println("Ahora esperamos echo");
                String lineaRecibida;
                while ( (lineaRecibida=br.nextLine())!=null && lineaRecibida.length()>0){
                    System.out.printf("Recibo %s%n",lineaRecibida);
                    lineaRecibida = "#"+lineaRecibida+"#";
                    pw.println(lineaRecibida);  //mandamos la linea recibida modificada
                    pw.flush(); //limpiamos el buffer para que se mande inmediatamente.
                }

                /*   ---------PREFIERO UTILIZAR NUESTRO SCANNER Y PRINTWRITER---------------------

                InputStream is = socketComunicacion.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, CODTEXTO);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socketComunicacion.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, CODTEXTO);
                BufferedWriter bw = new BufferedWriter(osw);
                
                
                Ahora debemos de leer des input y mandar al output modificado.
                 
                System.out.println("Ahora esperamos echo");
                String lineaRecibida;
                while ( (lineaRecibida=br.readLine())!=null && lineaRecibida.length()>0){
                    System.out.printf("Recibo %s%n",lineaRecibida);
                    lineaRecibida = "#"+lineaRecibida+"#";
                    bw.write(lineaRecibida);  //mandamos la linea recibida modificada
                    bw.newLine();  //mandamos también un salto de línea
                    bw.flush(); //limpiamos el buffer para que se mande inmediatamente.
                }
              /*  //ya hemos mandado la respuesta.
                if (!socketComunicacion.isConnected())
                    noConectado = true;
              */

            }

        } catch (NoSuchElementException e){
            System.out.println("El Cliente ha cerrado su conexión....");

            //COMO EJERCICIO......, ANALIZAR EL SIGUIENTE CÓDIGO.........
            if (socketComunicacion!=null && socketComunicacion.isConnected())
                try{
                     socketComunicacion.close();
                }catch (IOException ex){
                    System.out.println("Error en flujo de E/S al cerrar el Socket una vez desconectado con cliente");
                    ex.printStackTrace();
                }
            // FIN CÓDIGO A ANALIZAR.
            
        }
        
        catch (IOException e){
            System.out.println("Error en flujo de E/S");
            e.printStackTrace();
        }
         


    }
}
