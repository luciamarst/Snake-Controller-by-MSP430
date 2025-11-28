# Snake Controller by MSP430


<img width="1536" height="1024" alt="dcc8fc58-3d25-4349-a748-4e5a24633067" src="https://github.com/user-attachments/assets/9072666a-2970-4ad2-9bb1-f8e9020fc3bd" />

¡Welcome to a Snake Controlled by MSP430! 🐍🎮
Este proyecto combina hardware y software para crear una experiencia interactiva del clásico juego Snake, donde un microcontrolador MSP430 actúa como mando, y una interfaz en Java muestra el juego en pantalla.

## Descripción

El proyecto consiste en:
1) Hardware: Una placa MSP430 que funciona como control remoto mediante botones físicos. Cada botón envía comandos a través de UART.
2) Software: Una interfaz en Java que recibe los comandos del MSP430 y actualiza el juego Snake en tiempo real.
3) Pantalla LCD en MSP430: Visualiza información básica como puntuación y estado del juego.

El juego permite mover la serpiente hacia arriba, abajo, izquierda o derecha y gestionar eventos como:

1) Comer manzanas
2) Detectar colisiones con paredes o consigo misma
3) Mostrar mensaje de Game Over

## Características
- Conexión UART entre MSP430 y la interfaz Java.
- Control físico mediante botones en la placa.
- Interfaz gráfica con colores diferenciados para cabeza, cuerpo y manzanas.
- Generación aleatoria de manzanas.
- Compatible con control por teclado en caso de no tener el MSP430.

## Tecnologías
- MSP430: C
- Java: Swing para la interfaz gráfica
- Serial Communication: jSerialComm

## ¿Cómo usarlo?

· Hardware:
1) Conectar botones a los pines configurados en el MSP430.
2) Compilar y cargar el código en el MSP430.
3) Asegurarse de que la comunicación UART esté funcionando.

· Software:
1) Abrir el proyecto Java y compilarlo.
2) Conectar el MSP430 al PC.
3) Ejecutar VentanaPrincipal.java.

La serpiente se moverá según los botones presionados en la placa.

Control por teclado (opcional):
Flechas del teclado para mover la serpiente si no se usa la placa.

## Estructura del Proyecto
/MSP430
    main.c       # Código del microcontrolador
/Java
    VentanaPrincipal.java  # Interfaz gráfica del juego
    Bucle.java             # Lógica del temporizador
    Posicion.java          # Clase para la posición de la serpiente
    DIRECCION.java         # Enum de direcciones

## Autor
Lucía Martínez Martínez
