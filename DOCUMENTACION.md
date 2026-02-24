# Proyecto: La Chispa Adecuada (010-LA-CHISPA-ADECUADA)

## 📖 Introducción y Propósito del Sistema
Este proyecto es una **simulación distribuida de alta fidelidad** desarrollada en **Java**. Utiliza una arquitectura basada en **Sockets TCP** y **Multihilo (Multithreading)** para representar un ecosistema medieval fantástico interconectado. 

El objetivo narrativo y técnico es coordinar la vida de dos protagonistas, **Elisabetha** y **Lance**, quienes deben navegar a través de influencias sociales, sabotajes alquímicos y deberes políticos para incrementar su nivel de "Chispa" (conexión emocional) hasta 100, momento en el cual deben sincronizarse físicamente en una ubicación específica para concluir la simulación con el "Final Feliz".

---

## 🏗️ Mapa de Arquitectura y Red

El sistema se compone de **9 módulos independientes** que operan como procesos separados en la red, comunicándose a través de un esquema de puertos predefinidos en `localhost`.

### 📡 Especificación de Puertos
| Puerto | Servicio | Rol Principal |
| :--- | :--- | :--- |
| **5000** | **Servidor Elisabetha** | Nodo receptor de influencias sociales (Damas) y ataques (Alquimistas). |
| **5001** | **Servidor Lance** | Nodo receptor de desafíos (Caballeros) y sabotajes (Alquimistas). |
| **5002** | **Taberna El Descanso** | Punto de encuentro y sincronización de hilos para los protagonistas. |
| **5003** | **Mercado de Roedalia** | Proveedor de ítems y transacciones comerciales aleatorias. |
| **5004** | **Portón Norte** | Nodo de generación de eventos de inspección para la guardia. |
| **5005** | **Alacena de Pociones** | Almacén compartido (Búfer) para el intercambio de recursos entre facciones. |

---

## 🌹 Los Protagonistas: Elisabetha y Lance

Ambos actúan como **nodos híbridos** (Cliente y Servidor simultáneamente). Como clientes, realizan sus "vidas diarias"; como servidores, escuchan las "llamadas" del mundo exterior.

### 1. Elisabetha (`Cliente-Servidor-Elisabetha`)
Representa a la Alquimista Real. Su estado emocional es voluble y depende de la lectura y el entorno social.

#### **Lógica Interna y Atributos**
- **`chispa`**: Variable `volatile int` que asegura que los cambios realizados por el hilo del servidor sean visibles inmediatamente por el hilo cliente.
- **`buzonDamas`**: Un `ConcurrentHashMap` que funciona como una **matriz de datos dinámica**. Mapea el nombre de una dama a un array `int[]` que contiene el `tipo` de mensaje y el `daño` emocional.
- **Protección de Amor Inquebrantable**: En `modificarChispa(int cantidad)`, se implementa una guardia lógica: si la chispa es 100 y la cantidad es negativa, la operación se aborta. Esto asegura que una vez alcanzado el clímax, nada pueda enfriar su amor.

#### **Métodos Principales**
- **`leerPergaminos()`**: Simula la lectura en la biblioteca. Implementa una **suma dinámica**: si Elisabetha no conoce a Lance, su chispa tiene un "techo" de 30. El método calcula `30 - chispa` y solo suma el residuo si es menor que el aumento estándar (5).
- **`atenderDamas()`**: Elisabetha consulta su buzón. Si hay mensajes (Rumores, Confidencias o Invitaciones), procesa sus efectos y elimina la entrada del mapa (`remove`) para evitar procesar el mismo mensaje dos veces.

---

### 2. Lance (`Cliente-Servidor-Lance`)
Caballero protector del reino, enfocado en el honor y el combate.

#### **Lógica de Vigilancia y Honor**
- **`resolverDuelo()`**: Cuando un Caballero del Portón lo desafía, Lance entra en un estado de combate. El 20% de las veces hiere por error al oponente (resta chispa propia), pero el resto gana limpiamente (suma chispa dinámica hasta un tope de 50 antes de conocer a Elisabetha).
- **`hacerGuardiaPorton()`**: Conecta al Puerto 5004 para inspeccionar carretas. Implementa una lógica de filtrado basada en el origen ("Roedalia") y el tipo de producto (bloqueando lácteos sin fermentar de tierras lejanas).

---

## 🏛️ Servidores de Infraestructura Centrales

### 3. Taberna "El Descanso del Guerrero" (`ServidorTaberna`)
Es el componente más complejo de sincronización. No solo es un servidor, sino un **árbitro de encuentros**.

