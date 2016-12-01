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
    private List<String> listID_Rep = new LinkedList<String>();

    public WSClient( URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    public WSClient( URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "received: " + message );
        listID_Rep.add(message);
    }

  //  @Override
    public void onFragment( Framedata fragment ) {
        System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
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

    public static void main( String[] args ) throws URISyntaxException {
        System.out.println("*********** Connecting *************");
        WSClient c = new WSClient( new URI( "ws://localhost:8787" )); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        c.connect();
    }

    public List<String> getListID_Rep() {
        return listID_Rep;
    }

    public void setListID_Rep(List<String> listID_Rep) {
        this.listID_Rep = listID_Rep;
    }
}
