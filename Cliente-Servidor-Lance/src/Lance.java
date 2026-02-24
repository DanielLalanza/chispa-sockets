import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Lance {

    // Declaamos las variables con la informacion del servidor Elisabetha
    private static final String NOMBRE = "Lance";
    private static final String HOST = "localhost";
    public static final int PUERTO_LANCE = 5001;
    private static final int PUERTO_TABERNA = 5002;
    private static final int PUERTO_PORTON = 5004;

    // Variable Compartida para diferetes usos
    public static volatile int chispa = 0;
    public static boolean conoceAElisabetha = false;

    // Lista para que Lance sepa quienes son sus caballeros
    public static final java.util.List<String> compañerosConocidos = new java.util.ArrayList<>();

    // Buzón para guardar las ofensas de los caballeros
    public static final java.util.Map<String, int[]> buzonCompañeros = new java.util.concurrent.ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 1. ARRANCAR EL SERVIDOR EN SEGUNDO PLANO
        HiloServidorLance servidor = new HiloServidorLance();
        servidor.start();
        System.out.println("🛡️ " + NOMBRE + " LISTO (Chispa: " + chispa + ")");

        // 2. BUCLE DEL CLIENTE
        Random random = new Random(); // Nos creamos un Random para futuros usos
        try {
            while (true) {
                // Si la chispa baja de 100 (por ataques), volvemos a las acciones normales
                if (chispa < 100) {
                    Thread.sleep(3000); // Descanso entre acciones
                    int accion = random.nextInt(3); // 0, 1, 2

                    switch (accion) {
                        case 0: // HABLAR CON COMPAÑEROS
                            atenderCompañeros();
                            break;
                        case 1: // GUARDIA EN PORTÓN NORTE
                            hacerGuardiaPorton();
                            break;
                        case 2: // GUARDIA EN TABERNA
                            hacerGuardiaTaberna();
                            break;
                    }
                    System.out.println("   --> Nivel actual de Chispa (" + NOMBRE + "): " + chispa + "/100");
                } else {
                    // FASE FINAL: Solo se ejecuta si la chispa llega al maximo
                    System.out.println("¡" + NOMBRE + " TIENE LA CHISPA AL MÁXIMO! Buscando encuentro final...");
                    Thread.sleep(2000);
                    System.out.println("❤️ " + NOMBRE + " acude a la Taberna buscando su final feliz...");

                    if (intentarFinalFeliz()) { // si recibe el aviso que elisabetha esta lista para su final feliz se
                                                // encuientran los dos
                        System.out.println(
                                "\n🎉🎉🎉 ¡LANCE Y ELISABETHA SE HAN ENCONTRADO CON LA CHISPA AL MÁXIMO! 🎉🎉🎉");
                        System.out.println("FIN DE LA GRAN SIMULACIÓN DEL CANTAR DE LA LEYENDA DE ROEDALIA.");
                        System.exit(0);
                    } else { // si elisabetha aun no esta lista seguimos esperando
                        System.out.println("   --> Elisabetha no está lista aún o no coincidieron en la Taberna.");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Funcion para gestionar las ofensas dadas a lance
    public static synchronized boolean resolverDuelo() {
        System.out.println("⚔️ ¡" + NOMBRE + " es provocado y inicia un DUELO!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 100% Gana osea siempre gana. pero un 20% Hiere gravemente a su contrincante.
        if (new Random().nextInt(100) < 20) { // si el numero esta por debajo de 20 hiere a su oponente y no gana limpiamente
            System.out.println("🩸 " + NOMBRE + " hiere a su oponente por error (-5 Chispa).");
            modificarChispa(-5);
            return true; // Hubo herida

        } else { // si el numero esta por encima de 20 gana de forma limpia y ejemplar a su oponente
            int aumento = 7;
            if (!conoceAElisabetha) { // Si no conoce a Elisabetha, la chispa máxima será 50

                aumento = 50 - chispa; // Si a la chispa le queda mucho para los 50 se aumenta la chispa en 7

                if (aumento > 7) { // Si a la chispa le queda poco para los 50 se aumenta la chispa en lo que le falta
                    aumento = 7;
                }
            }

            if (aumento > 0) { // Si la chispa es menor a 50 se aumenta la chispa
                System.out.println("🏆 " + NOMBRE + " gana limpiamente el duelo (+" + aumento + " Chispa).");
                modificarChispa(aumento);
            } else { // Si la chispa es mayor o igual a 50 se muestra el mensaje de maximo alcanzado
                System.out.println(
                        "🏆 " + NOMBRE + " gana limpiamente el duelo (Máximo 50 alcanzado sin conocer a Elisabetha).");
            }
            return false; // No hubo herida
        }
    }

    // Funcion que gesiona las guardias del porton
    private static void hacerGuardiaPorton() {
        System.out.println(NOMBRE + " hace guardia en el PORTÓN NORTE...");
        try (Socket socket = new Socket(HOST, PUERTO_PORTON); // Gestion de la comunicacion con el porton norte

                // Definimos variables de Entrada y salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            // 1. Recibimos datos de la carreta
            String origen = entrada.readUTF();
            String producto = entrada.readUTF();

            System.out.println("   --> Inspeccionando carreta de " + origen + " con " + producto);

            // 2. Decidimos si pasa
            boolean pasa = true;
            if (!origen.equals("Roedalia")) {
                if (producto.equals("Queso sin fermentar") || producto.equals("Leche cruda")) {
                    pasa = false;
                }
            }

            // 3. Enviamos veredicto
            salida.writeBoolean(pasa);
            System.out.println(pasa ? "¡Adelante!" : "¡Alto! Mercancía prohibida.");

            // Coste de vigilancia: 5 segundos
            Thread.sleep(5000);

        } catch (IOException | InterruptedException e) {
            System.out.println("El Portón Norte está cerrado o guardia interrumpida.");
        }
    }

    // Funcion para gestionar las visitas de lance a la tabera
    private static void hacerGuardiaTaberna() {
        System.out.println(NOMBRE + " entra a vigilar la TABERNA...");
        try (Socket socket = new Socket(HOST, PUERTO_TABERNA); // Gestion de la comunicacion con la taberna

                // Definimos variables de Entrada y salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {
            salida.writeInt(2); // ID Lance nos comunicamos con el id del personaje en este caso lance es ID 2
            salida.writeBoolean(false); // Visita normal no estamos a 100 de Chispa

            // Revisamos si ha habido encuentro entre lance y elisabetha
            boolean encuentro = entrada.readBoolean();

            if (encuentro) { // Si el encuentro se ha dado se hace lo de aqui
                System.out.println("¡" + NOMBRE + " HA VISTO A ELISABETHA!");
                if (!conoceAElisabetha) { // si no se habian visto antes y es su primer encuentro la chispa de lance
                                          // sube a 75
                    conoceAElisabetha = true;
                    chispa = 75; // Si ya no es la primeravez le da un chute de chispa pero menor solo de 10
                } else {
                    modificarChispa(10);
                }
            } else { // si no se encuentran no pasa nada simplemente se va
                System.out.println("Elisabetha no estaba hoy...");
            }

        } catch (IOException e) {
            System.out.println("La Taberna está cerrada.");
        }
    }

    // Funciona para gestionar el FINAL FELIZ
    private static boolean intentarFinalFeliz() {
        try (Socket socket = new Socket(HOST, PUERTO_TABERNA); // Abrimos conexion con la taberna apra realizar acciones

                // Definimos variables de Entrada y salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            salida.writeInt(2); // ID Lance nos comunicamos con el id del personaje en este caso lance es ID 2
            salida.writeBoolean(true); // Esta listo para su final feliz y esta a 100 de chispa

            // Revisamos si ha habido encuentro entre lance y elisabetha
            boolean encuentro = entrada.readBoolean();
            return encuentro; // si es true es que lance esta listo, sino es que aun no esta preparado

        } catch (IOException e) {
            System.out.println("Taberna cerrada esperando el final...");
            return false;
        }
    }

    // Funcion para atender a los compañeros de lance
    private static void atenderCompañeros() throws InterruptedException {
        Random random = new Random(); // Definimos el random
        java.util.List<String> copiaCompas; // Definimos un contenedor para almacenar los nombres de los compañeros como
                                            // copia parara usarse en la funcion

        synchronized (compañerosConocidos) { // Revisamos a los compañeros y las vamos almacenando en la variable
            if (compañerosConocidos.isEmpty()) {
                System.out.println("🤷‍♂️ " + NOMBRE + " aún no ha hablado con ningún compañero hoy.");
                Thread.sleep(2000);
                return;
            }
            copiaCompas = new java.util.ArrayList<>(compañerosConocidos);
        }

        // Por defecto los compañeros no han sido atendidos
        boolean atendido = false;

        // Bucle principal Mientras queden compañeros en la lista y aun no se han
        // atendido a todos no se cierra
        while (!copiaCompas.isEmpty() && !atendido) {
            int indice = random.nextInt(copiaCompas.size()); // hacemos un random de entre todos los compañerps de mi
                                                             // lista
            String compaElegido = copiaCompas.get(indice); // variable con el compañero que hemos elegido

            System.out.println("🗣️ " + NOMBRE + " decide hablar con " + compaElegido + "...");
            Thread.sleep(4000); // 4 segundos de charla

            // Revisamos nuestro buzon y nos traemos la entrada del compañero seleccionado
            // de la lista
            if (buzonCompañeros.containsKey(compaElegido)) {
                buzonCompañeros.remove(compaElegido);
                System.out.println("   --> " + compaElegido + " le cuenta una confidencia sobre la guardia.");
                atendido = true;
            } else {
                System.out.println("   --> " + compaElegido + " no tiene novedades.");
                copiaCompas.remove(indice);
            }
        }
    }

    // Funcion para modificar el nivel de la chispa
    public static synchronized void modificarChispa(int cantidad) {
        // Una vez que el amor alcanza el 100%, nada puede enfriarlo (ya no baja)
        if (chispa >= 100 && cantidad < 0) {
            return;
        }

        chispa += cantidad;

        if (chispa < 0) // Freno para que la chispa no sea negativa
            chispa = 0;

        if (chispa > 100) // Freno para que la chispa no supere el máximo
            chispa = 100;
    }
}