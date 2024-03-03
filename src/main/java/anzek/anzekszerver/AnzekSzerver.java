/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and openFromClientStreamIOCsatornak the template in the editor.
 */
package anzek.anzekszerver;
        
import anzek.anzekkliens.AnzekKliensSzal;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Aszerveroldali osztaly<br>
 * @author User
 */
public class AnzekSzerver extends Thread {

    private int port = 7777;  
    
    /**
     * A komunikacios csatorna<br>
     */    
    private ServerSocket szerverSocket;

    /**
     * Szerver port, kliensek listaja<br>
     * ebben gyujtjuk be az osszes kliens szalat <br>
     */
    private List<AnzekKliensSzal> clients = new ArrayList<>();

    /**
     * A "szerver" konstruktora<br>
     * A szerver inditasa a megadott porton<br>
     * @param port a port (7777) amelyen a lekredezest, uzenetetet adatjuk/fogadjuk<br>
     * @throws java.io.IOException I/O hiba <br>
     * @throws java.lang.ClassNotFoundException nem tamogatott osztaly hiba<br>
     */
    public AnzekSzerver( int port ) throws IOException ,ClassNotFoundException {       
        this.port = port;       
        try {           
            this.szerverSocket = new ServerSocket( this.port );  
            System.out.println( "\n1, A szerver indulásra kész, (létrejött egy szerev-socket) a következő porton: " 
                                + this.port 
                                + "\n2, A szerver sikeresen elindult" );
        } catch (IOException e) {         
            System.out.println(e);
        }
    }

    /**
     * A szerver szal futtatoja<br>
     */
    @Override
    public void run() {  
        boolean stop = false;
        boolean pause = false;
        boolean sqlStop = false;
        boolean sqlPause = false;        
        try {                 
            // Egy végtelen ciklusban
            // Kliensekre várakozunk, melyek csatlakozni szeretnének 
            // a szerver ezen portjához és erőforrásvégpontjához:
            // minimis változatban:
            //            while(true){
            //                this.addClient();
            //            }
            while ( ! stop ) {         
                // Figyelünk és jönne ehhez a sockethez, elfogajuk a csatlakozni próbálkozó kliens(eke)t. 
                if( ! pause){
                    this.addClient(); 
                }
                if(sqlStop){
                    // itt lehet valamifajta feltétel kiértékeléssel leállítani (stop) a szervert! Vagy szüneteltetni (pause)
                    stop = true; 
                }
                if(sqlPause){
                    pause = true;
                }
            }
        } catch (IOException e) {           
            System.out.println(e);
        } catch (Exception ex) {         
            Logger.getLogger(AnzekSzerver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * A kliens üzeneteket olvassuk<br>
     * @param csatornaId a KLIENS-csatorna-azonosito
     * @param input az bekuldensdo szoveges tartalom<br>
     * @throws IOException I/O hiba<br>
     */
    public synchronized void handle(int csatornaId, String input ) throws IOException{                
        // a klienstől érkező üzenetek konzolra íratása:
        System.out.println( input );        
        if( input.equalsIgnoreCase( "HELLO SERVER" ) ){
            // Ha a kliens üzenet a "HELLO SERVER", akkor a szerver a kliensnek "HELLO KLIENS" -el válaszol.
            this.clients.get( this.findClient(csatornaId) ).sendToServer( "--- SZIA KLIENS!" );
        }        
    }
    
    /**
     * Megnyítjuk a kliens IO stream-eket.<br>
     * Hozzáadjuk az uj klienset a kliens listához <br>
     * @throws IOException I/O hiba<br>
     * @throws Exception egyeb hiba<br>
     */
    public void addClient() throws IOException ,Exception {    
        
        System.out.println( "\n3, Egy bármilyen 'Kliens'-re várakozunk \n-- addig is a 'szerverStocket.accept()' blokkolja a kod tovabb futast..." ); 
 
        AnzekKliensSzal aKliensSzal = new AnzekKliensSzal( this, this.szerverSocket.accept() );     
        // Megnyitjuk a kliens IO stream-eket
        aKliensSzal.openFromClientStreamIOCsatornak();     
        // Elinditjuk a szalat:
        aKliensSzal.start();    
        // Hozzaadjuk a klienset a kliens listahoz <ArrayList>
        this.clients.add(aKliensSzal );         
        aKliensSzal.sendToServer( "EN A KLIENS (A BACKEND_176) UDVOZOLLEK TEGED, SZERVER!" );
        System.out.println( "\nKliens elfogadva." );  
    }

    /**
     * A kliens eltávolítása azonositó kereséssel<br> 
     * @param csatornaId a KLIENS-csatorna-azonosito<br> 
     */
    public void removeClient( int csatornaId ) {      
        this.clients.remove( this.findClient( csatornaId ) );  
        System.out.println( "\nA kliens eltávolítva." );
    }
    
    /**
     * Kliens keresése azonosító alapján, a klienslistából<br>
     * @param csatornaId a KLIENS-csatorna-azonosito<br> 
     * @return visszaadja a KLIENS-poziciot a kliens-listabol<br> 
     */
    public int findClient( int csatornaId ) {     
        int findClient = -1;       
        for ( int i = 0; i < this.clients.size(); i++ ) {       
            if ( this.clients.get(i).getKliensSzalId() == csatornaId ) {       
                findClient = i;
            }
        }
        
        return findClient;
    }
    
}

