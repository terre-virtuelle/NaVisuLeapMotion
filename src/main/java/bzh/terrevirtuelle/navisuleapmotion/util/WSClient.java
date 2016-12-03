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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

/**
 *
 * @author Di Falco Nicola
 */
public class WSClient extends WebSocketClient{
    
    private final String ROUTE = "Route0.nds";
    private ParserXML customParser;
    private List<ARgeoData> static_ARgeoDataArray;

    public List<ARgeoData> getStatic_ARgeoDataArray() {
        return static_ARgeoDataArray;
    }
    
    private List<String> listID_Rep = new LinkedList<>();

    public WSClient( URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }
    
    public WSClient( URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println("Connection Opened");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "Received from Navisu: " + message );
        listID_Rep.add(message);
    }

  //  @Override
    public void onFragment( Framedata fragment ) {
        System.out.println( "Received fragment: " + new String( fragment.getPayloadData().array() ) );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
    
    public void ws_request() throws NotYetConnectedException{
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>NaVigationDataSetCmd</cmd><arg>%s</arg></arCommand>", ROUTE);
        System.out.println("Sending: " + cmd);
        this.send(cmd);
        static_ARgeoDataArray = handleRepStaticData();
        //new WebSock(cmd).execute();
    }

    public List<String> getListID_Rep() {
        return listID_Rep;
    }

    public void setListID_Rep(List<String> listID_Rep) {
        this.listID_Rep = listID_Rep;
    }
    
    private List<ARgeoData> handleRepStaticData() {
        Logger.getLogger("WSClient").log(Level.INFO, "handleRepStaticData");
        String message = "";
        List<String> messageList = this.getListID_Rep();
        boolean done = false;
        while (!done) {
            if (messageList.size() > 0 && messageList.get(0) != null) {
                message = messageList.get(messageList.size() - 1);
                done = true;
            } else { 
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
            
        System.out.println("Received: " + message);

        return response(message);
    }
    
    private List<ARgeoData> response(String resp) {
        String ans = "";
        Logger.getAnonymousLogger().log(Level.WARNING, resp);
        List<ARgeoData> argeoDatasList;
        if (resp != null) {
            customParser = new ParserXML(resp);
            customParser.process();
            argeoDatasList = customParser.getARgeoDatas();
            return argeoDatasList;
        }
        return null;
    }
    
    public static WSClient getInstance() {
        WSClient wsc = null;
        try {
            wsc = new WSClient( new URI( "ws://localhost:8787/navigation" ), new Draft_10() );
        } catch (URISyntaxException ex) {
            Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wsc;
    }
    
    public static WSClient getInstance(String ip) {
        WSClient wsc = null;
        try {
            wsc = new WSClient( new URI( "ws://" + ip + ":8787/navigation" ), new Draft_10() );
        } catch (URISyntaxException ex) {
            Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wsc;
    }
}
