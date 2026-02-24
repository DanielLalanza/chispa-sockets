import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class HiloDama extends Thread {

    // nombre del caballero
    private String nombre;


    // configuramos sus atributos de conexion
    private String hostEli;
    private int puertoEli;


    // Constructor, pedimos el name del caballero y los datos para conectarnos a elisabetha
    public HiloDama(String nombre, String hostEli, int puertoEli) {
        this.nombre = nombre;
        this.hostEli = hostEli;
        this.puertoEli = puertoEli;
    }


    // Metodo run
    @Override
    public void run() {
        System.out.println("DAMA " + nombre + " iniciando sus labores... (Objetivo: " + hostEli + ":" + puertoEli + ")");
        comportamientoDama();
    }


    // Metodo comportamiento Dama definimos como se comportara la dama
    // aqui vamos a registrar el funcionamieto principal
    private void comportamientoDama() {
        Random random = new Random(); // inicializamos nuestro random
        try {
            while (true) {
                // lanzamos un dado para ver que accion realizaremos
                // vigilancia | hablar con lance
                int dadoAccion = random.nextInt(2);


                if (dadoAccion == 0) {
                    // --- LABORES PROPIAS (5s) ---
                    String[] labores = { "Montar a caballo", "Practicar esgrima", "Enterarse de rumores" };
                    System.out.println(nombre + " realizando labor: " + labores[random.nextInt(labores.length)]); // seleccionamos una accion al azar
                    Thread.sleep(5000);


                } else {
                    // --- INTENTAR CONTACTAR A ELISABETHA (20s) ---
                    long fin = System.currentTimeMillis() + 20000; // Aqui sumamos a la hora actual 20 segundos y usamos un long por que es un num muy largo
                    boolean contactado = false; // variable para revisar si se ha contactado o no con lance
                    System.out.println(nombre + " tratando de contactar con Elisabetha en " + hostEli + ":" + puertoEli + "...");


                    // bucle que sigue intentando contactar mientras no hayan pasado 24 seg Y todabia no hayamos contactado
                    while (System.currentTimeMillis() < fin && !contactado) {


                        // abrimos conexion para intentar contactar con Elisabetha
                        try (Socket socket = new Socket(hostEli, puertoEli)) {
                            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


                            // AJUSTE BALANCEADO: 10% Invitación, 20% Rumor (-5), 70% Confidencia (0)
                            int randomVal = random.nextInt(100); // elegimos num al azar del 0 a 99
                            int tipo; // Variable para almacenar el tipo
                            int daño = 0; // Variable para almacenar el Daño


                            // 3 = Invitación | 2 = Rumor infundado | 1 =  Confidencia
                            if (randomVal < 10) { // Si el random da un num menor de 10 el tipo es invitacion y el daño 0
                                tipo = 3;
                                daño = 0;
                            } else if (randomVal < 30) { // Si el random da un num entre 10 y 30 el tipo es Rumor infundado y el daño 5
                                tipo = 1;
                                daño = 5;
                            } else { // Si el random es mayor a 30 el tipo es Confidencia y el daño 0
                                tipo = 1;
                                daño = 0;
                            }


                            // Enviamos el nombre para que Elisabetha sepa quién es y enviamos el tipo y daño
                            salida.writeUTF(nombre);
                            salida.writeInt(tipo);
                            salida.writeInt(daño);


                            // definimos mensajes y comportamiento
                            if (tipo == 3) {
                                System.out.println(nombre + ": ¡Invitación al baile enviada!");
                            } else if (daño > 0) {
                                System.out.println(nombre + ": ¡Rumor venenoso enviado (-2 chispa)!");
                            } else {
                                System.out.println(nombre + ": Confidencia personal contada (sin efecto).");
                            }
                            contactado = true;
                        } catch (IOException e) {
                            // Reintento rápido si el servidor no está disponible aún
                            Thread.sleep(500);
                        }
                    }
                    if (!contactado) {
                        System.out.println(nombre + ": No pudo contactar con Elisabetha tras 20s.");
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " interrumpida.");
        }
    }
}
