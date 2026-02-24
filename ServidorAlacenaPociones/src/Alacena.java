import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Alacena {
    // Declaamos las variables con la informacion del servidor alacena
    private static final String NOMBRE = "ALACENA";
    private static final int PUERTO = 5005;

    public static void main(String[] args) {
        // Arrancamos el servidor de forma permanente y listo para escuchar peticiones
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println(NOMBRE + " ABIERTA EN PUERTO " + PUERTO);
            while (true) {
                Socket socket = server.accept(); // Creamos el socket del servidor
                new HiloAlacena(socket).start(); // Abrimos un hilo por peticion
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
