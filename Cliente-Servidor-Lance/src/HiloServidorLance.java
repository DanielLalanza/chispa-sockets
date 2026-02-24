import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HiloServidorLance extends Thread {

    @Override
    public void run() {
        // Abrimos el socket del server de lance
        try (ServerSocket server = new ServerSocket(Lance.PUERTO_LANCE)) {

            // Bucle permanente del servidor
            while (true) {
                Socket socket = server.accept();// Abrimos el socket del server

                // Variable de entrada y salida del server
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


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
                    // ...
                }

                if (tipo == 1) { // CABALLERO
                    // Lance memoriza quién le ha hablado
                    synchronized (Lance.compañerosConocidos) {
                        if (!Lance.compañerosConocidos.contains(nombreRemitente)) {
                            Lance.compañerosConocidos.add(nombreRemitente);
                        }
                    }

                    if (daño > 0) { // OFENSA si al realizar el duelo recibe un true es que ha herido a su oponente si recibe un false es que ha sido duelo justo y limpio
                        System.out.println("⚔️ " + nombreRemitente + " lanza una ofensa... ¡RETADO A DUELO!");
                        boolean herida = Lance.resolverDuelo();
                        salida.writeBoolean(herida);


                    } else { // CONFIDENCIA (Se guarda para el turno de atención)
                        System.out.println("📩 " + nombreRemitente + " tiene algo que contar. Guardado en el buzón.");
                        Lance.buzonCompañeros.put(nombreRemitente, new int[] { tipo, daño });
                        salida.writeBoolean(false);
                    }


                } else if (tipo == 2) { // ALQUIMISTA
                    if (daño == 20) {
                        System.out.println("🧪 " + nombreRemitente + " (Alquimista) engaña con POCIÓN... (Chispa -20)");
                    } else {
                        System.out.println("🦅 " + nombreRemitente + " (Alquimista) AMENAZA con el Frente Norte... (Chispa -30)");
                    }
                    Lance.modificarChispa(-daño);
                }

                System.out.println("   --> [SISTEMA] Chispa de Lance: " + Lance.chispa + "/100");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}