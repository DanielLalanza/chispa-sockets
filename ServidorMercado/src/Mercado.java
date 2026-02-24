import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Mercado {
    // Declaamos las variables con la informacion del servidor alacena
    private static final String NOMBRE = "MERCADO";
    private static final int PUERTO = 5003;

    public static void main(String[] args) {
        // Abrimos el puesto corespondiente listo para recibir peticiones
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println(NOMBRE + " ABIERTO EN PUERTO " + PUERTO);

            // Creamos un bucle permanente para que este siempre el servidor disponible
            while (true) {
                Socket socket = server.accept(); // Abrimos conumicacion con el servidor
                new HiloMercado(socket).start(); // Establecemos un hilo para que atienda la peicion
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
