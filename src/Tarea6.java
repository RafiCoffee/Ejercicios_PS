import java.io.*;

public class Tarea6 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("java Tarea6 <nombre_archivo>");
        }
        String archivo = args[0];
        String comando = "dir";
        try {
            File filename = new File(archivo);
            Process proceso = Runtime.getRuntime().exec(comando);
            InputStreamReader in = new InputStreamReader(proceso.getInputStream());
            BufferedReader salida = new BufferedReader(in);
            FileWriter escritura = new FileWriter(filename);
            escritura.write(comando + ":\n");
            String linea;
            while ((linea = salida.readLine()) != null) {
                System.out.println(linea);
                escritura.write(linea + "\n");
            }
            escritura.close();

            proceso.waitFor();
            System.out.println("Ejecutado correctamente");
        } catch (IOException | InterruptedException ex) {
            ex.getLocalizedMessage();
        }
    }
}
