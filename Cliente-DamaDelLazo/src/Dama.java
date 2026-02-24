import java.util.Scanner;

public class Dama {

    public static void main(String[] args) {
        // inicializamos el scanner para leer el teclado
        Scanner sc = new Scanner(System.in);


        System.out.println("=== CONFIGURACIÓN DE DAMAS DEL LAZO ===");
        // Configuración de red (global para todos los hilos)
        System.out.print("IP del servidor (Elisabetha) ej: localhost: ");
        String ipEli = sc.nextLine();
        // Configuración de red (global para todos los hilos)
        System.out.print("Puerto del servidor (Elisabetha) ej: 5000: ");
        int puertoEli = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // preguntamos el numero de personajes
        System.out.print("Introduce el número de personajes (damas) a crear: ");
        int numPersonajes = sc.nextInt();
        sc.nextLine(); // limpiamos el scanner


        // Inicializamos los hinos en funcion de los personajes solicitados y almacenamos en array
        HiloDama[] hilos = new HiloDama[numPersonajes];


        // Le damos nombres a cada personaje y los guardamos
        for (int i = 0; i < numPersonajes; i++) {
            System.out.println("\n--- Dama " + (i + 1) + " ---");
            System.out.print("Nombre del personaje: ");
            String nombre = sc.nextLine();


            // aprobechamos  le asiganmos a cada personaje su info osea nombre puerto etc...
            hilos[i] = new HiloDama(nombre, ipEli, puertoEli);
        }


        // los ponemos a los alquimistas a chambear
        System.out.println("\n🚀 Iniciando todas las damas...");
        for (int i = 0; i < numPersonajes; i++) {
            hilos[i].start();
        }

        System.out.println("✅ Todos los hilos de damas han sido lanzados.");
    }
}