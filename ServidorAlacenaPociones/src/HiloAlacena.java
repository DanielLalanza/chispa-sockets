import java.io.*;
import java.net.Socket;

public class HiloAlacena extends Thread {

    // Nos guardamos el socket de conexion
    private Socket socket;


    // Almacén compartido de pociones para los alquimistas
    private static int pocionesElisabetha = 0;
    private static int pocionesLance = 0;


    // Constructor
    public HiloAlacena(Socket socket) {
        this.socket = socket;
    }

    // Metodo run para el flujo principal de los hilos del servidor
    @Override
    public void run() {
        try {
            // declaramos las variables de entrada salida del server
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


            // 1. Acción: 1=Guardar (Producir), 2=Sacar (Consumir)
            int accion = entrada.readInt();
            if (accion == 1) { // GUARDAR alquimista guarda una pocion
                // 2. Tipo: 1=Para Eli, 2=Para Lance
                int tipo = entrada.readInt();
                guardarPocion(tipo);
                System.out.println("🧪 Poción guardada. Stock -> Eli: " + pocionesElisabetha + ", Lance: " + pocionesLance);


            } else if (accion == 2) { // SACAR alquimista saca una pocion
                // 2. Tipo de pocion que necesita
                int tipo = entrada.readInt();
                boolean hayStock = sacarPocion(tipo);
                // 3. Respondemos si pudo coger la pocion
                salida.writeBoolean(hayStock);
                if (hayStock)
                    System.out.println("🧪 Poción retirada. Stock -> Eli: " + pocionesElisabetha + ", Lance: " + pocionesLance);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Metodo Guardar pocion
    private synchronized void guardarPocion(int tipo) {
        if (tipo == 1) // si la pocion en tipo uno se almacena con las pociones de elisabetha
            pocionesElisabetha++;
        else // si la pocion en tipo dos se almacena con las pociones de lance
            pocionesLance++;
    }


    // Metodo Sacar pocion
    private synchronized boolean sacarPocion(int tipo) {
        if (tipo == 1) { // si la pocion en tipo uno se saca de el almacen de elisabetha
            if (pocionesElisabetha > 0) {
                pocionesElisabetha--;
                return true;
            }
        } else { // si la pocion en tipo dos se saca de el almacen de lance
            if (pocionesLance > 0) {
                pocionesLance--;
                return true;
            }
        }
        return false;
    }
}
