import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Tarea10 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("java Tarea10 <nombre_archivo>");
            System.exit(1);
        }

        String archivo = args[0];
        String comando = "dir";

        try {
            File filename = new File(archivo);

            // Crear una lista de argumentos para el comando
            List<String> command = new ArrayList<>();
            command.add("cmd");
            command.add("/c");
            command.add(comando);

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirigir la salida del proceso al archivo
            processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(filename));

            Process proceso = processBuilder.start();
            proceso.waitFor();
            System.out.println("Ejecutado correctamente");
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}