#### **Flujo de Sincronización (Threading)**
1. **Entrada (`entrar`)**: Al conectar, el protagonista registra su `id` y si está "listo" (buscando el final). El método es `synchronized` para evitar condiciones de carrera en las flags compartidas (`elisabethaDentro`, `lanceDentro`).
2. **Estancia y Polling (`verificar`)**: El hilo no termina inmediatamente. Ejecuta un bucle de 16 iteraciones (8 segundos) llamando a `verificar()`. Esto permite que si un personaje entra 1 o 2 segundos después que el otro, el primero lo detecte mediante **polling sincronizado**.
3. **Grace Period**: Si detectan el "Final Feliz", el hilo duerme 15 segundos extra (`Thread.sleep(15000)`) para asegurar que el socket del compañero también reciba la confirmación de victoria antes de que el servidor limpie los estados.

### 4. Alacena de Pociones (`ServidorAlacenaPociones`)
Funciona como una memoria compartida persistente para los Alquimistas.
- **Pociones Tipo 1**: Destinadas a influir en Elisabetha.
- **Pociones Tipo 2**: Destinadas a influir en Lance.
- **Concurrencia**: Utiliza métodos `synchronized` para incrementar o decrementar el stock global, asegurando que dos alquimistas no retiren la misma poción simultáneamente.

---

## 👥 Facciones y Personajes de Apoyo

### 5. Alquimistas (`Cliente-Alquimista`)
Hilos independientes que operan como saboteadores.
- **Acción: Estudiar (60%)**: Dedican 30 segundos a la elaboración. Tienen un 40% de probabilidad de fallo.
- **Acción: Atacar**: Si consiguen una poción de la alacena, viajan al puerto de Lance o Eli. Mediante un `Socket`, envían un paquete de datos: `[Nombre, Tipo: 2, Daño]`.

### 6. Damas del Lazo (`Cliente-DamaDelLazo`)
Controlan el flujo social de Elisabetha.
- **Mensajería Distribuida**: Generan eventos aleatorios (15% Invitaciones, 85% Rumores/Confidencias) y los inyectan en el servidor de Elisabetha. 
- **Persistencia**: Si Elisabetha no está activa, reintentan la conexión cada 500ms durante 20 segundos.

### 7. Caballeros del Portón (`Cliente-CaballeroDelPorton`)
Interactúan exclusivamente con Lance para poner a prueba su honor.
- **Duelos**: Retan a Lance mediante una ofensa. Si Lance los hiere en el duelo, el hilo del caballero entra en un estado de "Recuperación" de 30 segundos (`Thread.sleep(30000)`).

---

## 🔄 Flujos Críticos y Casos de Uso

### Caso 1: Desbloqueo de la Chispa (El Primer Encuentro)
1. Elisabetha y Lance visitan la Taberna como parte de sus rutinas normales.
2. `HiloTaberna` detecta su presencia simultánea. Devuelve `true` a ambos.
3. El cliente de Elisabetha detecta `conoceALance = false`, lo cambia a `true` y salta automáticamente a **75 de chispa**. Esto permite que sus acciones futuras superen el límite anterior de 30.

### Caso 2: El Clímax del "Final Feliz"
1. Ambos procesos cliente alcanzan 100 de chispa.
2. Entran en el bloque `else` de sus bucles principales, dirigiéndose a la Taberna con el flag `buscandoFinal = true`.
3. El servidor Taberna valida la condición `lanceReady && elisabethaReady`.
4. Ambos procesos reciben la confirmación final y ejecutan `System.exit(0)`, terminando la simulación global.

---

## 🛠️ Tecnologías y Patrones de Diseño Aplicados

- **Protocolo de Aplicación sobre TCP**: Definición personalizada de paquetes de datos (UTF para nombres, Int para comandos, Boolean para estados).
- **Semáforos de Estado (Volatile)**: Garantía de visibilidad entre hilos de servidor y cliente en la misma JVM.
- **Tratamiento de Errores**: Manejo de `SocketTimeoutException` para evitar bloqueos infinitos de carretas o duelos cuando un servicio cae.
- **IA de Comportamiento**: Uso de probabilidades ponderadas (`Random`) para simular personalidades y decisiones humanas en los hilos secundarios.

---

## 📈 Rendimiento y Escalabilidad
El sistema está diseñado para soportar múltiples instancias de cada facción. Se pueden lanzar 10 hilos de Alquimistas, 20 de Damas y 15 de Caballeros simultáneamente, y el sistema de sincronización en los servidores (Alacena, Taberna) mantendrá la integridad de los datos gracias a la granularidad de los bloqueos `synchronized`.
