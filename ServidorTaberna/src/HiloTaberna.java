import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class HiloTaberna extends Thread {

    // Nos guardamos el socket de conexion

    private Socket socket;

    // Variables compartidas para saber estados de elisabetha y lance
    private static volatile boolean elisabethaDentro = false;
    private static volatile boolean elisabethaReady = false;
    private static volatile boolean lanceDentro = false;
    private static volatile boolean lanceReady = false;

    // Constructor
    public HiloTaberna(Socket socket) {
        this.socket = socket;
    }

    // Metodo run para el flujo principal de los hilos del servidor
    @Override
    public void run() {
        int idPersonaje = -1;
        try {
            // declaramos las variables de entrada salida del server
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            // 1. Leemos el id del personaje que se ha comunicado con nosotros
            // 1. Leemos si el personaje que acaba de entrar esta buscando su FINAL FELIZ o no
            idPersonaje = entrada.readInt();
            boolean buscandoFinal = entrada.readBoolean();


            // 2. Entrar
            entrar(idPersonaje, buscandoFinal);

            // 3. Estancia (Simulación de tiempo real en el servidor)
            // Dividimos el tiempo para permitir "detección tardía" del otro si entra
            // después
            boolean encuentro = false;
            for (int i = 0; i < 16; i++) { // 16 * 500ms = 8 segundos
                Thread.sleep(500);
                if (!encuentro) {
                    encuentro = verificar(idPersonaje, buscandoFinal);
                }
            }

            // 4. GRACE PERIOD: Esperar un poco antes de marcar salida
            if (encuentro && buscandoFinal) {
                // Si es victoria, esperamos 15s para que el otro proceso reciba el true también
                Thread.sleep(15000);
            } else {
                Thread.sleep(2000); // 2 segundos normal
            }

            // 5. Enviar resultado
            salida.writeBoolean(encuentro);
            if (encuentro) {
                System.out.println("¡CHISPA! " + (idPersonaje == 1 ? "Elisabetha" : "Lance") +
                        (buscandoFinal ? " ha hallado el FINAL FELIZ." : " ha visto al otro."));
            }

        } catch (Exception e) {
            System.out.println("Error en Taberna (" + idPersonaje + "): " + e.getMessage());
        } finally {
            if (idPersonaje != -1) {
                salir(idPersonaje);
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }


    // Metodo para saber si elisabetha y lance estan biscando su final feliz
    private synchronized void entrar(int id, boolean ready) {
        if (id == 1) {
            elisabethaDentro = true;
            elisabethaReady = ready;
            System.out.println("Elisabetha ha entrado a la taberna" + (ready ? " BUSCANDO EL FINAL." : "."));
        } else {
            lanceDentro = true;
            lanceReady = ready;
            System.out.println("Lance ha entrado a la taberna" + (ready ? " BUSCANDO EL FINAL." : "."));
        }
    }


    // Metodo para verificar el estado de los personajes
    private synchronized boolean verificar(int id, boolean ready) {
        if (id == 1) { // Elisabetha pregunta por Lance
            if (ready) {
                return lanceDentro && lanceReady; // Solo gana si Lance también está listo
            } else {
                return lanceDentro; // Encuentro normal
            }
        } else { // Lance pregunta por Elisabetha
            if (ready) {
                return elisabethaDentro && elisabethaReady; // Solo gana si Eli también está lista
            } else {
                return elisabethaDentro; // Encuentro normal
            }
        }
    }


    // Metodo para salir tras un periodo de tiempo si no estan los dos listos para su final feliz
    private synchronized void salir(int id) {
        if (id == 1) {
            elisabethaDentro = false;
            elisabethaReady = false;
            System.out.println("Elisabetha ha salido de la taberna.");
        } else {
            lanceDentro = false;
            lanceReady = false;
            System.out.println("Lance ha salido de la taberna.");
        }
    }
}