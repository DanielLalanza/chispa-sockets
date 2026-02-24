# Diagrama UML de Clases - La Chispa Adecuada

A continuación se muestra una representación simplificada de las clases del proyecto, sus atributos principales y sus métodos, omitiendo la herencia de `Thread` para mayor claridad.

```mermaid
classDiagram
    %% --- PROTAGONISTAS ---
    class Elisabetha {
        +int chispa
        +boolean conoceALance
        +boolean tieneInvitacion
        +List~String~ damasConocidas
        +List~String~ alquimistasVistos
        +Map buzonDamas
        +main(args)
        +atenderDamas()
        +leerPergaminos()
        +visitarMercado()
        +visitarTaberna()
        +modificarChispa(int)
    }

    class HiloServidorElisabetha {
        +run()
    }

    class Lance {
        +int chispa
        +boolean conoceAElisabetha
        +List~String~ compañerosConocidos
        +List~String~ alquimistasVistos
        +Map buzonCompañeros
        +main(args)
        +atenderCompañeros()
        +resolverDuelo()
        +hacerGuardiaPorton()
        +hacerGuardiaTaberna()
        +modificarChispa(int)
    }

    class HiloServidorLance {
        +run()
    }

    %% --- PERSONAJES DE APOYO ---
    class Alquimista {
        +main(args)
    }

    class HiloAlquimista {
        +String nombre
        +run()
        +comportamientoAlquimista()
        +guardarPocion(int)
        +sacarPocion(int)
        +atacar(int, int, int)
    }

    class Dama {
        +main(args)
    }

    class HiloDama {
        +String nombre
        +run()
        +comportamientoDama()
    }

    class Caballero {
        +main(args)
    }

    class HiloCaballero {
        +String nombre
        +run()
        +comportamientoCaballero()
    }

    %% --- SERVICIOS GLOBALES ---
    class Taberna {
        +main(args)
    }

    class HiloTaberna {
        -boolean elisabethaDentro
        -boolean lanceDentro
        +run()
        +entrar(id, ready)
        +verificar(id, ready)
        +salir(id)
    }

    class Mercado {
        +main(args)
    }

    class HiloMercado {
        -String[] inventario
        +run()
    }

    class Porton {
        +main(args)
    }

    class HiloPorton {
        -String[] origenes
        -String[] productos
        +run()
    }

    class Alacena {
        +main(args)
    }

    class HiloAlacena {
        -int pocionesEli
        -int pocionesLance
        +run()
    }

    %% --- RELACIONES (COMUNICACIÓN POR SOCKETS) ---
    Elisabetha ..> HiloServidorElisabetha : lanza
    Lance ..> HiloServidorLance : lanza
    
    Alquimista ..> HiloAlquimista : lanza N hilos
    Dama ..> HiloDama : lanza N hilos
    Caballero ..> HiloCaballero : lanza N hilos

    HiloAlquimista ..> HiloServidorElisabetha : ataca (P5000)
    HiloAlquimista ..> HiloServidorLance : ataca/amenaza (P5001)
    HiloAlquimista ..> HiloAlacena : almacena/extrae (P5005)

    HiloDama ..> HiloServidorElisabetha : envía rumor/invitacion (P5000)
    HiloCaballero ..> HiloServidorLance : envía ofensa/duelo (P5001)

    Elisabetha ..> HiloMercado : compra (P5003)
    Elisabetha ..> HiloTaberna : visita/final (P5002)
    Lance ..> HiloTaberna : visita/final (P5002)
    Lance ..> HiloPorton : inspecciona (P5004)
```

### Resumen de la Estructura
- **Elisabetha y Lance** son los ejes centrales. Poseen un servidor interno para recibir influencias externas y un comportamiento de cliente para realizar sus acciones.
- **Los Alquimistas, Damas y Caballeros** son agentes externos (Clientes) que buscan afectar la "Chispa" de los protagonistas.
- **Los Servicios (Alacena, Mercado, Taberna, Portón)** son servidores pasivos que gestionan recursos o encuentros.
