import java.util.Scanner;

public class Caballero {

    public static void main(String[] args) {
        // inicializamos el scanner para leer el teclado
        Scanner sc = new Scanner(System.in);


        System.out.println("=== CONFIGURACIÓN DE CABALLEROS DEL PORTÓN ===");
        // Configuración de red (global para todos los hilos)
        System.out.print("IP del servidor (Lance) ej: localhost: ");
        String ipLance = sc.nextLine();
        // Asignamos el puerto de conexion
        System.out.print("Puerto del servidor (Lance) ej: 5001: ");
        int puertoLance = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // preguntamos el numero de personajes
        System.out.print("Introduce el número de personajes (caballeros) a crear: ");
        int numPersonajes = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // Inicializamos los hinos en funcion de los personajes solicitados y almacenamos en array
        HiloCaballero[] hilos = new HiloCaballero[numPersonajes];


        // Le damos nombres a cada personaje y los guardamos
        for (int i = 0; i < numPersonajes; i++) {
            System.out.println("\n--- Caballero " + (i + 1) + " ---");
            System.out.print("Nombre del personaje: ");
            String nombre = sc.nextLine();


            // aprobechamos  le asiganmos a cada personaje su info osea nombre puerto etc...
            hilos[i] = new HiloCaballero(nombre, ipLance, puertoLance);
        }


        // los ponemos a los alquimistas a chambear
        System.out.println("\n🚀 Iniciando todos los caballeros...");
        for (int i = 0; i < numPersonajes; i++) {
            hilos[i].start();
        }


        System.out.println("✅ Todos los hilos de caballeros han sido lanzados.");
    }
}
