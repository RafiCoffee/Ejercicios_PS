import java.io.*;

public class Tarea8 {
    public static void main(String[] args) {

        try {
            Process process = Runtime.getRuntime().exec("ping -n 4 8.8.8.8");
            Thread.sleep(3000);
            process.destroy();

            int exitCode = process.waitFor();
            System.out.println("Código de salida del ping: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*¿Qué sucede si en la ejecución del hijo, el padre no lo mata?

 Si el padre no mata al hijo el hijo seguira ejecutandose hasta
 que haya terminado de realizar los pings.*/