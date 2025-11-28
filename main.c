#include <msp430.h>

unsigned int n = 0;
unsigned int contador=0;
unsigned int iniciado = 0;
unsigned int mostrando = 0;
int b[6] = {'0', '0', '0', '0', '0', '0'};

// Configures ACLK to 32 KHz crystal
void config_ACLK_to_32KHz_crystal () {
    // By default , ACLK runs on LFMODCLK at 5 MHz /128 = 39 KHz
    // Reroute pins to LFXIN / LFXOUT functionality
    PJSEL1 &= ~ BIT4 ;
    PJSEL0 |= BIT4 ;
    // Wait until the oscillator fault flags remain cleared
    CSCTL0 = CSKEY ; // Unlock CS registers
    do {
        CSCTL5 &= ~ LFXTOFFG ; // Local fault flag
        SFRIFG1 &= ~ OFIFG ; // Global fault flag
    } while (( CSCTL5 & LFXTOFFG ) != 0) ;
    CSCTL0_H = 0; // Lock CS registers
    return ;
}

const unsigned char LCD_Num [10] = {0xFC , 0x60 , 0xDB , 0xF3 , 0x67 , 0xB7 , 0xBF , 0xE0 , 0xFF ,0xE7 };

// *************** function that displays any 16 - bit unsigned integer ************
inline void display_num_lcd(unsigned int n1, unsigned int n2) {
    unsigned int digit;

    // Muestra la primera variable (n1) en los dígitos quinto y cuarto
       digit = n1 % 100;  // Tomar solo los dos dígitos menos significativos
       if (digit > 99) digit = 99; // Asegurar que el dígito no sea mayor que 99
       LCDM6 = LCD_Num[digit / 10]; // Quinto dígito
       LCDM4 = LCD_Num[digit % 10]; // Cuarto dígito

       // Muestra la segunda variable (n2) en los dígitos tercero, segundo y primero
       digit = n2 % 1000; // Tomar solo los tres dígitos menos significativos
       if (digit > 999) digit = 999; // Asegurar que el dígito no sea mayor que 999
       LCDM19 = LCD_Num[digit / 100]; // Tercer dígito
       LCDM15 = LCD_Num[(digit / 10) % 10]; // Segundo dígito
       LCDM8 = LCD_Num[digit % 10]; // Primer dígito
}

// **********************************************************
// Initializes the LCD_C module
// *** Source : Function obtained from MSP430FR6989 Sample Code ***
void Initialize_LCD () {
    PJSEL0 = BIT4 | BIT5 ; // For LFXT
    // Initialize LCD segments 0 - 21; 26 - 43
    LCDCPCTL0 = 0xFFFF ;
    LCDCPCTL1 = 0xFC3F ;
    LCDCPCTL2 = 0x0FFF ;
    // Configure LFXT 32 kHz crystal
    CSCTL0_H = CSKEY >> 8; // Unlock CS registers
    CSCTL4 &= ~ LFXTOFF ; // Enable LFXT
    do {
        CSCTL5 &= ~ LFXTOFFG ; // Clear LFXT fault flag
        SFRIFG1 &= ~ OFIFG ;
    } while ( SFRIFG1 & OFIFG ) ; // Test oscillator fault flag
    CSCTL0_H = 0; // Lock CS registers
    // Initialize LCD_C
    // ACLK , Divider = 1 , Pre - divider = 16; 4 - pin MUX
    LCDCCTL0 = LCDDIV__1 | LCDPRE__16 | LCD4MUX | LCDLP ;
    // VLCD generated internally ,
    // V2 -V4 generated internally , v5 to ground
    // Set VLCD voltage to 2.60 v
    // Enable charge pump and select internal reference for it
    LCDCVCTL = VLCD_1 | VLCDREF_0 | LCDCPEN ;
    LCDCCPCTL = LCDCPCLKSYNC ; // Clock synchronization enabled
    LCDCMEMCTL = LCDCLRM ; // Clear LCD memory
    // Turn LCD on
    LCDCCTL0 |= LCDON ;
    return ;
}

