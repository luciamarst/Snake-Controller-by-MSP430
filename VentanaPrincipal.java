package main;

import com.fazecast.jSerialComm.*;


import java.awt.Color;
import java.io.File;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.*;
public class VentanaPrincipal extends JFrame{
	
	private SerialPort comPort;
	private static final int MAX_SIZE = 650;
	private static final int RGN_SIZE = 25;
	private static final Color cabeza = new Color(168, 10,170);
	private static final Color cuerpo = new Color(204,43,212);
	private static final Color appleColor = new Color(207,17,17);
	private static final int SPEED = 1000;
	private  JPanel regiones[][];
	private DIRECCION dir;
	private  DIRECCION dir_anterior;
	private  LinkedList<Posicion> snake;
	private  Posicion manzana;
	private  Timer reloj;
	private  JLabel finJuego;
	
	
	
     public VentanaPrincipal() {
        super("¡El mejor juego de la historia (SNAKE DE GOOGLE)!");
        VentanaPrincipal esto = this;
        
        SerialPort[] listaPuertos = SerialPort.getCommPorts();
   	 
   	 for(int i = 0; i < listaPuertos.length;i++)
   	 {
   		 System.out.println(listaPuertos.toString());
   	 }
   	 
   	    comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.addDataListener(new SerialPortDataListener() {
           @Override
           public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
           @Override
           public void serialEvent(SerialPortEvent event)
           {
              byte[] newData = event.getReceivedData();
              if(newData.length > 0) {
            	  
            	  switch((char)newData[0]) {
            	  case '3': 
						if(!esto.direccionContraria(DIRECCION.LEFT))
							esto.dir = DIRECCION.LEFT;
					break;
				case '4': 
					if(!esto.direccionContraria(DIRECCION.RIGHT))
						esto.dir = DIRECCION.RIGHT;
					break;
				case '1': 
					if(!esto.direccionContraria(DIRECCION.UP))
						esto.dir = DIRECCION.UP;
					break;
				case '2': 
					if(!esto.direccionContraria(DIRECCION.DOWN))
						esto.dir = DIRECCION.DOWN;
					break;
				default: break;
            	  }
              }
              
              System.out.println("Received data of size: " + newData.length);
              for (int i = 0; i < newData.length; ++i)
                 System.out.print((char)newData[i]);
              System.out.println("\n");
           }
        });
        
        dir = DIRECCION.RIGHT;
   	 
        GraphicsEnvironment ge = 
        GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./resurce/game_over.ttf")));
		} catch (Exception e) {
			e.printStackTrace();
		}

