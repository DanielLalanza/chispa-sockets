import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Elisabetha {

    // Declaamos las variables con la informacion del servidor Elisabetha
    private static final String NOMBRE = "Elisabetha";
    private static final String HOST = "localhost";
    public static final int PUERTO_ELI = 5000;
    private static final int PUERTO_TABERNA = 5002;
    private static final int PUERTO_MERCADO = 5003;

    // Variable Compartida para diferetes usos
    public static volatile int chispa = 0;
    public static boolean conoceALance = false;
    public static volatile boolean tieneInvitacion = false;

    // Lista para que elisabetha sepa quienes son sus damas
    public static final java.util.List<String> damasConocidas = new java.util.ArrayList<>();

    // Buzón para guardar mensajes de las damas. Un Map no es un simple array,
    // funciona como una "matriz" o tabla de dos columnas: LLAVE (Nombre) y VALOR
    // (Datos).
    // Ejemplo: ["Dama del Lazo", [tipo: 1, daño: 15]] -> Al buscar por nombre,
    // obtenemos sus datos asociados.
    public static final java.util.Map<String, int[]> buzonDamas = new java.util.concurrent.ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 1. ARRANCAR EL SERVIDOR EN SEGUNDO PLANO
        HiloServidorElisabetha servidor = new HiloServidorElisabetha();
        servidor.start();
        System.out.println("🌹 " + NOMBRE + " LISTA (Chispa: " + chispa + ")");

        // 2. BUCLE DEL CLIENTE
        Random random = new Random(); // Nos creamos un Random para futuros usos
        try {
            while (true) {
                if (chispa < 100) { // Si la chispa baja de 100, seguimos con la vida normal
                    Thread.sleep(3000);
                    int accion = random.nextInt(4); // Realizamos una accion y la establecemos mediante un random

                    switch (accion) {
                        case 0: // ACCION ATENDER DAMAS
                            atenderDamas();
                            break;
                        case 1: // ACCION ASISTIR A BAILES
                            if (tieneInvitacion) {
                                gestionarInvitacionBaile();
                                tieneInvitacion = false;
                            } else {
                                System.out.println("🤷‍♀️ " + NOMBRE + " no tiene invitaciones pendientes.");
                            }
                            break;
                        case 2: // ACCION LEER PERGAMINOS
                            leerPergaminos();
                            break;
                        case 3: // ACCION ESCAPARSE A LUGARES
                            int lugar = random.nextInt(2);
                            if (lugar == 0)
                                visitarMercado();
                            else
                                visitarTaberna();
                            break;
                    }
                    System.out.println("   --> Nivel actual de Chispa (Eli): " + chispa + "/100");

                } else { // FASE FINAL: Solo se ejecuta si la chispa llega al maximo
                    System.out.println("¡" + NOMBRE + " TIENE LA CHISPA AL MÁXIMO! Esperando a Lance...");
                    Thread.sleep(2000);
                    System.out.println("❤️ " + NOMBRE + " acude a la Taberna buscando su final feliz...");

                    if (intentarFinalFeliz()) { // si recibe el aviso que lance esta listo para su final feliz se
                                                // encuientran los dos
                        System.out.println(
                                "🎉🎉🎉 ¡ELISABETHA Y LANCE SE HAN ENCONTRADO CON LA CHISPA AL MÁXIMO! 🎉🎉🎉");
                        System.out.println("FIN DE LA GRAN SIMULACIÓN DEL CANTAR DE LA LEYENDA DE ROEDALIA.");
                        System.exit(0);
                    } else { // si lance aun no esta listo seguimos esperando
                        System.out.println("   --> Lance no está listo aún o no coincidieron en la Taberna.");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Funcion Para gestionar las invitaciones al baile
    private static void gestionarInvitacionBaile() throws InterruptedException {
        // Elisabetha lee petición. 90% esquiva, 10% inevitable.
        System.out.println("✉️ " + NOMBRE + " revisa una invitación al baile...");
        if (new Random().nextInt(100) < 10) { // Hacemos un random para que si el numero esta por debajo de 10 se vuelva
                                              // inevitable asistir
            System.out.println("👗 ¡Inevitable! " + NOMBRE + " debe asistir al baile. (Chispa -3)");
            Thread.sleep(5000);
            modificarChispa(-3);

        } else { // si el numero es mayor a 10 encuentra la manera de esquivar la invitacion
            System.out.println("🏃‍♀️ " + NOMBRE + " logra esquivar la invitación con una excusa.");
        }
    }

    // Funcion para gestionar la lectura de pergaminos
    private static void leerPergaminos() throws InterruptedException {
        System.out.println("📜 Leyendo pergaminos en la biblioteca...");
        Thread.sleep(5000); // 5s

        // 60% de encontrar una historia inspiradora vs 40% de pergaminos aburridos
        if (new Random().nextInt(100) < 60) {
            int aumento = 8;
            if (!conoceALance) { // Si no conoce a Lance

                aumento = 30 - chispa; // Si no conoce a Lance, la chispa máxima será 30

                if (aumento > 8) { // Si a la chispa le queda mucho para los 30 se aumenta la chispa en 8
                    aumento = 8;
                }
            }

            if (aumento > 0) { // Si la chispa es menor a 30 se aumenta la chispa
                System.out.println("   --> ¡Una historia de valientes caballeros! (+" + aumento + " chispa)");
                modificarChispa(aumento);
            } else { // Si la chispa es mayor o igual a 30 se muestra el mensaje de maximo alcanzado
                System.out.println(
                        "   --> ¡Una historia de valientes caballeros! (Máximo 30 alcanzado sin conocer a Lance)");
            }
        } else { // Si el random es falso se resta chispa
            System.out.println("   --> Pergaminos soporíferos sobre genealogía... (-5 chispa)");
            modificarChispa(-5);
        }
    }

    // Funcion para gestionar la visita al mercado
    private static void visitarMercado() {
        System.out.println("🛒 " + NOMBRE + " se escapa al Mercado...");
        try (Socket socket = new Socket(HOST, PUERTO_MERCADO); // Abrimos conexion con el mercado apra realizar acciones

                // Definimos variables de Entrada y salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            // Revisamos el catalogo de de productos nos dan 5 productos
            for (int i = 0; i < 5; i++) {
                entrada.readUTF();
            }

            // Tras tener los 5 productos comparamos 1 de ellos
            Thread.sleep(5000);
            salida.writeInt(new Random().nextInt(5)); // Elegimos uno al azar
            System.out.println("   --> Compra realizada con éxito.");

        } catch (Exception e) {
            System.out.println("El Mercado está cerrado.");
        }
    }

    // Funcion para gestionar las visitas a la taberna
    private static void visitarTaberna() {
        System.out.println("🍺 " + NOMBRE + " se escapa a la Taberna El Descanso del Guerrero...");
        try (Socket socket = new Socket(HOST, PUERTO_TABERNA); // Abrimos conexion con la taberna apra realizar acciones

                // Definimos variables de Entrada y salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {
            salida.writeInt(1); // ID Elisabetha nos comunicamos con el id del personaje en este caso elisabetha
                                // es ID 1
            salida.writeBoolean(false); // Visita normal no estamos a 100 de Chispa

            // Revisamos si ha habido encuentro entre lance y elisabetha
            boolean encuentro = entrada.readBoolean();

            if (encuentro) { // Si el encuentro se ha dado se hace lo de aqui
                System.out.println("¡" + NOMBRE + " HA ENCONTRADO A LANCE!");
                if (!conoceALance) { // si no se habian visto antes y es su primer encuentro la chispa de elisabetha
                                     // sube a 75
                    conoceALance = true;
                    chispa = 75;
                } else { // Si ya no es la primeravez le da un chute de chispa pero mayor de 15
                    modificarChispa(15);
                }
            } else { // si no se encuentran no pasa nada simplemente se va
                System.out.println(NOMBRE + " no estaba hoy...");
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
            salida.writeInt(1); // ID Elisabetha nos comunicamos con el id del personaje en este caso elisabetha
                                // es ID 1
            salida.writeBoolean(true); // Esta lista para su final feliz y esta a 100 de chispa

            // Revisamos si ha habido encuentro entre lance y elisabetha
            boolean encuentro = entrada.readBoolean();
            return encuentro; // si es true es que lance esta listo, sino es que aun no esta preparado

        } catch (IOException e) {
            System.out.println("Taberna cerrada esperando el final...");
            return false;
        }
    }

    // Funcion para gestionar la atencion a las damas
    private static void atenderDamas() throws InterruptedException {
        Random random = new Random(); // Definimos el random
        java.util.List<String> copiaDamas; // Definimos un contenedor para almacenar los nombres de las damas como copia
                                           // parara usarse en la funcion

        synchronized (damasConocidas) { // Revisamos a las damas y las vamos almacenando en la variable
            if (damasConocidas.isEmpty()) {
                System.out.println("🤷‍♀️ " + NOMBRE + " aún no conoce a ninguna de sus Damas del Lazo.");
                Thread.sleep(2000);
                return;
            }
            copiaDamas = new java.util.ArrayList<>(damasConocidas);
        }

        // Por defecto las damas no han sido atendidas
        boolean atendida = false;

        // Bucle principal Mientras queden damas en la lista y aun no se han atendido a
        // todas no se cierra
        while (!copiaDamas.isEmpty() && !atendida) {
            int indice = random.nextInt(copiaDamas.size()); // hacemos un random de entre todas las damas de mi lista
            String damaSeleccionada = copiaDamas.get(indice); // variable con la dama que hemos elegido

            System.out.println("🌹 " + NOMBRE + " elige atender a " + damaSeleccionada + "...");
            Thread.sleep(4000); // 4 segundos de atención

            // Revisamos nuestro buzon y nos traemos la entrada de la dama seleccionada de
            // la lista
            if (buzonDamas.containsKey(damaSeleccionada)) {
                int[] mensaje = buzonDamas.remove(damaSeleccionada); // revisamos el mensaje y vemos de que tipo es
                int tipo = mensaje[0];
                int daño = mensaje[1];

                if (tipo == 1) { // RUMOR o CONFIDENCIA
                    if (daño > 0) {
                        System.out.println("👂 " + damaSeleccionada + " le cuenta un rumor infundado: Chispa -" + daño);
                        modificarChispa(-daño);
                    } else {
                        System.out.println(
                                "👂 " + damaSeleccionada + " le cuenta una confidencia personal (sin efecto).");
                    }
                } else if (tipo == 3) { // INVITACIÓN
                    System.out.println("💌 " + damaSeleccionada + " le entrega una invitación al baile.");
                    tieneInvitacion = true;
                }
                atendida = true;
            } else {
                System.out.println("   --> " + damaSeleccionada + " no tiene nada que contarle.");
                copiaDamas.remove(indice); // Elegir otra aleatoriamente
            }
        }

        if (!atendida) {
            System.out.println("🤷‍♀️ Ninguna de las damas tenía novedades hoy.");
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
