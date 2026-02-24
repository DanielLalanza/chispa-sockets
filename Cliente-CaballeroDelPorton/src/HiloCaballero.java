import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class HiloCaballero extends Thread {

    // nombre del caballero
    private String nombre;


    // configuramos sus atributos de conexion
    private String hostLance;
    private int puertoLance;


    // Constructor, pedimos el name del caballero y los datos para conectarnos a lance
    public HiloCaballero(String nombre, String hostLance, int puertoLance) {
        this.nombre = nombre;
        this.hostLance = hostLance;
        this.puertoLance = puertoLance;
    }


    // Metodo run
    @Override
    public void run() {
        System.out
                .println("⚔️ " + nombre + " iniciando vigilancia... (Objetivo: " + hostLance + ":" + puertoLance + ")");
        comportamientoCaballero();
    }


    // Metodo comportamiento Caballero definimos como se comportara el caballero
    // aqui vamos a registrar el funcionamieto principal
    private void comportamientoCaballero() {
        Random random = new Random(); // inicializamos nuestro random
        try {
            while (true) {
                // lanzamos un dado para ver que accion realizaremos
                // vigilancia | hablar con lance
                int dadoAccion = random.nextInt(2);


                if (dadoAccion == 0) {
                    // --- LABORES DE VIGILANCIA (6s) ---
                    String[] lugares = { "Portón Norte", "Muralla", "Torres" };
                    System.out.println(nombre + " vigilando: " + lugares[random.nextInt(lugares.length)]); // seleccionamos un lugar al azar
                    Thread.sleep(6000);


                } else {
                    // --- HABLAR CON LANCE (25s) ---
                    long fin = System.currentTimeMillis() + 25000; // Aqui sumamos a la hora actual 25 segundos y usamos un long por que es un num muy largo
                    boolean contactado = false; // variable para revisar si se ha contactado o no con lance
                    System.out.println(nombre + " tratando de hablar con Lance en " + hostLance + ":" + puertoLance + "...");


                    // bucle que sigue intentando contactar mientras no hayan pasado 24 seg Y todavia no hayamos contactado
                    while (System.currentTimeMillis() < fin && !contactado) {


                        // abrimos conexion para intentar contactar con lance
                        try (Socket socket = new Socket(hostLance, puertoLance)) {
                            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


                            // 25% Ofensa (reto a duelo), 75% Confidencia
                            int prob = random.nextInt(100); // elegimos num al azar del 0 a 99
                            int daño = (prob < 25) ? 5 : 0; // Si ese num es menor a 25 se hace ofensa con daño 5, sino es una Confidencia con daño 0


                            salida.writeUTF(nombre); // Presentación dinámica
                            salida.writeInt(1); // Tipo 1: Caballero
                            salida.writeInt(daño);


                            // definomos variable de entrada
                            DataInputStream entrada = new DataInputStream(socket.getInputStream());
                            boolean herida = entrada.readBoolean(); // Esperar resultado del duelo (si lo hubo)

                            // definimos mensajes y comportamiento
                            if (daño > 0) {
                                System.out.println(nombre + ": ¡Ofensa enviada! Lance me reta a duelo.");
                                if (herida) {
                                    System.out.println(nombre + ": ¡He sido herido! Recuperándome (30s)...");
                                    Thread.sleep(30000);
                                } else {
                                    System.out.println(nombre + ": Perdí el duelo, pero salí ileso.");
                                }
                            } else {
                                System.out.println(nombre + ": Charla amistosa con Lance.");
                            }
                            contactado = true;
                        } catch (IOException e) {
                            // Reintento rápido si el servidor no está disponible aún
                            Thread.sleep(500);
                        }
                    }
                    if (!contactado) {
                        System.out.println(nombre + ": No pudo hablar con Lance tras 25s.");
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " interrumpido.");
        }
    }
}
