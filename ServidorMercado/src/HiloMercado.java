import java.io.*;
import java.net.Socket;
import java.util.Random;

public class HiloMercado extends Thread {

    // Nos guardamos el socket de conexion
    private Socket socket;

    // Array con los productos del mercado
    private String[] inventario = {"Queso", "Pan recién horneado", "Especias", "Telas", "Jugo de grosella",
            "Repelente de gatos", "Collares", "Cucharas de boj"};

    // Constructor
    public HiloMercado(Socket socket) {
        this.socket = socket;
    }

    // Metodo run para el flujo principal de los hilos del servidor
    @Override
    public void run() {
        try {
            // declaramos las variables de entrada salida del server
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            Random random = new Random();

            // 1. Ofrecer 5 productos aleatorios
            for (int i = 0; i < 5; i++) {
                String producto = inventario[random.nextInt(inventario.length)];
                salida.writeUTF(producto);
            }

            // 2. Esperar a que el cliente "compre" (el cliente duerme 5s)
            // Leemos qué producto eligió el cliente (índice 0-4)
            int eleccion = entrada.readInt();

            // 3. Despedida
            salida.writeUTF("Gracias por su compra.");
            System.out.println("Cliente compró el producto nº " + (eleccion + 1));

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
