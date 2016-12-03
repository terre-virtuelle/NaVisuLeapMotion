/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2016 Di Falco Nicola
 *
 * NaVisuLeapMotion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NaVisuLeapMotion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bzh.terrevirtuelle.navisuleapmotion.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.drafts.Draft_10;

/**
 *
 * @author Di Falco Nicola
 */
public class Main {
    public static WSClient wsc;
    private static Thread thread0;
    
    public static void main( String[] args ) throws URISyntaxException, IOException, InterruptedException {
        initWSC();
        while(wsc == null){
            Thread.sleep(250);
            System.out.println("Waiting connection");
        }
        System.out.println("**************************");
        wsc.ws_request();
        System.out.println("##########################");
        
        wsc.getStatic_ARgeoDataArray().forEach((element) -> System.out.println(element));
        System.out.println("##########################");
        Thread.sleep(500);
        System.out.println("**************************");
        wsc.ws_request();
        System.out.println("##########################");
        
        wsc.getStatic_ARgeoDataArray().forEach((element) -> System.out.println(element));
        System.out.println("##########################");
        
    }
    
    public static void initWSC(){
       thread0 =new Thread((Runnable) () -> {
            System.out.println("Current Time =" + new Date());
            try {
                System.out.println("*********** Connecting *************");
                wsc = new WSClient( new URI( "ws://localhost:8787/navigation" ), new Draft_10() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
                wsc.connect();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread0.start();
    }
    
    
}
