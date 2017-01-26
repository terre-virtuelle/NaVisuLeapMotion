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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.TimeLimitExceededException;
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
    private List<String> listID_Rep = new LinkedList<>();

    public List<ARgeoData> getStatic_ARgeoDataArray() {
        return static_ARgeoDataArray;
    }
    

    public WSClient( URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }
    
    public WSClient( URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println("Connection Opened");
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
    
    public void sendIP() throws NotYetConnectedException, UnknownHostException{
        String ip = WSClient.getIPAddress(true);
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>IPInfo</cmd><arg>%s</arg></arCommand>", ip);
        System.out.println("Sending: " + cmd);
        this.send(cmd);
    }
    
    public void sendClose() throws NotYetConnectedException, UnknownHostException{
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>ServerClosing</cmd><arg></arg></arCommand>");
        System.out.println("Sending: " + cmd);
        this.send(cmd);
    }
    
    public void ws_request() throws TimeLimitExceededException{
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>NaVigationDataSetCmd</cmd><arg>%s</arg></arCommand>", ROUTE);
        System.out.println("Sending: " + cmd);
        this.send(cmd);
        static_ARgeoDataArray = handleRepStaticData();
    }

    public List<String> getListID_Rep() {
        return listID_Rep;
    }

    public void setListID_Rep(List<String> listID_Rep) {
        this.listID_Rep = listID_Rep;
    }
    
    private List<ARgeoData> handleRepStaticData() throws TimeLimitExceededException {
        Logger.getLogger("WSClient").log(Level.INFO, "handleRepStaticData");
        String message = "";
        List<String> messageList = this.getListID_Rep();
        boolean done = false;
        int trials = 0;
        while (!done) {
            if (messageList.size() > 0 && messageList.get(0) != null) {
                message = messageList.get(messageList.size() - 1);
                done = true;
                if(message.length() == 0){
                    this.getListID_Rep().remove(messageList.size() - 1);
                    done = false;
                }
            } else { 
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                trials++;
                if(trials > 100)
                    throw new TimeLimitExceededException("Cannot manage to get server answer");
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
    
      /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
