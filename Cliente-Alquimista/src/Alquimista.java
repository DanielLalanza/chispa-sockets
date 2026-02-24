import java.util.Scanner;

public class Alquimista {

    public static void main(String[] args) {
        // inicializamos el scanner para leer el teclado
        Scanner sc = new Scanner(System.in);


        System.out.println("=== CONFIGURACIÓN DE ALQUIMISTAS ===");
        // Configuración de red (global para todos los hilos)
        System.out.print("IP del servidor (ej: localhost): ");
        String host = sc.nextLine();
        // Asignamos el puerto de conexion
        System.out.print("Puerto Alacena (ej: 5005): ");
        int puertoAlacena = sc.nextInt();
        // si hay mas de 1 puesto los asignamos todos
        System.out.print("Puerto Elisabetha (ej: 5000): ");
        int puertoEli = sc.nextInt();
        // ...
        System.out.print("Puerto Lance (ej: 5001): ");
        int puertoLance = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // preguntamos el numero de personajes
        System.out.print("Introduce el número de personajes (alquimistas) a crear: ");
        int numPersonajes = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // Inicializamos los hinos en funcion de los personajes solicitados y almacenamos en array
        HiloAlquimista[] hilos = new HiloAlquimista[numPersonajes];


        // Le damos nombres a cada personaje y los guardamos
        for (int i = 0; i < numPersonajes; i++) {
            System.out.println("\n--- Alquimista " + (i + 1) + " ---");
            System.out.print("Nombre del personaje: ");
            String nombre = sc.nextLine();


            // aprobechamos  le asiganmos a cada personaje su info osea nombre puerto etc...
            hilos[i] = new HiloAlquimista(nombre, host, puertoAlacena, puertoEli, puertoLance);
        }


        // los ponemos a los alquimistas a chambear
        System.out.println("\n🚀 Iniciando todos los alquimistas...");
        for (int i = 0; i < numPersonajes; i++) {
            hilos[i].start();
        }


        System.out.println("✅ Todos los hilos han sido lanzados.");
    }
}