void main(void)
{

    PM5CTL0 &= ~LOCKLPM5;
    WDTCTL = WDTPW + WDTHOLD;

    config_ACLK_to_32KHz_crystal();
    Initialize_LCD ();

    TA0CTL = TASSEL__ACLK | ID__1 | MC__UP | TACLR ;
    TA0CCR0 = 10000;//maximo del contador del timer
    TA0CCTL0 = CCIE ; // Habilita interrupcion (bit CCTIMER1_A0IE ) en TIMER1_A0

    CSCTL0_H = CSKEY >> 8; // Unlock clock registers
    CSCTL1 = DCOFSEL_3 | DCORSEL; // Set DCO to 8MHz
    CSCTL2 = SELA__VLOCLK | SELS__DCOCLK | SELM__DCOCLK;
    CSCTL3 = DIVA__1 | DIVS__1 | DIVM__1; // Set all dividers
    CSCTL0_H = 0;


    //botones
    P1DIR &= ~BIT1;
    P1REN |= BIT1;
    P1OUT |= BIT1;
    P1IE |= BIT1;
    P1IES |= BIT1;
    P1IFG &= ~BIT1;

    P1DIR &= ~BIT2;
    P1REN |= BIT2;
    P1OUT |= BIT2;
    P1IE |= BIT2;
    P1IES |= BIT2;
    P1IFG &= ~BIT2;

    P1DIR &= ~BIT6;
    P1REN |= BIT6;
    P1OUT &= ~BIT6;
    P1IE |= BIT6;
    P1IES &= ~BIT6;
    P1IFG &= ~BIT6;

    P1DIR &= ~BIT7;
    P1REN |= BIT7;
    P1OUT &= ~BIT7;
    P1IE |= BIT7;
    P1IES &= ~BIT7;
    P1IFG &= ~BIT7;


    __bis_SR_register ( GIE ) ;

    P3SEL0 |= BIT4;
    P3SEL0 |= BIT5;

    P3SEL1 &= ~BIT5;
    P3SEL1 &= ~BIT4;


    //Configurar periferico
       UCA1IE |= BIT1;
       UCA1IE |= BIT0;

       UCA1IFG &= ~BIT1;
       UCA1IFG &= ~BIT0;
       // Configure USCI_A1 for UART mode
       UCA1CTLW0 = UCSWRST; // Put eUSCI in reset
       UCA1CTLW0 |= UCSSEL__SMCLK; // CLK = SMCLK

       // Baud Rate calculation
       // 8000000/(16*9600) = 52.083
       // Fractional portion = 0.083
       // User's Guide Table 21-4: UCBRSx = 0x04
       // UCBRFx = int ( (52.083-52)*16) = 1
       UCA1BR0 = 52; // 8000000/16/9600
       UCA1BR1 = 0x00;
       UCA1MCTLW |= UCOS16 | UCBRF_1 | 0x4900;
       UCA1CTLW0 &= ~UCSWRST; // Initialize eUSCI
       // Configure interruptions to receive and transmit data in register UCA1IE
       //------------

       UCA1IE |= UCRXIE;

          __no_operation();
          display_num_lcd (n, contador);

           return 0;
}
    # pragma vector = TIMER0_A0_VECTOR
    __interrupt void TIMER0_A0_ISR ( void ) {
        if(iniciado == 1){
            contador++;
            display_num_lcd(n, contador);
        }
    }


    //Port 1 ISR
# pragma vector = PORT1_VECTOR
__interrupt void Port_1 ( void ) {
       if(P1IFG & BIT1 ){
           if(UCA1IFG & UCTXIFG){
               UCA1TXBUF = '1';
           }
           P1IFG &= ~BIT1;
       }
       else if(P1IFG & BIT2){
           if(UCA1IFG & UCTXIFG){
              UCA1TXBUF = '2';
           }
           P1IFG &= ~BIT2;
       }
       else if(P1IFG & BIT6){
           if(UCA1IFG & UCTXIFG){
               UCA1TXBUF = '3';
           }

           P1IFG &= ~BIT6;
       }
       else if(P1IFG & BIT7){
           if(UCA1IFG & UCTXIFG){
                  UCA1TXBUF = '4';
               }
           P1IFG &= ~BIT7;
       }

  }
#pragma vector = USCI_A1_VECTOR
   __interrupt void USCI_A1_ISR(void){
       if(UCA1IFG & UCRXIFG){
           if(UCA1RXBUF == '2'){
               iniciado = 1;
               contador = 0;
               n = 0;
           }
           if(UCA1RXBUF == '1' ){
               n++;
               display_num_lcd(n, contador);
           }
           if(UCA1RXBUF == '3'){
               iniciado = 0;
           }

       }
   }