        snake = new LinkedList<Posicion>();
        reloj = new Timer(SPEED,new Bucle(this));
   	 	reloj.start();
        initGUI();
     
     
    }
     
     public void actualiza()
     {
    	 byte[] escritura = {'0'};
    	 dir_anterior = dir;
    	 Posicion cabezon = snake.getLast();
    	 
    	 cambiarColor(cuerpo,cabezon);
    	 cabezon = new Posicion(cabezon.get_x()+dir.get_x(),cabezon.get_y()+dir.get_y());
    	 
    	 if(manzana.equals(cabezon))
    	 {
    		 generarManzana();
    		 escritura[0] = '1';
    		 comPort.writeBytes(escritura, 1);
    		 regiones[manzana.get_x()][manzana.get_y()].setBackground(appleColor);
    	 }
    	 else {
    		 Posicion primero = snake.getFirst();
    		 cambiarColor(colorCelda(primero.get_x(),primero.get_y()), primero);
    		 snake.removeFirst();
    	 }
    	 
    	 if(outOfRange(cabezon)|| snake.contains(cabezon)) {
    		 reloj.stop();
    		 escritura[0] = '3';
    		 comPort.writeBytes(escritura, 1);
    		 finJuego.setVisible(true);
    		 return;
    	 }
    	 
    	 snake.addLast(cabezon);
    	 cambiarColor(cabeza,snake.getLast());
     }

     private void initGUI() {
         JPanel mainPanel = new JPanel();
         boolean empezado = false;
         mainPanel.setBackground(Color.BLACK);
         mainPanel.setOpaque(true);
         mainPanel.setLayout(null);
         
         finJuego = new JLabel("GAME OVER");
         finJuego.setForeground(Color.red);
         finJuego.setSize(500, 250);
         finJuego.setLocation(125, 75);
         finJuego.setFont(new Font("Game Over", Font.BOLD, 160));
         finJuego.setVisible(false);
         
         mainPanel.add(finJuego);
         
         regiones = new JPanel[MAX_SIZE/RGN_SIZE][];
         for(int i = 0 ; i < MAX_SIZE/RGN_SIZE;i++)
         {
        	 regiones[i] = new JPanel[MAX_SIZE/RGN_SIZE];
        	 for(int e = 0; e < MAX_SIZE/RGN_SIZE;e++)
        	 {
        		 regiones[i][e] = new JPanel();
        		 regiones[i][e].setLocation(i*RGN_SIZE,e*RGN_SIZE);
        		 regiones[i][e].setSize(RGN_SIZE,RGN_SIZE);
        		 regiones[i][e].setBackground(colorCelda(i,e));
        		 mainPanel.add(regiones[i][e]);
        	 }
         }
         
         snake.addLast(new Posicion(((int)(MAX_SIZE/RGN_SIZE)/2)-1,(int)(MAX_SIZE/RGN_SIZE)/2));
         regiones[snake.get(0).get_x()][snake.get(0).get_y()].setBackground(cuerpo);
         snake.addLast(new Posicion((int)(MAX_SIZE/RGN_SIZE)/2,(int)(MAX_SIZE/RGN_SIZE)/2));
         regiones[snake.get(1).get_x()][snake.get(1).get_y()].setBackground(cabeza);
         
         generarManzana();
         regiones[manzana.get_x()][manzana.get_y()].setBackground(appleColor);
         
         this.setContentPane(mainPanel);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         this.setSize(MAX_SIZE + 15, MAX_SIZE+40);
         this.setResizable(false);
         this.setVisible(true);
         

         byte[] empieza = {'0'};
         if(empieza[0] != '2' && !empezado) {
        	 empieza[0] = '2';
   	  		comPort.writeBytes(empieza, 1);
   	  		empezado = true;
         }
        
     }
     
    
     public static void main(String [] args) {
         SwingUtilities.invokeLater(new Runnable() {
         public void run() {
        	 new VentanaPrincipal().addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyPressed(KeyEvent e) {
					VentanaPrincipal ventana = (VentanaPrincipal)e.getSource();
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_LEFT: 
								if(!ventana.direccionContraria(DIRECCION.LEFT))
									ventana.dir = DIRECCION.LEFT;
							break;
						case KeyEvent.VK_RIGHT: 
							if(!ventana.direccionContraria(DIRECCION.RIGHT))
								ventana.dir = DIRECCION.RIGHT;
							break;
						case KeyEvent.VK_UP: 
							if(!ventana.direccionContraria(DIRECCION.UP))
								ventana.dir = DIRECCION.UP;
							break;
						case KeyEvent.VK_DOWN: 
							if(!ventana.direccionContraria(DIRECCION.DOWN))
								ventana.dir = DIRECCION.DOWN;
							break;
						default: break;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {}
        		 
        	 });;
        }});
     }
     
     private void generarManzana(){
    	 Posicion pos;
    	 
    	 do {
    		pos = new Posicion((int)(Math.random() * (MAX_SIZE/RGN_SIZE)),(int)(Math.random() * (MAX_SIZE/RGN_SIZE)));
    	 }while(snake.contains(pos));
    	 
    	 manzana = pos;
     }
     
     private boolean outOfRange(Posicion pos) {
    	 return pos.get_x() >= (int)MAX_SIZE/RGN_SIZE || pos.get_x() < 0 
    			 || pos.get_y() >= (int)MAX_SIZE/RGN_SIZE || pos.get_y() < 0;
     }
     
     private void cambiarColor(Color color, Posicion _pos)
     {
    	 regiones[_pos.get_x()][_pos.get_y()].setBackground(color);
     }
     
     private  Color colorCelda(int x, int y){
    	 boolean color;
    	 
    	 color = (x%2 == 0);
    	 if(y%2 == 0) color = !color;
    	 
    	 return color ? new Color(54,230,15): new Color(10,170,15); //10,170,15
     }
     
     private  boolean direccionContraria(DIRECCION _dir){
 		return  (dir_anterior.get_x()+_dir.get_x())== 0 && (dir_anterior.get_y() + _dir.get_y()) == 0;
     }
     
     
}