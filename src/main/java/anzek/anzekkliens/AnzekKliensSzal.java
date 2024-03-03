/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and openFromClientStreamIOCsatornak the template in the editor.
 */
package anzek.anzekkliens;

import anzek.anzekszerver.AnzekSzerver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * kliens szal<br>
 * @author User
 */
public final class AnzekKliensSzal extends Thread {
    
    private final Socket kliensCsatornaSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private int kliensCsatornaId = 1;
    private final AnzekSzerver anzekSzerver;

    public int getKliensSzalId() {
        return kliensCsatornaId;
    }

    /**
     * A kliens szal konstruktora<br>
     * Ez egy uzenet - egy szal<br>
     * Beallítjuk a kliens kliensCsatornaId -t, amelyet a kivalasztott "port"-tol kapunk meg.<br>
     * @param anzekSzerver a Szerver-oldali szerverpeldany objektum-hivatkozasa<br>
     * @param csatornaSocket akommunikacios csatorna<br>
     * @throws InterruptedException megszakitasi hiba<br>
     * @throws IOException I/O hiba<br>
     * @throws ClassNotFoundException nem tamogatott osztaly hibaja<br>
     * @throws ProtocolException protokolsertes hibaja<br>
     * @throws Exception egyeb hiba<br>
     */
    public AnzekKliensSzal( AnzekSzerver anzekSzerver, Socket csatornaSocket ) throws  InterruptedException,                                                                                
                                                                                       IOException,                                                                              
                                                                                       ClassNotFoundException,
                                                                                       ProtocolException,
                                                                                       Exception {
        this.anzekSzerver = anzekSzerver;
        this.kliensCsatornaSocket = csatornaSocket;
        this.kliensCsatornaId = csatornaSocket.getPort();        
    }
    
    /**
     * A kliens szal futtatoja<br>
     * A szerver oldali uzenetek olvasasa<br>
     */
    @SuppressWarnings("override")
    public void run() {     
        try {
            String input = "";
            while ((input = this.in.readUTF()) != null) {       
                this.anzekSzerver.handle(this.kliensCsatornaId, input);
            }
        } catch (EOFException ex) {
            // Kapcsolat véget ért, nincs további bemenet
            Logger.getLogger(AnzekKliensSzal.class.getName()).log(Level.INFO, "EOF() ->  ezzel az adatfolyam véget ért!");         
        } catch ( ProtocolException ex ) {            
            Logger.getLogger( AnzekKliensSzal.class.getName()).log( Level.SEVERE, null, ex );
        } catch ( IOException ex ) {                
            Logger.getLogger( AnzekKliensSzal.class.getName()).log( Level.SEVERE, null, ex );
        } finally {
            try {
                // Itt lehet felszabadítani erőforrásokat, ha szükséges
                this.closeFromClientIOCsatornak();
                Logger.getLogger(AnzekKliensSzal.class.getName()).log(Level.INFO, "Klienskapcsolat lezárva!"); 
            } catch (IOException ex) {
                Logger.getLogger(AnzekKliensSzal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Uzenet küldese a szervernek<br>
     * @param msg az uzenet<br>
     * @throws IOException I/O hiba<br>
     */
    public void sendToServer( String msg ) throws IOException {  
        this.out.writeUTF( msg );
    }
    
    /**
     * IO stream-ek megnyitasa, miutan a socket kapcsolodva van. <br>
     * @throws IOException I/O hiba<br>
     */
    public void openFromClientStreamIOCsatornak() throws IOException {
        // kliens oldal:
        this.out = new DataOutputStream(this.kliensCsatornaSocket.getOutputStream());
        // szerver oldal:
        this.in = new DataInputStream(this.kliensCsatornaSocket.getInputStream());
    }
    
    /**
     * Socket bezarasa (kapcsolat) es stream-ek bezarasa.<br>
     * @throws IOException I/O hiba<br>
     */
    public void closeFromClientIOCsatornak() throws IOException {
        // zárjuk a szerver felőli Input-stream csatornát
        this.in.close();
        // zárjuk a kliens felőli Output-stream csatornát:
        this.out.close();
        // zárjuk a kliens kapcsolatot:
        this.kliensCsatornaSocket.close();      
        System.out.println( "Kapcsolati csatornában (a kliens-socketben) az IO-streamek lezárva, a Socket is lezárva!" );
    }
}
