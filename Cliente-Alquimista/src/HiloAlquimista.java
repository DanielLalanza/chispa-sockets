import java.io.*;
import java.net.Socket;
import java.util.Random;

public class HiloAlquimista extends Thread {

    // nombre del alquimista
    private String nombre;


    // configuramos sus atributos de conexion
    private String host;
    private int puertoAlacena;
    private int puertoEli;
    private int puertoLance;


    // Constructor, pedimos el name del alquimista y los datos para conectarnos a
    // lance y elizabetha
    public HiloAlquimista(String nombre, String host, int puertoAlacena, int puertoEli, int puertoLance) {
        this.nombre = nombre;
        this.host = host;
        this.puertoAlacena = puertoAlacena;
        this.puertoEli = puertoEli;
        this.puertoLance = puertoLance;
    }


    // Metodo run
    @Override
    public void run() {
        System.out.println("🧙 " + nombre + " iniciando su trabajo alquímico...");
        comportamientoAlquimista();
    }


    // Metodo comportamientoAlquimista definimos como se comportara el alquimista
    // aqui vamos a registrar el funcionamieto principal
    private void comportamientoAlquimista() {
        Random random = new Random(); // inicializamos nuestro random
        try {
            while (true) {
                // lanzamos un dado para ver que accion realizaremos
                // estudiar | visitar a elizabetha | visitar a lance
                int dadoAccion = random.nextInt(100);


                // --- 60% ESTUDIAR CALDEROS ---
                if (dadoAccion < 60) {
                    System.out.println("🧙 " + nombre + " estudiando calderos...");
                    Thread.sleep(30000); // 30s Estudio


                    // Probabilidad de obtener poción al estudiar
                    int dadoPocion = random.nextInt(100);
                    if (dadoPocion < 30) {
                        guardarPocion(1); // 30% Poción para bajar chispa a elizabeta
                    } else if (dadoPocion < 60) {
                        guardarPocion(2); // 30% Poción de excusa para visitar a lance y bajarle la chispa
                    } else {
                        System.out.println(nombre + ": El estudio falló."); // 40% de fracasar elaborando la pocion
                    }


                    // --- 20% VISITAR ELISABETHA ---
                } else if (dadoAccion < 80) {
                    if (sacarPocion(1)) { // Miramos si tenemos una pocion para bajarle la chispa a lisabetha
                        System.out.println("🧙 " + nombre + " viaja a visitar a Elisabetha... (5s)");
                        Thread.sleep(5000); // la visitamos 5s

                        if (random.nextInt(100) < 15) { // Intentar engañar por tonico de belleza (15% éxito)
                            atacar(puertoEli, 2, 10); // si elizabeta es engañada por el alquimista -10 de chispa
                        } else {
                            System.out.println(nombre + ": ¡Elisabetha no se tragó el engaño!");
                        }
                    } else {
                        System.out.println(nombre + ": No tengo poción para Elisabetha.");
                    }


                    // --- 20% VISITAR LANCE ---
                } else {
                    if (sacarPocion(2)) { // miramos si hay pocion para lance ya que la necesitamos como excusa
                        int subAccion = random.nextInt(100);
                        if (subAccion < 80) { // ENGAÑAR (80%)
                            System.out.println(
                                    "🧙 " + nombre + " viaja a visitar a Lance para engañarlo con la poción... (7s)");
                            Thread.sleep(7000);
                            if (random.nextInt(100) < 20) {
                                atacar(puertoLance, 2, 20); // si el alquimista consigue engañar a lance este la baja -20 de chispa
                            } else {
                                System.out.println(nombre + ": Lance no creyó el engaño.");
                            }

                        } else { // AMENAZAR (20%)
                            System.out.println(
                                    "🧙 " + nombre + " viaja a amenazar a Lance usando la poción como prueba... (7s)");
                            Thread.sleep(7000);
                            if (random.nextInt(100) < 20) {
                                atacar(puertoLance, 2, 30); // si el alquimista conigue amenzara a lance la chispa le baja -30
                            } else {
                                System.out.println(nombre + ": Lance ignoró la amenaza.");
                            }
                        }
                    } else {
                        System.out.println("🧙 " + nombre
                                + ": No puedo visitar a Lance, no tengo ninguna poción para usar de excusa.");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Metodo guardarPocion
    private void guardarPocion(int tipo) {
        // abrimos conexion con la alacena de pociones para almacenar la pocion
        try (Socket s = new Socket(host, puertoAlacena);
                DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            // Guardamos 1 pocion del tipo que se desse, para eli o lance
            out.writeInt(1);
            out.writeInt(tipo);
            System.out.println(nombre + ": Poción (" + tipo + ") guardada en alacena.");
        } catch (IOException e) {
            System.err.println(
                    nombre + ": Error conectando con Alacena " + host + ":" + puertoAlacena + " - " + e.getMessage());
        }
    }


    // Metodo sacarPocion
    private boolean sacarPocion(int tipo) {
        // abrimos conexion con la alacena de pociones para sacar una pocion
        try (Socket s = new Socket(host, puertoAlacena);
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            // Sacamos una pocion del tipo que se desee, para eli o lance
            out.writeInt(2);
            out.writeInt(tipo);
            return in.readBoolean();
        } catch (IOException e) {
            System.err.println(
                    nombre + ": Error conectando con Alacena " + host + ":" + puertoAlacena + " - " + e.getMessage());
            return false;
        }
    }


    // Metodo atacar
    private void atacar(int puerto, int tipoAtaque, int daño) {
        // abrimos conexion con lance o elizabetha para atacarlos de forma engañosa
        try (Socket s = new Socket(host, puerto);
                DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            // enviamos el nombre para que sepan a donde vamos a atacar
            out.writeUTF(nombre);
            // el ataque es tipo 2 = Alquimista | si lance recibiera un tipo 1 seria un caballero mirar cliente lance
            out.writeInt(tipoAtaque);
            out.writeInt(daño);
            System.out.println("⚔️ " + nombre + ": ¡ATAQUE EXITOSO! Daño: " + daño + " a puerto " + puerto);
        } catch (IOException e) {
            System.err.println(nombre + ": Error atacando a puerto " + puerto + ": " + e.getMessage());
        }
    }
}