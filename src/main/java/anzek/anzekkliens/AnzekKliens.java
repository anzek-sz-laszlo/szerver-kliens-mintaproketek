/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anzek.anzekkliens;

import static anzek.SzerverMain.PORT;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author User
 */
public final class AnzekKliens extends Thread {

    private Socket kliensSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final String dadaStream = "0123456789abcdefghaijklmnopqrstxyvzABCDEFGHIJKLMNOPQRSTXYVZ..."+
                                      "123456789abcdefghaijklmnopqrstxyvzABCDEFGHIJKLMNOPQRSTXYVZ..."+
                                      "23456789abcdefghaijklmnopqrstxyvzABCDEFGHIJKLMNOPQRSTXYVZ...§";
    public AnzekKliens( final String host, final int port ) {

        // Probálkozunk kapcsolatot létesíteni a szerverrel
        
        try {
            
            System.out.println( "Kliens oldali kapcsolódás a szerverhez: 'http://" + host + "':" + port );
            this.kliensSocket = new Socket( host, port );
            
            if( this.kliensSocket.isConnected() ){   
                // Ha sikerült kapcsolódni,
                // akkor megnyitjuk a stream-eket                 
                this.open();
                // majd küldünk egy üdvözlő üzenetet a szervernek:  
                System.out.println( "KliensSzál elindult és kapcsolódva ehhez a szerverhez: " + host + " és port: " + port );
                this.send("HELLO SERVER");
                this.send("ÉN A KLIENS, ÜDVÖZÖLLEK KEDVES SZERVER!\nKüldök neked egy adatfolyamot:");
                for(int i=0; i < this.dadaStream.length()-1; i++ ){                    
                    this.send(this.dadaStream.substring(i, i+1) );
                }             
                this.close();
            }else{      
                throw new IOException("Szerver nem érhető el!");
            }
        } catch (IOException e) {  
            // Sikertelen kapcsolodas eseten hiabuzenet.. 
            System.out.println("Nem sikerült csatlakozni a szerverhez. " + e.getMessage());
        }
    }

    /**
     * A szal-futtato, amely feladata a szervertol kapott uzenetek olvasasa<br>
     */
    @Override
    public void run() {
        try {        
            while (true) {
                String message = in.readUTF();
                System.out.println( "Üzenet a szervertől: " + message );
            }
        } catch (IOException e) {       
            // Ha olvasasi problemak voltak, a kapcsolat megszakitasa kovetkezik:
            this.close();
            System.out.println( "Kapcsolat megszakítva. " + e.getMessage() );
        }
    }

    /**
     * IO stream-ek megnyítasa, miutan a kapcsolat letrejott<br>
     * @throws IOException I/O hiba<br>
     */
    public void open() throws IOException {       
        this.in = new DataInputStream( this.kliensSocket.getInputStream() );
        this.out = new DataOutputStream( this.kliensSocket.getOutputStream() );
    }

    /**
     * Uzenet kuldese a szervernek.<br>
     * @param msg az uzenet<br>
     * @throws java.io.IOException I/O hiba<br>
    */
    public void send(String msg) throws IOException {       
        this.out.writeUTF(msg);
    }
    public void sendToServer(String message) throws IOException {
        this.send(message);
    }    

    /**
     * Lezarjuk a kliens stream-eket.<br>
     */
    public void close() {     
        try {      
            this.out.close();
            this.in.close();
            this.kliensSocket.close();
        } catch (IOException e) {        
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * @param args Program parameter argumentumlista-tomb<br>
     * @throws java.io.IOException I/O hiba<br>
     * @throws java.lang.ClassNotFoundException nem tamogatott osztaly hiba<br>
     */
    public static void main( String[] args) throws IOException, ClassNotFoundException {

        // (A) Kliens oldal : valamifele uzenetet kuld a szerver fele es valaszt var :    
        AnzekKliens ak = new AnzekKliens( "localhost", PORT );
        ak.start();  
        ak.close();
    }
}
