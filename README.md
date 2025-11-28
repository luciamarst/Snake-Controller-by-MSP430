# Snake Controller by MSP430

¡Welcome to a Snake Controlled by MSP430! 🐍🎮

This project combines hardware and software to create an interactive experience of the clasic Snake game, where an MSP430 microcontroller acts as a controller,and a Java interfaces displays the game on screen.

<img width="1536" height="1024" alt="dcc8fc58-3d25-4349-a748-4e5a24633067" src="https://github.com/user-attachments/assets/9072666a-2970-4ad2-9bb1-f8e9020fc3bd" />

## Description

The projects consists of:
1) Hardware: An MSP430 board acting as remote control using physical buttoms. Each buttom sends commads vía UART.
2) Software: A Java interface which receives the commands from MPS430 and updates the Snake game in real time.
3) Screen LCD on MSP430: Displays basic information such as score and game state.

The game allows move the snake up, down, left, or right and handles events like:
- Eating apples
- Detecting collisions with walls or itself
- Displaying a Game Over message

## Features
- Conection UART between MSP430 and Java interface.
- Physical control via buttons on the boards.
- Graphical interface with different colors for the head, body, and apples.
- Random apple generation.
- Keyboard control available if MSP430 is not used.

## Technologies
- MSP430: C
- Java: Swing for GUI
- Serial Communication: jSerialComm

## ¿How to Use?

· Hardware:
1) Connect the buttons to the configurated pins on the MSP430.
2) Compile and upload the coide on the MSP430.
3) Ensure the UART communication is working.
   
· Software:
1) Open the Java project and compile it.
2) Connect the MPS430 board to PC.
3) Execute VentanaPrincipal.java.

The snake will move according to the buttoms pressed on the board.

Keyboard control (optional):
Use the arrow keys to move the snake if the MSP430 is not connected.

## Project Structure

```
/MSP430
main.c # Microcontroller code
/Java
VentanaPrincipal.java # Graphical interface
Bucle.java # Timer logic
Posicion.java # Snake position class
DIRECCION.java # Direction enum
```

## Author
Lucía Martínez Martínez
