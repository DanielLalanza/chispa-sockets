import java.io.*;
import java.net.Socket;
import java.util.Random;

public class HiloPorton extends Thread {

    // Nos guardamos el socket de conexion
    private Socket socket;

    // Declaramos un random para reutilizarlo
    private Random random = new Random();

    // Datos para generar carretas
    private String[] origenes = { "Roedalia", "Tierras Lejanas" };
    private String[] productos = { "Trigo", "Madera", "Queso sin fermentar", "Leche cruda", "Manzanas" };

    // Constructor
    public HiloPorton(Socket socket) {
        this.socket = socket;
    }

    // Metodo run para el flujo principal de los hilos del servidor
    @Override
    public void run() {
        try {
            // declaramos las variables de entrada salida del server
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // 1. Generar Carreta Aleatoria
            String origen = origenes[random.nextInt(origenes.length)];
            String producto = productos[random.nextInt(productos.length)];

            System.out.println("Llega carreta de " + origen + " con " + producto);

            // 2. Enviar datos al Guardia (Lance)
            salida.writeUTF(origen);
            salida.writeUTF(producto);

            // 3. Recibir veredicto de Lance (true=Pasa, false=No pasa)
            boolean puedePasar = entrada.readBoolean();

            if (puedePasar)
                System.out.println("Carreta aceptada.");
            else
                System.out.println("Carreta rechazada (Producto prohibido).");

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
