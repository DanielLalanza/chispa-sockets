import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HiloServidorElisabetha extends Thread {

    @Override
    public void run() {
        // Abrimos el socket del server de elisabetha
        try (ServerSocket server = new ServerSocket(Elisabetha.PUERTO_ELI)) {

            // Bucle permanente del servidor
            while (true) {
                Socket socket = server.accept(); // Abrimos el socket del server
                DataInputStream entrada = new DataInputStream(socket.getInputStream()); // Variable de entrada del server


                // variables generales
                String nombreRemitente = "Desconocido";
                int tipo = -1;
                int daño = 0;

                try {
                    // Intentamos leer el nombre
                    socket.setSoTimeout(1000); // Pequeño timeout para no bloquear
                    nombreRemitente = entrada.readUTF();
                    tipo = entrada.readInt();
                    daño = entrada.readInt();
                } catch (Exception e) {
                    // Si falla, es posible que sea un Alquimista
                }


                if (tipo == 1 || tipo == 3) { // RUMOR o INVITACIÓN de Dama
                    System.out.println("📩 Mensaje recibido de " + nombreRemitente + ". Guardado en el buzón.");
                    // Almacenamos los nombres de las damas en memoria
                    synchronized (Elisabetha.damasConocidas) {
                        if (!Elisabetha.damasConocidas.contains(nombreRemitente)) {
                            Elisabetha.damasConocidas.add(nombreRemitente);
                        }
                    }
                    Elisabetha.buzonDamas.put(nombreRemitente, new int[] { tipo, daño });


                } else if (tipo == 2) { // POCIÓN / ALQUIMISTA (O protocolo antiguo)
                    System.out.println("🧪 Alquimista engaña con poción... (Chispa -" + daño + ")");
                    Elisabetha.modificarChispa(-daño);


                } else {
                    // Manejo para protocolo antiguo si llega directo
                    // Elisabetha.modificarChispa(-daño);
                }

                System.out.println("   --> [SISTEMA] Chispa de Elisabetha: " + Elisabetha.chispa + "/100");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}