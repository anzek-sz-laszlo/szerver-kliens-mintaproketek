/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package anzek;


/**
 *
 * @author DELL
 */

// Példa com.sun.net.httpserver használatára
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

public class BongeszohozOnalloSzerverKapcsolat{
        
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(7777), 0);
        server.createContext("/message", new MessageHandler());
        server.setExecutor(null);
        server.start();
    }

    private static class MessageHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            // a szerver üzleti logikája 
            // amely itt most mindössze a böngésző-kliens beérkező kérésének a feldolgozása lesz:
            InputStream requestBody = exchange.getRequestBody();
            InputStreamReader isr = new InputStreamReader(requestBody);
            BufferedReader br = new BufferedReader(isr);
            // dinamikus string formátum:
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }  
            
            // az URL-en utazó JSON-sztringet dekódoljuk:
            String urlDecodedJson = URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8).replace("&", "\r\n");
            
            // a Normalizer osztály a diakritikus jelek (ékezetek és más vezérlőkaraktereket)
            // eltávolításához használjuk. 
            // A Normalizer.Form.NFD az NFC (Normalization Form Compatibility) formátumot használja és mellé illeszti az alapkaraktert, 
            // végül a "\\p{M}" (amely egy reguláris kifejezés) eltávolítja a diakritikus karaktereket.
            urlDecodedJson = Normalizer.normalize(urlDecodedJson, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

            // A válasz body és a formázott JSON szöveg logolása a konzolra:
            System.out.println("\nKérés URL-kódolt JSON-sztringje: \n" + sb.toString());
            System.out.println("\nKérés tartalma URL-dekódolt JSON-sztringje: \n" + urlDecodedJson);
       
            // Válasz elkészítése
            String response = "Hello POSTMAN, vagy Te a bongeszo vagy?\n\n" 
                            + "GET response message\n" 
                            + urlDecodedJson; 
       
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }            
        }
        
        //@Override
        //public void handle(HttpExchange exchange) throws IOException {
        //    System.out.println("A böngésző kérésének üzelti tartalma: " + exchange.getRequestBody());
        //    String message = "Hello POSTMAN, vagy Te a bongeszo vagy?";
        //    exchange.sendResponseHeaders(200, message.length());
        //    try (OutputStream os = exchange.getResponseBody()) {
        //        os.write(message.getBytes());
        //    }
        //}
    }
}
