package Sockets.EjemplosClase;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.net.Inet4Address;
import java.net.Inet6Address;

import javax.lang.model.util.ElementScanner6;

import java.lang.StringBuilder;
import java.util.Collections;

public class InfoInterfaces {

    
    public static void muestraInformacionInterface(NetworkInterface eth)throws SocketException{
        String nomInterface;    //nombre de la interfaz
        String nombre;  //nombre de la inerfaz
        byte [] mac;  //dirección mac
        Enumeration<InetAddress> ips;
        
        
        System.out.println ("Nombre de la interfaz: " + eth.getName());
        System.out.println ("Otra forma de nombre interfaz: " + eth.getDisplayName());
        if (!eth.getName().equals("lo")) {
            mac = eth.getHardwareAddress();
            if (mac != null) {
                System.out.print("La mac Actual es  : ");

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    if (i < mac.length - 1)
                        sb.append(String.format("%02X%s", mac[i], "-"));
                    else
                        sb.append(String.format("%02X%s", mac[i], ":"));
                }

                System.out.println(sb);
            } else {
                System.out.println("No se pudo obtener la dirección MAC para esta interfaz.");
            }
        }

        System.out.println("Parámetros ip: ");

        ips = eth.getInetAddresses();  //devolvemos las direcciones ipV4 de cada interfaz

        for(ips=eth.getInetAddresses(); ips.hasMoreElements();){
            InetAddress ip = ips.nextElement();
            if (!ip.isLoopbackAddress())
            //if (!eth.getName().equals("lo"))
                if (ip instanceof Inet4Address)
                    System.out.printf("Dirección ip v4 es: %s%n", ip.getHostAddress());
                else
                    if (ip instanceof Inet6Address)
                        System.out.printf("Dirección ip v6 es: %s%n", ip.getHostAddress());
        }
    }


    public static void main(String[] args) {
        
        List<InterfaceAddress> ipsV4;
        try{

            for (Enumeration<NetworkInterface> eth = NetworkInterface.getNetworkInterfaces(); eth.hasMoreElements();){
                System.out.println("-----> Muestro información de interfaz----->\n\n");
                //para cada una de las interfaces, mostramos información de cada una de ellas.
                muestraInformacionInterface(eth.nextElement());

            }
            
        }catch(SocketException e){
            System.out.println("Error excepción socket");
        }
    }
    
}
