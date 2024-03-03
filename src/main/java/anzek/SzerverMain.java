/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anzek;

import anzek.anzekszerver.AnzekSzerver;
import java.io.IOException;

/**
 *
 * @author User
 */
public class SzerverMain {
    
    public static final int PORT = 7777;
    /* ha "true" - szerver | ha "false" - kliens */
    
    /**
     * @param args Program parameter argumentumlista-tomb<br>
     * @throws java.io.IOException I/O hiba<br>
     * @throws java.lang.ClassNotFoundException nem tamogatott osztaly hiba<br>
     */
    public static void main( String[] args) throws IOException, ClassNotFoundException {

    // (A) Szerver-oldal : valamifele kerdest var es valaszol:
        AnzekSzerver as = new AnzekSzerver( PORT );
        as.start();
    }
}
