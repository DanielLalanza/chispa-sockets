# 📖 LA BIBLIA DEL PROYECTO: "La Chispa Adecuada"
## Guía Completa Paso a Paso — De Cero a Héroe

> **¿Para quién es esta guía?** Para cualquier persona que no sepa NADA de programación con sockets ni hilos. Aquí se explica TODO desde el principio, paso a paso, como si fuera la primera vez que abres Java.

---

## 📑 ÍNDICE

1. [¿Qué es este proyecto?](#1--qué-es-este-proyecto)
2. [Requisitos previos](#2--requisitos-previos)
3. [Conceptos teóricos fundamentales](#3--conceptos-teóricos-fundamentales)
   - [¿Qué es un Socket?](#31--qué-es-un-socket)
   - [¿Qué es un Hilo (Thread)?](#32--qué-es-un-hilo-thread)
   - [¿Qué es Cliente-Servidor?](#33--qué-es-cliente-servidor)
   - [¿Qué es `synchronized` y `volatile`?](#34--qué-es-synchronized-y-volatile)
4. [Estructura del Proyecto](#4--estructura-del-proyecto)
5. [Mapa de Puertos y Conexiones](#5--mapa-de-puertos-y-conexiones)
6. [Los Servidores — Explicados uno a uno](#6--los-servidores--explicados-uno-a-uno)
   - [Servidor Taberna](#61-servidor-taberna-puerto-5002)
   - [Servidor Mercado](#62-servidor-mercado-puerto-5003)
   - [Servidor Portón Norte](#63-servidor-portón-norte-puerto-5004)
   - [Servidor Alacena de Pociones](#64-servidor-alacena-de-pociones-puerto-5005)
7. [Los Protagonistas — Elisabetha y Lance](#7--los-protagonistas--elisabetha-y-lance)
   - [Elisabetha](#71-elisabetha-cliente-servidor-puerto-5000)
   - [Lance](#72-lance-cliente-servidor-puerto-5001)
8. [Los Personajes Secundarios (Clientes)](#8--los-personajes-secundarios-clientes)
   - [Las Damas del Lazo](#81-las-damas-del-lazo)
   - [Los Caballeros del Portón](#82-los-caballeros-del-portón)
   - [Los Alquimistas](#83-los-alquimistas)
9. [Mecánica de la Chispa](#9--mecánica-de-la-chispa)
10. [Cómo Compilar y Ejecutar](#10--cómo-compilar-y-ejecutar)
11. [Flujo Completo de la Simulación](#11--flujo-completo-de-la-simulación)
12. [Diagrama UML](#12--diagrama-uml)
13. [Preguntas Frecuentes](#13--preguntas-frecuentes)
14. [Guía: Crear Nuevos Personajes y Lugares](#14--guía-crear-nuevos-personajes-y-lugares)
    - [Crear un nuevo personaje secundario (Cliente)](#141-crear-un-nuevo-personaje-secundario-cliente)
    - [Crear un nuevo lugar (Servidor)](#142-crear-un-nuevo-lugar-servidor)
    - [Integrar el nuevo personaje con un protagonista](#143-integrar-el-nuevo-personaje-con-un-protagonista)
    - [Integrar el nuevo lugar con un protagonista](#144-integrar-el-nuevo-lugar-con-un-protagonista)
    - [Añadirlo al script de lanzamiento](#145-añadirlo-al-script-de-lanzamiento)

---

## 1. 🎯 ¿Qué es este proyecto?

Este proyecto es una **simulación distribuida** escrita en **Java** que recrea la historia de amor entre dos ratones: **Elisabetha** (una princesa) y **Lance** (un caballero). 

La simulación funciona así:
- Hay **9 programas Java independientes** que se ejecutan al mismo tiempo.
- Cada programa representa un personaje o un lugar del reino de Roedalia.
- Los programas se comunican entre sí usando **Sockets TCP** (como si se mandaran mensajes por red).
- Cada personaje tiene su propio **hilo de ejecución** (Thread) para que todos actúen al mismo tiempo.
- El objetivo es que Elisabetha y Lance acumulen **100 puntos de "chispa"** (su nivel de amor) y se encuentren en la Taberna para el **Final Feliz**.

### La historia en una línea:
> Elisabetha y Lance se conocen en una taberna, se enamoran, pero los alquimistas malvados, las damas chismosas y los caballeros bravucones intentarán impedir que su amor triunfe. Si ambos llegan a 100 de chispa y se encuentran → **Final Feliz** 🎉

---

## 2. 🛠 Requisitos previos

| Herramienta | Versión mínima | ¿Para qué? |
|---|---|---|
| **Java JDK** | 8 o superior | Compilar y ejecutar los archivos `.java` |
| **IntelliJ IDEA** | Cualquiera (Community vale) | IDE recomendado para abrir el proyecto |
| **Windows** | 10/11 | El script `.bat` está diseñado para Windows |

### Verificar que tienes Java instalado:
```bash
java -version
javac -version
```
Si ambos comandos muestran una versión, estás listo.

---

## 3. 📚 Conceptos teóricos fundamentales

### 3.1. 🔌 ¿Qué es un Socket?

Imagina que quieres hablar por teléfono con alguien:
1. **Tú** (el cliente) marcas un número de teléfono.
2. **La otra persona** (el servidor) tiene el teléfono encendido esperando llamadas.
3. Cuando conectáis, podéis hablar (enviar y recibir datos).

Un **Socket** es exactamente eso pero en programación. Es un "enchufe" virtual que conecta dos programas para que puedan enviarse datos.

```
┌──────────────┐                          ┌──────────────┐
│   CLIENTE    │  ───── Socket TCP ─────▶ │   SERVIDOR   │
│ (el que llama)│                          │ (el que espera)│
└──────────────┘                          └──────────────┘
```

#### En Java, un Socket se usa así:

**El servidor** (el que espera):
```java
// 1. Abro un "teléfono" en el puerto 5002
ServerSocket server = new ServerSocket(5002);

// 2. Me quedo esperando a que alguien llame
Socket socket = server.accept();  // Esto BLOQUEA hasta que alguien conecte

// 3. Creo canales para leer y escribir datos
DataInputStream entrada = new DataInputStream(socket.getInputStream());
DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

// 4. Leo lo que me manda el cliente
String mensaje = entrada.readUTF();

// 5. Le respondo
salida.writeUTF("Recibido!");

// 6. Cierro la conexión
socket.close();
```

**El cliente** (el que llama):
```java
// 1. Me conecto al servidor (IP + Puerto)
Socket socket = new Socket("localhost", 5002);

// 2. Creo canales para leer y escribir datos
DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
DataInputStream entrada = new DataInputStream(socket.getInputStream());

// 3. Le mando un mensaje al servidor
salida.writeUTF("Hola servidor!");

// 4. Leo su respuesta
String respuesta = entrada.readUTF();

// 5. Cierro la conexión
socket.close();
```

> **📌 CLAVE**: `"localhost"` significa "mi propio ordenador". Si quisieras conectar con otro PC, pondrías su IP (ej: `"192.168.1.100"`).

#### Tipos de datos que se envían por Socket:

| Método de envío | Método de lectura | Tipo de dato |
|---|---|---|
| `salida.writeUTF("texto")` | `entrada.readUTF()` | Texto (String) |
| `salida.writeInt(42)` | `entrada.readInt()` | Número entero |
| `salida.writeBoolean(true)` | `entrada.readBoolean()` | Verdadero/Falso |

> **⚠️ REGLA DE ORO**: El orden en que **escribes** datos debe ser el MISMO orden en que los **lees**. Si el cliente envía primero un `writeInt` y luego un `writeUTF`, el servidor debe hacer primero `readInt` y luego `readUTF`.

---

### 3.2. 🧵 ¿Qué es un Hilo (Thread)?

Normalmente, un programa hace las cosas **una detrás de otra** (secuencial). Un **hilo** permite que un programa haga **varias cosas a la vez** (en paralelo).

```
SIN HILOS (Secuencial):
  Tarea A ──────▶ Tarea B ──────▶ Tarea C

CON HILOS (Paralelo):
  Tarea A ──────────────▶
  Tarea B ──────────────▶  (las 3 a la vez)
  Tarea C ──────────────▶
```

#### ¿Cómo se crea un hilo en Java?

```java
// 1. Creamos una clase que EXTIENDE de Thread
public class MiHilo extends Thread {

    // 2. Sobrescribimos el método run() → aquí va el código que se ejecutará en paralelo
    @Override
    public void run() {
        System.out.println("¡Soy un hilo ejecutándose en paralelo!");
        // Aquí va todo el trabajo del hilo
    }
}

// 3. En el main, creamos una instancia y llamamos a start()
public class Main {
    public static void main(String[] args) {
        MiHilo hilo = new MiHilo();
        hilo.start();  // ¡OJO! Se llama a start(), NO a run()
        
        System.out.println("El main sigue ejecutándose a la vez");
    }
}
```

> **⚠️ IMPORTANTE**: Se llama a `.start()`, NUNCA a `.run()`. Si llamas a `.run()` directamente, NO crea un hilo nuevo, simplemente ejecuta el código en el hilo actual (como una función normal).

#### `Thread.sleep()` — Pausar un hilo

```java
Thread.sleep(5000);  // Pausa el hilo actual durante 5000 milisegundos (5 segundos)
```

Esto se usa en la simulación para representar el "tiempo" que cuesta cada acción (vigilar, estudiar, etc.).

---

### 3.3. 🔄 ¿Qué es Cliente-Servidor?

Es un **patrón de comunicación** entre programas:

| Rol | Descripción | Analogía |
|---|---|---|
| **Servidor** | Se queda esperando conexiones. Cuando alguien conecta, le atiende. | Una tienda que abre y espera clientes |
| **Cliente** | Se conecta al servidor cuando necesita algo. | El cliente que entra a la tienda |

En nuestro proyecto hay **tres tipos de roles**:

```
╔══════════════════════════════════════════════════════════════╗
║  SERVIDORES PUROS (solo esperan y atienden):                ║
║    • Taberna (puerto 5002)                                  ║
║    • Mercado (puerto 5003)                                  ║
║    • Portón Norte (puerto 5004)                             ║
║    • Alacena de Pociones (puerto 5005)                      ║
║                                                              ║
║  CLIENTES-SERVIDORES (hacen las dos cosas a la vez):        ║
║    • Elisabetha (servidor en puerto 5000 + cliente)         ║
║    • Lance (servidor en puerto 5001 + cliente)              ║
║                                                              ║
║  CLIENTES PUROS (solo se conectan a otros):                 ║
║    • Damas del Lazo → se conectan a Elisabetha (5000)       ║
║    • Caballeros del Portón → se conectan a Lance (5001)     ║
║    • Alquimistas → se conectan a varios puertos             ║
╚══════════════════════════════════════════════════════════════╝
```

---

### 3.4. 🔒 ¿Qué es `synchronized` y `volatile`?

Cuando varios hilos **leen y escriben la misma variable** al mismo tiempo, puede haber problemas. Imagina que dos hilos intentan sumar al mismo contador a la vez:

```
Hilo A lee chispa = 50
Hilo B lee chispa = 50  (¡aún no se ha sumado lo de A!)
Hilo A escribe chispa = 55
Hilo B escribe chispa = 57  (¡Se perdió la suma de A!)
```

#### `synchronized` — El cerrojo

`synchronized` es como un **cerrojo en una puerta**. Solo un hilo puede entrar a la vez:

```java
public static synchronized void modificarChispa(int cantidad) {
    chispa += cantidad;  // Solo UN hilo puede ejecutar esto a la vez
}
```

#### `volatile` — La alerta inmediata

`volatile` le dice a Java: "cada vez que un hilo cambie esta variable, que TODOS los demás hilos lo sepan inmediatamente":

```java
public static volatile int chispa = 0;  // Todos los hilos ven el valor actualizado
```

---

## 4. 📂 Estructura del Proyecto

```
010-LA-CHISPA-ADECUADA/
│
├── 📁 ServidorTaberna/              ← SERVIDOR: Lugar de encuentro de Eli y Lance
│   └── src/
│       ├── Taberna.java             ← Clase main (abre el puerto 5002)
│       └── HiloTaberna.java         ← Hilo que gestiona cada visita
│
├── 📁 ServidorMercado/              ← SERVIDOR: Tienda de productos
│   └── src/
│       ├── Mercado.java             ← Clase main (abre el puerto 5003)
│       └── HiloMercado.java         ← Hilo que ofrece 5 productos al visitante
│
├── 📁 ServidorPortonNorte/          ← SERVIDOR: Control de acceso de carretas
│   └── src/
│       ├── Porton.java              ← Clase main (abre el puerto 5004)
│       └── HiloPorton.java          ← Hilo que genera carretas aleatorias
│
├── 📁 ServidorAlacenaPociones/      ← SERVIDOR: Almacén de pociones
│   └── src/
│       ├── Alacena.java             ← Clase main (abre el puerto 5005)
│       └── HiloAlacena.java         ← Hilo que guarda/saca pociones
│
├── 📁 Cliente-Servidor-Elisabetha/  ← PROTAGONISTA: Princesa ratona
│   └── src/
│       ├── Elisabetha.java          ← Main + lógica del cliente (acciones)
│       └── HiloServidorElisabetha.java ← Servidor que recibe ataques/rumores
│
├── 📁 Cliente-Servidor-Lance/       ← PROTAGONISTA: Caballero ratón
│   └── src/
│       ├── Lance.java               ← Main + lógica del cliente (acciones)
│       └── HiloServidorLance.java   ← Servidor que recibe ofensas/pociones
│
├── 📁 Cliente-Alquimista/           ← ANTAGONISTA: Saboteadores
│   └── src/
│       ├── Alquimista.java          ← Main (configura y lanza hilos)
│       └── HiloAlquimista.java      ← Lógica de cada alquimista individual
│
├── 📁 Cliente-DamaDelLazo/          ← SECUNDARIO: Damas chismosas
│   └── src/
│       ├── Dama.java                ← Main (configura y lanza hilos)
│       └── HiloDama.java            ← Lógica de cada dama individual
│
├── 📁 Cliente-CaballeroDelPorton/   ← SECUNDARIO: Caballeros bravucones
│   └── src/
│       ├── Caballero.java           ← Main (configura y lanza hilos)
│       └── HiloCaballero.java       ← Lógica de cada caballero individual
│
├── INICIAR_SIMULACION.bat           ← Script para arrancar TODO automáticamente
├── LIMPIAR_CLASS.bat                ← Script para borrar archivos compilados
├── enunciado.txt                    ← Enunciado original del ejercicio
├── DOCUMENTACION.md                 ← Documentación técnica resumida
└── DIAGRAMA_UML.md                  ← Diagrama de clases del proyecto
```

### ¿Por qué hay DOS archivos Java en cada carpeta?

Cada módulo sigue el mismo patrón:

| Archivo | Función | Analogía |
|---|---|---|
| **Clase principal** (ej: `Taberna.java`) | Abre el "teléfono" (puerto) y espera llamadas | El edificio de la taberna |
| **Clase Hilo** (ej: `HiloTaberna.java`) | Gestiona CADA llamada individual en un hilo separado | El camarero que atiende a cada cliente |

```java
// Taberna.java (simplificado) — EL PATRÓN UNIVERSAL DE SERVIDOR
while (true) {
    Socket socket = server.accept();          // Espera una conexión
    new HiloTaberna(socket).start();          // Crea un hilo para atenderla
}
```

> **📌 Esto permite que el servidor atienda múltiples conexiones a la vez.** Si no usáramos hilos, el servidor quedaría "bloqueado" atendiendo a un cliente y no podría recibir a nadie más hasta que terminara.

---

## 5. 🗺 Mapa de Puertos y Conexiones

```
                    ┌─────────────────────────────────┐
                    │    SERVIDORES (siempre activos)  │
                    └─────────────────────────────────┘

   ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐
   │  TABERNA    │  │  MERCADO   │  │   PORTÓN   │  │  ALACENA   │
   │ Puerto 5002│  │ Puerto 5003│  │ Puerto 5004│  │ Puerto 5005│
   └──────┬─────┘  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘
          │               │               │               │
          │               │               │               │
  ┌───────┼───────────────┼───────────────┼───────────────┼────────┐
  │       ▼               ▼               │               │        │
  │  ╔═══════════╗  ╔═══════════╗         │               │        │
  │  ║ELISABETHA ║  ║   LANCE   ║         │               │        │
  │  ║Puerto 5000║  ║Puerto 5001║◀────────┘               │        │
  │  ╚═════╤═════╝  ╚═════╤═════╝                         │        │
  │        │               │                               │        │
  │        ▲               ▲                               │        │
  │        │               │                               │        │
  │   ┌────┴────┐    ┌─────┴─────┐    ┌──────────────┐    │        │
  │   │  DAMAS  │    │CABALLEROS │    │ ALQUIMISTAS  │────┘        │
  │   │(Clientes)│   │ (Clientes)│    │  (Clientes)  │             │
  │   └─────────┘    └───────────┘    └──────────────┘             │
  │                                          │                     │
  │                                          └─ Se conectan a ─────┘
  │                                             Eli (5000)
  │                                             Lance (5001)
  │                                             Alacena (5005)
  └────────────────────────────────────────────────────────────────┘
```

### Tabla resumen de conexiones:

| Quién se conecta | ¿A dónde? | Puerto | ¿Para qué? |
|---|---|---|---|
| Elisabetha | Taberna | 5002 | Visitar / buscar a Lance / Final Feliz |
| Elisabetha | Mercado | 5003 | Comprar productos |
| Lance | Taberna | 5002 | Vigilar / buscar a Elisabetha / Final Feliz |
| Lance | Portón Norte | 5004 | Inspeccionar carretas |
| Damas del Lazo | Elisabetha | 5000 | Enviar rumores, confidencias e invitaciones |
| Caballeros | Lance | 5001 | Enviar ofensas y confidencias |
| Alquimistas | Alacena | 5005 | Guardar y sacar pociones |
| Alquimistas | Elisabetha | 5000 | Atacar con pociones |
| Alquimistas | Lance | 5001 | Atacar con pociones o amenazar |

---

## 6. 🏰 Los Servidores — Explicados uno a uno

### 6.1. Servidor Taberna (Puerto 5002)

**Archivos**: `Taberna.java` + `HiloTaberna.java`

La taberna es el lugar **más importante** de toda la simulación. Es donde Elisabetha y Lance pueden encontrarse.

#### ¿Qué hace `Taberna.java`?

Solo tiene un `main` que hace esto:
```java
ServerSocket server = new ServerSocket(5002);  // Abre el puerto 5002
while (true) {
    Socket socket = server.accept();           // Espera visitantes
    new HiloTaberna(socket).start();           // Crea un hilo para cada uno
}
```

#### ¿Qué hace `HiloTaberna.java`? (La magia)

Tiene **4 variables compartidas** (static volatile) que todos los hilos pueden ver:
```java
private static volatile boolean elisabethaDentro = false;  // ¿Está Eli en la taberna?
private static volatile boolean elisabethaReady = false;    // ¿Eli tiene chispa=100?
private static volatile boolean lanceDentro = false;        // ¿Está Lance en la taberna?
private static volatile boolean lanceReady = false;         // ¿Lance tiene chispa=100?
```

**Flujo cuando alguien entra a la taberna:**

```
1. El visitante envía:  writeInt(id)       → 1=Elisabetha, 2=Lance
                        writeBoolean(ready) → true=buscando Final Feliz

2. El servidor registra que ha entrado (método entrar())

3. Durante 8 SEGUNDOS (16 comprobaciones × 500ms cada una):
   → El servidor comprueba una y otra vez si el OTRO está dentro también

4. Grace Period: si hubo encuentro, wait 15s extra para sincronizar

5. Devuelve al visitante: writeBoolean(encuentro) → true/false
   → true = "¡Sí, el otro está aquí! ¡CHISPA!"
   → false = "No, el otro no vino. Vuelta a casa."

6. Al salir, limpia las variables (método salir())
```

> **📌 ¿Por qué hace 16 comprobaciones?** Porque si Lance entra a la taberna 3 segundos DESPUÉS que Elisabetha, ella aún está dentro comprobando. Este "polling" permite que se detecten aunque no entren exactamente al mismo segundo.

---

### 6.2. Servidor Mercado (Puerto 5003)

**Archivos**: `Mercado.java` + `HiloMercado.java`

El mercado es **el servidor más sencillo**. Simplemente ofrece productos.

**Flujo:**
```
1. El visitante se conecta

2. El servidor le ofrece 5 productos aleatorios del inventario:
   ["Queso", "Pan recién horneado", "Especias", "Telas", 
    "Jugo de grosella", "Repelente de gatos", "Collares", "Cucharas de boj"]

3. El visitante elige uno (envía un número del 0 al 4)

4. El servidor dice "Gracias por su compra" y cierra
```

> **📌 Efecto en la chispa**: NINGUNO. Visitar el mercado no sube ni baja la chispa.

---

### 6.3. Servidor Portón Norte (Puerto 5004)

**Archivos**: `Porton.java` + `HiloPorton.java`

El portón genera **carretas aleatorias** que Lance debe inspeccionar.

**Flujo:**
```
1. Lance se conecta al portón

2. El servidor genera una carreta aleatoria:
   - Origen: "Roedalia" o "Tierras Lejanas"
   - Producto: "Trigo", "Madera", "Queso sin fermentar", "Leche cruda" o "Manzanas"

3. Envía al cliente: writeUTF(origen), writeUTF(producto)

4. Lance decide si la deja pasar:
   - Si es de Roedalia → SIEMPRE pasa
   - Si es de Tierras Lejanas Y lleva "Queso sin fermentar" o "Leche cruda" → RECHAZADA

5. Lance envía: writeBoolean(puedePasar)
```

> **📌 Efecto en la chispa**: NINGUNO. Vigilar el portón no cambia la chispa.

---

### 6.4. Servidor Alacena de Pociones (Puerto 5005)

**Archivos**: `Alacena.java` + `HiloAlacena.java`

La alacena es un **almacén compartido** de pociones. Los alquimistas **guardan** pociones aquí tras estudiar, y las **sacan** cuando quieren atacar.

**Variables compartidas:**
```java
private static int pocionesElisabetha = 0;  // Stock de pociones contra Eli
private static int pocionesLance = 0;       // Stock de pociones contra Lance
```

**Flujo:**
```
GUARDAR (acción=1):
   1. Alquimista envía: writeInt(1) + writeInt(tipo)  // tipo 1=para Eli, 2=para Lance
   2. El servidor incrementa el contador correspondiente

SACAR (acción=2):
   1. Alquimista envía: writeInt(2) + writeInt(tipo)
   2. El servidor comprueba si hay stock
   3. Responde: writeBoolean(hayStock) → true/false
   4. Si hay stock, decrementa el contador
```

> **📌 Los métodos `guardarPocion()` y `sacarPocion()` son `synchronized`** para que dos alquimistas no saquen la misma poción al mismo tiempo.

---

## 7. 👑 Los Protagonistas — Elisabetha y Lance

### 7.1. Elisabetha (Cliente-Servidor, Puerto 5000)

**Archivos**: `Elisabetha.java` + `HiloServidorElisabetha.java`

Elisabetha es un **nodo híbrido**: tiene un servidor propio (para recibir rumores de Damas y ataques de Alquimistas) y un cliente (para visitar lugares).

#### Atributos importantes:
```java
public static volatile int chispa = 0;           // Su nivel de amor (0-100)
public static boolean conoceALance = false;       // ¿Ya lo ha conocido?
public static volatile boolean tieneInvitacion = false;  // ¿Tiene invitación a baile?
public static final List<String> damasConocidas;  // Lista de damas que la han contactado
public static final Map<String, int[]> buzonDamas; // Buzón con mensajes pendientes
```

#### ¿Cómo arranca?

```java
// 1. Arranca su servidor interno en segundo plano (un hilo aparte)
HiloServidorElisabetha servidor = new HiloServidorElisabetha();
servidor.start();

// 2. Entra en el bucle principal de acciones
while (true) {
    if (chispa < 100) {
        // Elige acción aleatoria: 0, 1, 2 o 3
        int accion = random.nextInt(4);
        switch (accion) {
            case 0: atenderDamas();         break;
            case 1: gestionarInvitacionBaile(); break;  // solo si tiene invitación
            case 2: leerPergaminos();        break;
            case 3: visitarMercado() o visitarTaberna(); break;
        }
    } else {
        // ¡CHISPA A 100! → Ir a la Taberna a buscar el Final Feliz
        intentarFinalFeliz();
    }
}
```

#### Acciones explicadas:

| Acción | Duración | Efecto en Chispa |
|---|---|---|
| **Atender damas** | 4s | Depende del mensaje: rumor (-5), confidencia (0), invitación (nada directo) |
| **Asistir a baile** | 5s | -3 puntos (si no lo esquiva, 80% probabilidad de esquivar) |
| **Leer pergaminos** | 5s | 60% historias de caballeros (+8, max 30 sin conocer a Lance) / 40% soporíferos (-5) |
| **Visitar mercado** | 5s | Sin efecto |
| **Visitar taberna** | ~8s | Si coincide con Lance: 1ª vez → chispa=75 / Siguientes → +15 |

#### El servidor de Elisabetha (`HiloServidorElisabetha.java`):

Escucha en el puerto 5000 y recibe mensajes con este protocolo:
```
Datos recibidos: readUTF(nombre) + readInt(tipo) + readInt(daño)

Si tipo == 1  → Rumor/Confidencia de una Dama     → Guarda en buzonDamas
Si tipo == 2  → Poción/Ataque de un Alquimista     → Baja chispa directamente
Si tipo == 3  → Invitación al baile de una Dama    → Guarda en buzonDamas
```

---

### 7.2. Lance (Cliente-Servidor, Puerto 5001)

**Archivos**: `Lance.java` + `HiloServidorLance.java`

Lance funciona de forma análoga a Elisabetha pero con sus propias acciones.

#### Acciones:
```java
int accion = random.nextInt(3);  // 0, 1 o 2
switch (accion) {
    case 0: atenderCompañeros();    break;  // Hablar con caballeros
    case 1: hacerGuardiaPorton();   break;  // Inspeccionar carretas
    case 2: hacerGuardiaTaberna();  break;  // Vigilar la taberna
}
```

| Acción | Duración | Efecto en Chispa |
|---|---|---|
| **Hablar con compañeros** | 4s | Lee buzón, si hay ofensa → duelo automático |
| **Guardia en Portón** | 5s | Sin efecto (inspecciona carretas) |
| **Guardia en Taberna** | ~8s | Si coincide con Eli: 1ª vez → chispa=75 / Siguientes → +10 |
| **Duelo** (provocado) | 5s | 80% gana limpio (+7, max 50 sin conocer Eli) / 20% hiere (-5) |

#### El servidor de Lance (`HiloServidorLance.java`):

Escucha en puerto 5001:
```
Datos recibidos: readUTF(nombre) + readInt(tipo) + readInt(daño)

Si tipo == 1 y daño > 0  → Ofensa de Caballero → DUELO y devuelve writeBoolean(herida)
Si tipo == 1 y daño == 0  → Confidencia         → Se guarda en buzón
Si tipo == 2              → Ataque/Amenaza de Alquimista → Baja chispa directamente
```

---

## 8. 🎭 Los Personajes Secundarios (Clientes)

### 8.1. Las Damas del Lazo

**Archivos**: `Dama.java` + `HiloDama.java`

Las damas son **clientes puros** que se conectan al servidor de Elisabetha (puerto 5000).

**Configuración al iniciar**: El `main` de `Dama.java` pide por consola:
- IP de Elisabetha (`localhost`)
- Puerto de Elisabetha (`5000`)
- Número de damas a crear
- Nombre de cada dama

**Comportamiento de cada dama** (50/50):
1. **Labores propias** (50%): Montar a caballo / Practicar esgrima / Enterarse de rumores → 5s, sin efecto
2. **Contactar a Elisabetha** (50%): Intenta conectarse durante **20 segundos** máximo. Si lo consigue, envía:
   - 10% → Invitación al baile (tipo=3)
   - 20% → Rumor infundado sobre Lance (tipo=1, daño=5)
   - 70% → Confidencia personal (tipo=1, daño=0)

**Protocolo de envío:**
```java
salida.writeUTF(nombre);   // Nombre de la dama
salida.writeInt(tipo);     // 1=Rumor/Confidencia, 3=Invitación
salida.writeInt(daño);     // 5 si es rumor, 0 si es confidencia/invitación
```

---

### 8.2. Los Caballeros del Portón

**Archivos**: `Caballero.java` + `HiloCaballero.java`

Los caballeros se conectan al servidor de Lance (puerto 5001).

**Comportamiento** (50/50):
1. **Vigilancia** (50%): Vigilan Portón Norte / Muralla / Torres → 6s, sin efecto
2. **Hablar con Lance** (50%): Intenta contactar durante **25 segundos** máximo. Si lo consigue:
   - 25% → Ofensa (tipo=1, daño=5) → Lance los reta a duelo. Si Lance los hiere → 30s de recuperación
   - 75% → Confidencia (tipo=1, daño=0)

---

### 8.3. Los Alquimistas

**Archivos**: `Alquimista.java` + `HiloAlquimista.java`

Los alquimistas son los **más complejos** de los secundarios. Se conectan a **tres sitios**: Alacena (5005), Elisabetha (5000) y Lance (5001).

**Comportamiento:**
```
60% → Estudiar calderos (30 segundos)
        ├── 30% → Crea poción para Eli → la guarda en Alacena
        ├── 30% → Crea poción para Lance → la guarda en Alacena
        └── 40% → Fracasa

20% → Visitar a Elisabetha (5 segundos)
        └── Necesita poción de la Alacena
        └── 15% de engañarla → ataca al puerto 5000 (daño=10)

20% → Visitar a Lance (7 segundos)
        └── Necesita poción de la Alacena
        ├── 80% intenta engañar → 20% éxito → ataca al puerto 5001 (daño=20)
        └── 20% amenaza → 20% éxito → ataca al puerto 5001 (daño=30)
```

---

## 9. ❤️ Mecánica de la Chispa

La "chispa" es el **corazón del juego**. Cada protagonista tiene un nivel de chispa (0-100):

### Tabla completa de modificadores de chispa:

#### Para Elisabetha:
| Evento | Cambio | Límite |
|---|---|---|
| Conocer a Lance en Taberna (1ª vez) | = 75 | - |
| Reencontrarse con Lance en Taberna | +15 | 100 |
| Leer historias de caballeros | +8 | 30 (sin conocer a Lance) / 100 (conociéndolo) |
| Leer pergaminos soporíferos | -5 | 0 (min) |
| Asistir a baile obligatorio | -3 | 0 (min) |
| Rumor de una Dama | -5 | 0 (min) |
| Poción de Alquimista | -10 | 0 (min) |
| **Chispa ≥ 100** | **No puede bajar** | **INMORTAL** |

#### Para Lance:
| Evento | Cambio | Límite |
|---|---|---|
| Conocer a Elisabetha en Taberna (1ª vez) | = 75 | - |
| Reencontrarse con Elisabetha en Taberna | +10 | 100 |
| Ganar duelo limpiamente | +7 | 50 (sin conocer a Eli) / 100 (conociéndola) |
| Herir al oponente en duelo | -5 | 0 (min) |
| Poción de Alquimista | -20 | 0 (min) |
| Amenaza de Alquimista | -30 | 0 (min) |
| **Chispa ≥ 100** | **No puede bajar** | **INMORTAL** |

### Fases de la chispa:

```
FASE 1: No conocen al otro (chispa 0-30/50)
   └── Elisabetha: max 30 leyendo pergaminos
   └── Lance: max 50 ganando duelos

FASE 2: Se conocen en la Taberna (chispa → 75)
   └── ¡BOOM! Ambos saltan a 75
   └── Se eliminan los topes de 30 y 50

FASE 3: Subiendo hacia 100
   └── Reencuentros en Taberna, duelos limpios, lecturas...

FASE 4: CHISPA = 100 → ¡FINAL FELIZ!
   └── Ya no baja por nada
   └── Ambos van a la Taberna con buscandoFinal=true
   └── Cuando coinciden → FIN DE LA SIMULACIÓN 🎉
```

---

## 10. 🚀 Cómo Compilar y Ejecutar

### Opción A: Automático con el script .bat

1. Abre el proyecto en IntelliJ IDEA
2. Haz **Build → Build Project** (Ctrl+F9) para compilar todo
3. Ejecuta el archivo `INICIAR_SIMULACION.bat` haciendo doble clic

Esto abrirá **9 ventanas de terminal**, una por cada componente, en este orden:
```
1. Alacena (5005)       ← Primero los servidores
2. Taberna (5002)
3. Mercado (5003)
4. Portón Norte (5004)
5. Elisabetha (5000)    ← Luego los protagonistas
6. Lance (5001)
7. Alquimistas          ← Por último los clientes
8. Caballeros
9. Damas
```

### Opción B: Manual (desde la terminal)

**Paso 1: Compilar** cada módulo:
```bash
# Desde la carpeta raíz del proyecto
javac ServidorAlacenaPociones/src/Alacena.java ServidorAlacenaPociones/src/HiloAlacena.java
javac ServidorTaberna/src/Taberna.java ServidorTaberna/src/HiloTaberna.java
javac ServidorMercado/src/Mercado.java ServidorMercado/src/HiloMercado.java
javac ServidorPortonNorte/src/Porton.java ServidorPortonNorte/src/HiloPorton.java
javac Cliente-Servidor-Elisabetha/src/Elisabetha.java Cliente-Servidor-Elisabetha/src/HiloServidorElisabetha.java
javac Cliente-Servidor-Lance/src/Lance.java Cliente-Servidor-Lance/src/HiloServidorLance.java
javac Cliente-Alquimista/src/Alquimista.java Cliente-Alquimista/src/HiloAlquimista.java
javac Cliente-DamaDelLazo/src/Dama.java Cliente-DamaDelLazo/src/HiloDama.java
javac Cliente-CaballeroDelPorton/src/Caballero.java Cliente-CaballeroDelPorton/src/HiloCaballero.java
```

**Paso 2: Ejecutar** (abrir una terminal por cada uno, EN ESTE ORDEN):
```bash
# Terminal 1: Alacena
cd ServidorAlacenaPociones/src && java Alacena

# Terminal 2: Taberna
cd ServidorTaberna/src && java Taberna

# Terminal 3: Mercado
cd ServidorMercado/src && java Mercado

# Terminal 4: Portón
cd ServidorPortonNorte/src && java Porton

# Terminal 5: Elisabetha
cd Cliente-Servidor-Elisabetha/src && java Elisabetha

# Terminal 6: Lance
cd Cliente-Servidor-Lance/src && java Lance

# Terminal 7: Alquimistas (te pedirá datos por teclado)
cd Cliente-Alquimista/src && java Alquimista

# Terminal 8: Caballeros (te pedirá datos por teclado)
cd Cliente-CaballeroDelPorton/src && java Caballero

# Terminal 9: Damas (te pedirá datos por teclado)
cd Cliente-DamaDelLazo/src && java Dama
```

### Opción C: Desde IntelliJ IDEA

1. Abre la carpeta del proyecto en IntelliJ
2. Ejecuta los `main` en este orden:
   - `Alacena.java` → `Taberna.java` → `Mercado.java` → `Porton.java`
   - `Elisabetha.java` → `Lance.java`
   - `Alquimista.java` → `Caballero.java` → `Dama.java`

> **⚠️ ORDEN IMPORTANTE**: Los servidores SIEMPRE se arrancan ANTES que los clientes. Si un cliente intenta conectar a un servidor que no existe, dará error `Connection refused`.

---

## 11. 🔄 Flujo Completo de la Simulación

```
┌─────────────────────────────────────────────────────────────┐
│                    INICIO DE LA SIMULACIÓN                  │
│     Chispa Elisabetha: 0          Chispa Lance: 0          │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────┐
│  FASE 1: VIDA ANTES DEL ENCUENTRO                           │
│                                                              │
│  Elisabetha:                    Lance:                       │
│  • Lee pergaminos (+8/-5)       • Gana duelos (+7/-5)       │
│  • Atiende damas (rumores -5)   • Habla con compañeros      │
│  • Visita mercado (sin efecto)  • Vigila portón (sin efecto)│
│  • Visita taberna (buscando)    • Vigila taberna (buscando) │
│                                                              │
│  MAX CHISPA: Eli=30, Lance=50                               │
│                                                              │
│  Mientras tanto:                                             │
│  • Damas envían rumores (-5) e invitaciones a bailes (-3)   │
│  • Caballeros ofenden a Lance → duelos                      │
│  • Alquimistas estudian pociones (30s cada vez)             │
└──────────────────────────┬───────────────────────────────────┘
                           │  ← Eli y Lance coinciden en la Taberna
                           ▼
┌──────────────────────────────────────────────────────────────┐
│  FASE 2: ¡LA CHISPA! (Primer encuentro)                     │
│                                                              │
│  Chispa Elisabetha: → 75         Chispa Lance: → 75        │
│  conoceALance = true             conoceAElisabetha = true   │
│                                                              │
│  ¡Se eliminan los topes! Ahora pueden subir libremente.     │
└──────────────────────────┬───────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────┐
│  FASE 3: CAMINO HACIA EL 100                                │
│                                                              │
│  • Reencuentros en taberna (Eli +15, Lance +10)             │
│  • Lecturas inspiradoras (+8 sin tope)                      │
│  • Duelos limpios (+7 sin tope)                             │
│                                                              │
│  PERO los enemigos contraatacan:                             │
│  • Pociones de alquimistas (-10/-20/-30)                    │
│  • Rumores de damas (-5)                                    │
│  • Duelos con herida (-5)                                   │
│  • Amenazas del Frente Norte (-30)                          │
└──────────────────────────┬───────────────────────────────────┘
                           │  ← Ambos llegan a chispa = 100
                           ▼
┌──────────────────────────────────────────────────────────────┐
│  FASE 4: FINAL FELIZ                                        │
│                                                              │
│  • Chispa ya NO puede bajar (protegida)                     │
│  • Ambos entran en bucle: ir a Taberna con ready=true       │
│  • Taberna verifica: elisabethaReady && lanceReady          │
│  • Cuando coinciden: ¡VICTORIA!                             │
│                                                              │
│  🎉🎉🎉 FIN DE LA SIMULACIÓN 🎉🎉🎉                      │
│  System.exit(0)                                              │
└──────────────────────────────────────────────────────────────┘
```

---



## 13. ❓ Preguntas Frecuentes

### ¿Por qué Elisabetha y Lance son "Cliente-Servidor"?
Porque necesitan **dos cosas a la vez**:
- **Servidor** (puerto 5000/5001): Para que las Damas, Caballeros y Alquimistas les envíen mensajes *en cualquier momento*.
- **Cliente**: Para visitar la Taberna, el Mercado, etc. *cuando ellos quieran*.

### ¿Qué pasa si un servidor no está arrancado?
El cliente que intente conectarse recibirá una excepción `IOException` (Connection refused). Por eso los servidores se arrancan PRIMERO.

### ¿Por qué la Taberna hace "polling" durante 8 segundos?
Porque Elisabetha y Lance no entran al mismo milisegundo. El servidor les da una ventana de 8 segundos para coincidir. Si uno entra en el segundo 2 y el otro en el segundo 5, el polling los detectará.

### ¿Qué es el "Grace Period" de 15 segundos?
Cuando ambos coinciden buscando el Final Feliz, el servidor espera 15 segundos antes de cerrar. Esto da tiempo a que **ambos** reciban la confirmación de victoria antes de que los estados se limpien.

### ¿Los Alquimistas siempre se conectan directamente a Eli/Lance?
No exactamente. Los Alquimistas primero **fabrican pociones** (30s estudiando) y las **guardan en la Alacena** (puerto 5005). Solo cuando tienen pociones en stock pueden intentar atacar. Primero **sacan** una poción de la Alacena, y solo si hay stock, se conectan a Eli/Lance para atacar.

### ¿Se puede ejecutar cada módulo en un ordenador diferente?
¡SÍ! Solo necesitas cambiar `"localhost"` por la IP del ordenador donde esté el servidor. Los clientes (Alquimistas, Damas, Caballeros) ya piden la IP por teclado al inicio.

### ¿Se pueden crear más de 2 alquimistas/damas/caballeros?
¡SÍ! El número de personajes se configura al iniciar cada cliente. Puedes tener 10 damas, 5 caballeros y 3 alquimistas si quieres. Cada uno será un hilo independiente.

### ¿Cuánto tarda la simulación?
Depende del azar. Puede tardar entre **5 y 30 minutos** dependiendo de cuántas veces coinciden en la Taberna y cuánto daño hacen los Alquimistas.

---

## 14. 🧩 Guía: Crear Nuevos Personajes y Lugares

Esta sección te enseña a **ampliar la simulación** añadiendo nuevos personajes secundarios y nuevos lugares siguiendo exactamente los mismos patrones del proyecto existente.

> **📌 Regla de oro**: En este proyecto, todo sigue el mismo patrón. Si entiendes cómo funciona UNO, puedes crear TODOS los que quieras.

---

### 14.1. Crear un nuevo personaje secundario (Cliente)

Supongamos que quieres crear un nuevo tipo de personaje: **los Espías del Rey**. Son clientes puros que se conectan a Lance para robarle información.

#### Paso 1: Crear la carpeta del módulo

```
010-LA-CHISPA-ADECUADA/
└── 📁 Cliente-EspiaDelRey/       ← NUEVA CARPETA
    └── src/
        ├── Espia.java            ← Main (configura y lanza hilos)
        └── HiloEspia.java        ← Lógica de cada espía individual
```

#### Paso 2: Crear la clase Main (`Espia.java`)

Copia el patrón exacto de `Dama.java` o `Caballero.java`. Solo cambia nombres:

```java
import java.util.Scanner;

public class Espia {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CONFIGURACIÓN DE ESPÍAS DEL REY ===");

        // 1. Pedimos la dirección del servidor al que nos conectamos
        System.out.print("IP del servidor (Lance) ej: localhost: ");
        String ipLance = sc.nextLine();

        System.out.print("Puerto del servidor (Lance) ej: 5001: ");
        int puertoLance = sc.nextInt();
        sc.nextLine();  // Limpiamos el buffer del Scanner

        // 2. Pedimos cuántos espías queremos
        System.out.print("Número de espías a crear: ");
        int numEspias = sc.nextInt();
        sc.nextLine();

        // 3. Creamos un array para guardar los hilos
        HiloEspia[] hilos = new HiloEspia[numEspias];

        // 4. Pedimos nombre de cada espía y creamos su hilo
        for (int i = 0; i < numEspias; i++) {
            System.out.print("Nombre del espía " + (i + 1) + ": ");
            String nombre = sc.nextLine();
            hilos[i] = new HiloEspia(nombre, ipLance, puertoLance);
        }

        // 5. Arrancamos todos los hilos
        System.out.println("\n🕵️ Iniciando todos los espías...");
        for (int i = 0; i < numEspias; i++) {
            hilos[i].start();
        }
        System.out.println("✅ Todos los espías lanzados.");
    }
}
```

> **📌 ¿Qué está pasando aquí?** Es SIEMPRE el mismo patrón: pedir datos → crear hilos → arrancarlos. Compáralo con `Dama.java` o `Caballero.java` — son prácticamente iguales.

#### Paso 3: Crear la clase Hilo (`HiloEspia.java`)

Aquí es donde defines el **comportamiento** del personaje:

```java
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class HiloEspia extends Thread {

    // --- ATRIBUTOS (los mismos que en cualquier personaje) ---
    private String nombre;
    private String hostLance;
    private int puertoLance;

    // --- CONSTRUCTOR ---
    public HiloEspia(String nombre, String hostLance, int puertoLance) {
        this.nombre = nombre;
        this.hostLance = hostLance;
        this.puertoLance = puertoLance;
    }

    // --- RUN: aquí arranca el hilo ---
    @Override
    public void run() {
        System.out.println("🕵️ " + nombre + " iniciando espionaje...");
        comportamientoEspia();
    }

    // --- COMPORTAMIENTO PRINCIPAL (bucle infinito) ---
    private void comportamientoEspia() {
        Random random = new Random();
        try {
            while (true) {
                int accion = random.nextInt(2);  // 50/50

                if (accion == 0) {
                    // ACCIÓN 1: Labores propias (no conecta con nadie)
                    System.out.println(nombre + " espiando en las sombras...");
                    Thread.sleep(5000);  // 5 segundos

                } else {
                    // ACCIÓN 2: Contactar con Lance para hacerle daño
                    contactarConLance();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " interrumpido.");
        }
    }

    // --- MÉTODO PARA CONECTAR CON LANCE ---
    private void contactarConLance() throws InterruptedException {
        // Intentar contactar durante 20 segundos máximo
        long fin = System.currentTimeMillis() + 20000;
        boolean contactado = false;

        while (System.currentTimeMillis() < fin && !contactado) {
            try (Socket socket = new Socket(hostLance, puertoLance)) {
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream());

                // *** PROTOCOLO: Debe coincidir con lo que espera HiloServidorLance ***
                // El servidor de Lance espera: writeUTF(nombre) + writeInt(tipo) + writeInt(daño)
                salida.writeUTF(nombre);     // Quién soy
                salida.writeInt(1);          // tipo=1 (Caballero/personaje que habla con Lance)
                salida.writeInt(5);          // daño=5 (ofensa → provocará duelo)

                // El servidor responde con un boolean (si hubo herida en el duelo)
                boolean herida = entrada.readBoolean();

                if (herida) {
                    System.out.println(nombre + ": ¡Lance me ha herido! Recuperándome...");
                    Thread.sleep(30000);  // 30 segundos de recuperación
                } else {
                    System.out.println(nombre + ": Perdí el duelo pero estoy ileso.");
                }
                contactado = true;

            } catch (IOException e) {
                Thread.sleep(500);  // Reintentar
            }
        }

        if (!contactado) {
            System.out.println(nombre + ": No pude contactar con Lance.");
        }
    }
}
```

> **⚠️ LO MÁS IMPORTANTE**: El protocolo (los `write`/`read`) debe coincidir EXACTAMENTE con lo que espera el servidor de Lance. Si Lance espera `readUTF → readInt → readInt`, tú debes enviar `writeUTF → writeInt → writeInt` en ese mismo orden.

#### Resumen del patrón para CUALQUIER personaje secundario:

```
┌────────────────────────────────────────────────┐
│  PLANTILLA PARA NUEVO PERSONAJE SECUNDARIO     │
├────────────────────────────────────────────────┤
│  1. Crear carpeta: Cliente-NombrePersonaje/    │
│  2. Crear Main.java:                           │
│     • Pedir IP, puerto, nº personajes, nombres │
│     • Crear array de hilos                     │
│     • Llamar .start() en cada hilo             │
│  3. Crear HiloPersonaje.java:                  │
│     • Constructor con nombre + datos de red    │
│     • run() → llama a comportamiento()         │
│     • comportamiento() → bucle infinito con:   │
│       ├─ Acciones propias (Thread.sleep)        │
│       └─ Conexión por Socket al servidor       │
│           ├─ Enviar datos (writeUTF/Int/Bool)  │
│           └─ Leer respuesta (readUTF/Int/Bool) │
└────────────────────────────────────────────────┘
```

---

### 14.2. Crear un nuevo lugar (Servidor)

Supongamos que quieres crear un nuevo lugar: **la Herrería**, donde Lance puede reparar su armadura y ganar chispa.

#### Paso 1: Crear la carpeta del módulo

```
010-LA-CHISPA-ADECUADA/
└── 📁 ServidorHerreria/             ← NUEVA CARPETA
    └── src/
        ├── Herreria.java            ← Clase main (abre un puerto)
        └── HiloHerreria.java        ← Hilo que atiende cada visita
```

#### Paso 2: Elegir un puerto LIBRE

Los puertos ya usados son:

| Puerto | Servicio |
|---|---|
| 5000 | Elisabetha |
| 5001 | Lance |
| 5002 | Taberna |
| 5003 | Mercado |
| 5004 | Portón Norte |
| 5005 | Alacena |
| **5006** | **← LIBRE: úsalo para la Herrería** |

#### Paso 3: Crear la clase Main (`Herreria.java`)

Copia el patrón de CUALQUIER servidor (`Taberna.java`, `Mercado.java`, etc.):

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Herreria {
    private static final String NOMBRE = "HERRERÍA";
    private static final int PUERTO = 5006;  // ← Puerto nuevo

    public static void main(String[] args) {
        // Abrimos el servidor en el puerto
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println(NOMBRE + " ABIERTA EN PUERTO " + PUERTO);

            // Bucle infinito: siempre esperando clientes
            while (true) {
                Socket socket = server.accept();           // Espera conexión
                new HiloHerreria(socket).start();          // Atiende en hilo aparte
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> **📌 Este código es IDÉNTICO** al de `Taberna.java`, `Mercado.java`, etc. Solo cambian el nombre y el puerto. TODOS los servidores siguen este patrón.

#### Paso 4: Crear la clase Hilo (`HiloHerreria.java`)

Aquí defines la **lógica del lugar**: qué pasa cuando alguien visita.

```java
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class HiloHerreria extends Thread {

    private Socket socket;

    // Servicios disponibles en la herrería
    private String[] servicios = {
        "Reparar armadura", "Afilar espada", "Forjar escudo nuevo",
        "Pulir yelmo", "Ajustar grebas"
    };

    public HiloHerreria(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            Random random = new Random();

            // 1. Ofrecer 3 servicios aleatorios al visitante
            for (int i = 0; i < 3; i++) {
                String servicio = servicios[random.nextInt(servicios.length)];
                salida.writeUTF(servicio);
            }

            // 2. El visitante elige uno (índice 0-2)
            int eleccion = entrada.readInt();

            // 3. Decidir si la reparación sale bien (70%) o mal (30%)
            boolean exito = random.nextInt(100) < 70;
            salida.writeBoolean(exito);

            System.out.println("Visita a la herrería: " + (exito ? "¡Éxito!" : "Falló"));

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### Resumen del patrón para CUALQUIER lugar/servidor:

```
┌──────────────────────────────────────────────────┐
│  PLANTILLA PARA NUEVO LUGAR (SERVIDOR)           │
├──────────────────────────────────────────────────┤
│  1. Crear carpeta: ServidorNombreLugar/          │
│  2. Elegir puerto libre (5006, 5007...)          │
│  3. Crear Main.java:                             │
│     • ServerSocket(PUERTO) + while(true)         │
│     • server.accept() → new HiloX(socket).start()│
│  4. Crear HiloX.java:                            │
│     • Constructor con Socket                     │
│     • run(): DataInput/Output + lógica propia    │
│     • Definir PROTOCOLO: qué datos envía/recibe  │
│  5. Documentar el protocolo de comunicación      │
└──────────────────────────────────────────────────┘
```

---

### 14.3. Integrar el nuevo personaje con un protagonista

Si tu nuevo personaje necesita **enviar mensajes a Elisabetha o Lance**, debe seguir **el protocolo existente** de sus servidores.

#### Protocolo del servidor de Elisabetha (puerto 5000):

```java
// HiloServidorElisabetha espera SIEMPRE estos 3 datos:
String nombre = entrada.readUTF();   // ¿Quién eres?
int tipo = entrada.readInt();        // ¿Qué tipo de mensaje traes?
int daño = entrada.readInt();        // ¿Cuánto daño haces?

// Tipos reconocidos:
//   tipo == 1 → Rumor/Confidencia (se guarda en buzón de damas)
//   tipo == 2 → Poción/Ataque de alquimista (baja chispa directamente)
//   tipo == 3 → Invitación al baile (se guarda en buzón)
```

**Para que tu nuevo personaje afecte a Elisabetha**, debe enviar estos 3 datos. Ejemplos:

```java
// Ejemplo: Un espía envía un rumor a Elisabetha (-5 chispa)
salida.writeUTF("Espía Sombra");  // nombre
salida.writeInt(1);                // tipo=1 (rumor)
salida.writeInt(5);                // daño=5

// Ejemplo: Un hechicero envía un ataque directo a Elisabetha (-15 chispa)
salida.writeUTF("Hechicero Oscuro");  // nombre
salida.writeInt(2);                    // tipo=2 (ataque directo)
salida.writeInt(15);                   // daño=15
```

#### Protocolo del servidor de Lance (puerto 5001):

```java
// HiloServidorLance espera SIEMPRE estos 3 datos:
String nombre = entrada.readUTF();   // ¿Quién eres?
int tipo = entrada.readInt();        // ¿Qué tipo de mensaje traes?
int daño = entrada.readInt();        // ¿Cuánto daño haces?

// Tipos reconocidos:
//   tipo == 1 y daño > 0  → Ofensa (duelo) → el servidor RESPONDE writeBoolean(herida)
//   tipo == 1 y daño == 0 → Confidencia (se guarda en buzón)
//   tipo == 2             → Ataque de alquimista (baja chispa directamente)
```

**Para que tu nuevo personaje afecte a Lance**, debe enviar estos 3 datos. Ejemplos:

```java
// Ejemplo: Un espía provoca a Lance a duelo
salida.writeUTF("Espía Sombra");  // nombre
salida.writeInt(1);                // tipo=1 (caballero/ofensa)
salida.writeInt(5);                // daño=5 (provoca duelo)
boolean herida = entrada.readBoolean();  // ¡IMPORTANTE! Leer la respuesta del duelo

// Ejemplo: Un envenenador ataca directamente a Lance (-20 chispa)
salida.writeUTF("Envenenador");   // nombre
salida.writeInt(2);                // tipo=2 (ataque directo como alquimista)
salida.writeInt(20);               // daño=20
// NOTA: tipo=2 NO devuelve respuesta, no hay readBoolean()
```

> **⚠️ CUIDADO**: Si envías `tipo=1` con `daño > 0` al servidor de Lance, el servidor **RESPONDE** con un `writeBoolean(herida)`. Tu cliente DEBE hacer `readBoolean()` o el socket se quedará colgado. Si envías `tipo=2` (alquimista), el servidor NO responde nada.

#### ¿Y si quiero crear un tipo NUEVO que el servidor no reconoce?

Entonces necesitas **modificar el servidor** (`HiloServidorElisabetha.java` o `HiloServidorLance.java`) para añadir un nuevo `else if`:

```java
// En HiloServidorElisabetha.java, añadir después del bloque de tipo==3:
} else if (tipo == 4) { // NUEVO: Mensaje de un Espía
    System.out.println("🕵️ " + nombreRemitente + " envía información secreta...");
    // Tu lógica aquí: subir chispa, bajar chispa, guardar en buzón...
    Elisabetha.modificarChispa(-daño);
}
```

---

### 14.4. Integrar el nuevo lugar con un protagonista

Si has creado un nuevo servidor (como la Herrería en puerto 5006), necesitas que **Elisabetha o Lance puedan visitarlo**.

#### Paso 1: Añadir el puerto al protagonista

En `Lance.java` (o `Elisabetha.java`), añade la constante del nuevo puerto:

```java
// En Lance.java, junto a los demás puertos:
private static final int PUERTO_HERRERIA = 5006;  // ← NUEVO
```

#### Paso 2: Crear el método de visita

Añade un nuevo método que se conecte a tu servidor:

```java
// En Lance.java, nuevo método:
private static void visitarHerreria() {
    System.out.println(NOMBRE + " visita la HERRERÍA...");
    try (Socket socket = new Socket(HOST, PUERTO_HERRERIA);
         DataInputStream entrada = new DataInputStream(socket.getInputStream());
         DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

        // 1. Recibir 3 servicios (el servidor los envía)
        for (int i = 0; i < 3; i++) {
            String servicio = entrada.readUTF();
            System.out.println("   Servicio " + (i + 1) + ": " + servicio);
        }

        // 2. Elegir uno al azar
        salida.writeInt(new Random().nextInt(3));

        // 3. Ver si la reparación salió bien
        boolean exito = entrada.readBoolean();

        Thread.sleep(5000);  // 5 segundos de visita

        if (exito) {
            System.out.println("🔨 ¡Armadura reparada! (+5 chispa)");
            modificarChispa(5);
        } else {
            System.out.println("🔨 La reparación falló...");
        }

    } catch (Exception e) {
        System.out.println("La Herrería está cerrada.");
    }
}
```

> **⚠️ RECUERDA**: Los `read` y `write` deben coincidir con lo que tu `HiloHerreria.java` envía/espera. Si el servidor hace `writeUTF, writeUTF, writeUTF, readInt, writeBoolean`, el cliente debe hacer `readUTF, readUTF, readUTF, writeInt, readBoolean` en ese mismo orden.

#### Paso 3: Añadir la acción al switch del protagonista

En el bucle principal de `Lance.java`, amplía el `switch`:

```java
// ANTES (3 acciones):
int accion = random.nextInt(3);

// DESPUÉS (4 acciones):
int accion = random.nextInt(4);  // ← Cambiado de 3 a 4
switch (accion) {
    case 0: atenderCompañeros();    break;
    case 1: hacerGuardiaPorton();   break;
    case 2: hacerGuardiaTaberna();  break;
    case 3: visitarHerreria();      break;  // ← NUEVA ACCIÓN
}
```

---

### 14.5. Añadirlo al script de lanzamiento

Edita `INICIAR_SIMULACION.bat` para que tu nuevo módulo se arranque automáticamente.

#### Para un nuevo SERVIDOR, añádelo en la zona de servidores:

```bat
REM ===== SERVIDORES =====
echo [SERVIDOR] Iniciando Herreria (Puerto 5006)...
start "HERRERÍA - Puerto 5006" cmd /k "cd /d %OUT_PATH%\ServidorHerreria && java Herreria"
timeout /t 3 >nul
```

#### Para un nuevo CLIENTE, añádelo en la zona de clientes:

```bat
REM ===== CLIENTES =====
echo [CLIENTE] Iniciando Espias (2 espías)...
start "ESPÍAS" cmd /k "cd /d %OUT_PATH%\Cliente-EspiaDelRey && (echo localhost&echo 5001&echo 2&echo Sombra&echo Sigiloso) | java Espia"
timeout /t 3 >nul
```

> **📌 El truco del `echo`**: Los `echo` separados por `&` simulan la entrada por teclado. Cada `echo` responde a una pregunta que hace el `Scanner` del programa. Mira `INICIAR_SIMULACION.bat` para ver cómo lo hacen los demás clientes.

---

### 📋 Checklist rápido para añadir algo nuevo

#### Nuevo personaje secundario (Cliente):
- [ ] Crear carpeta `Cliente-NuevoPersonaje/src/`
- [ ] Crear `NuevoPersonaje.java` (main con Scanner + lanzar hilos)
- [ ] Crear `HiloNuevoPersonaje.java` (comportamiento + conexión Socket)
- [ ] Respetar el protocolo del servidor al que se conecta
- [ ] Probar que compila: `javac NuevoPersonaje.java HiloNuevoPersonaje.java`
- [ ] Añadir al `INICIAR_SIMULACION.bat`

#### Nuevo lugar (Servidor):
- [ ] Crear carpeta `ServidorNuevoLugar/src/`
- [ ] Elegir puerto libre (5006, 5007...)
- [ ] Crear `NuevoLugar.java` (main con ServerSocket + while true)
- [ ] Crear `HiloNuevoLugar.java` (lógica de atención + definir protocolo)
- [ ] Añadir el puerto como constante en el protagonista que lo visite
- [ ] Crear el método `visitarNuevoLugar()` en el protagonista
- [ ] Ampliar el `switch` del protagonista para incluir la nueva acción
- [ ] Añadir al `INICIAR_SIMULACION.bat`

---

> 📝 **Nota final**: Esta guía cubre el 100% del código fuente del proyecto y te enseña a ampliarlo. Si has leído todo esto, ya eres capaz de entender, modificar y crear un proyecto similar desde cero. ¡Buena suerte, Custodio del Fuego Secreto! 🔥
