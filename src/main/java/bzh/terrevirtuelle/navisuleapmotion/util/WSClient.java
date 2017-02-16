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
 * Represents a WebSocketClient. Enables communication from RA_NaVisu to
 * Navisu's Server
 *
 * @author Di Falco Nicola
 */
public class WSClient extends WebSocketClient {

    /**
     * Default Route used to test communication
     */
    private final String ROUTE = "Route0.nds";

    /**
     * XML Parser to unmarshall responses from NaVisu
     */
    private ParserXML customParser;

    /**
     * Stores the ARgeoData list (filled upon incoming message)
     */
    private List<ARgeoData> static_ARgeoDataArray;

    /**
     * Stores the received messages
     */
    private List<String> listID_Rep = new LinkedList<>();

    /**
     * Gets the ARgeoData list
     *
     * @return The ARgeoData list
     */
    public List<ARgeoData> getStatic_ARgeoDataArray() {
        return static_ARgeoDataArray;
    }

    /**
     * Constructor using the server URI and a Draft
     *
     * @param serverUri The server URI (In this application case:
     * ws://ServerIP:8787/navigation)
     * @param draft The Draft used
     */
    public WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    /**
     * Constructor using the server URI
     *
     * @param serverURI The server URI (In this application case:
     * ws://ServerIP:8787/navigation)
     */
    public WSClient(URI serverURI) {
        super(serverURI);
    }

    /**
     * Actions made once the connection is opened
     *
     * @param handshakedata The Server handshake
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection Opened");
    }

    /**
     * Actions made once a message arrives (stored here in the listID_Rep)
     *
     * @param message The incoming message
     */
    @Override
    public void onMessage(String message) {
        System.out.println("Received from Navisu: " + message);
        listID_Rep.add(message);
    }

    /**
     * Actions made once a message's fragment arrives
     *
     * @param fragment A data fragment
     */
    public void onFragment(Framedata fragment) {
        System.out.println("Received fragment: " + new String(fragment.getPayloadData().array()));
    }

    /**
     * Actions made once the connection is closed
     *
     * @param code The close code
     * @param reason The reason behind closed connection
     * @param remote Is the connection closed by the server
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
    }

    /**
     * Actions made once a error happen
     *
     * @param ex The Exception
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    /**
     * Sends the device's IP on which this app is running to NaVisu Server. When
     * the server receives this message, it will try to connect to the RA_Navisu
     * server
     *
     * @throws NotYetConnectedException Thrown if the socket is not connected
     * yet
     * @throws UnknownHostException Thrown if the socket is connected to an
     * unkown host
     */
    public void sendIP() throws NotYetConnectedException, UnknownHostException {
        String ip = WSClient.getIPAddress(true);
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>IPInfo</cmd><arg>%s</arg></arCommand>", ip);
        System.out.println("Sending: " + cmd);
        this.send(cmd);
    }

    /**
     * Sends to NaVisu server to inform that the RA_Navisu server will be
     * closing (initialize proper deconnection procedure in NaVisu client)
     *
     * @throws NotYetConnectedException Thrown if the socket is not connected
     * yet
     * @throws UnknownHostException Thrown if the socket is connected to an
     * unkown host
     */
    public void sendClose() throws NotYetConnectedException, UnknownHostException {
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>ServerClosing</cmd><arg></arg></arCommand>");
        System.out.println("Sending: " + cmd);
        this.send(cmd);
    }

    /**
     * Sends a message to NaVisu server to ask information about Route0.nds
     *
     * @throws TimeLimitExceededException Thrown if timed out
     */
    public void ws_request() throws TimeLimitExceededException {
        String cmd = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><arCommand><cmd>NaVigationDataSetCmd</cmd><arg>%s</arg></arCommand>", ROUTE);
        System.out.println("Sending: " + cmd);
        this.send(cmd);
        static_ARgeoDataArray = handleRepStaticData();
    }

    /**
     * Gets the message list
     *
     * @return The Message List
     */
    public List<String> getListID_Rep() {
        return listID_Rep;
    }

    /**
     * Sets the message list
     *
     * @param listID_Rep The value of the new message list
     */
    public void setListID_Rep(List<String> listID_Rep) {
        this.listID_Rep = listID_Rep;
    }

    /**
     * Try to gather ARgeoData on any message stored in listID_Rep (pop)
     *
     * @return The ARgeoData list
     * @throws TimeLimitExceededException Thrown if after 100 trials, the
     * function does not manage to get on message from the message list
     */
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
                if (message.length() == 0) {
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
                messageList = this.getListID_Rep();
                if (trials > 100) {
                    throw new TimeLimitExceededException("Cannot manage to get server answer");
                }
            }
        }

        System.out.println("Received: " + message);

        return response(message);
    }

    /**
     * Unmarshall the ARgeoData from the message given in parameter
     *
     * @param message The message to unmarshall
     * @return The ARgeoData list extracted from the message
     */
    private List<ARgeoData> response(String message) {
        Logger.getAnonymousLogger().log(Level.WARNING, message);
        List<ARgeoData> argeoDatasList;
        if (message != null) {
            customParser = new ParserXML(message);
            customParser.process();
            argeoDatasList = customParser.getARgeoDatas();
            return argeoDatasList;
        }
        return null;
    }

    /**
     * Returns an instance of WSClient connected to localhost
     *
     * @return An instance of WSClient. If the URI built from localhost is
     * incorrect, null will be returned
     */
    public static WSClient getInstance() {
        WSClient wsc = null;
        try {
            wsc = new WSClient(new URI("ws://localhost:8787/navigation"), new Draft_10());
        } catch (URISyntaxException ex) {
            Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wsc;
    }

    /**
     * Returns an instance of WSClient connected to the server whose IP is given
     * in parameter
     *
     * @param ip The server's IP to connect to
     * @return An instance of WSClient. If the URI built from the IP address is
     * incorrect, null will be returned
     */
    public static WSClient getInstance(String ip) {
        WSClient wsc = null;
        try {
            wsc = new WSClient(new URI("ws://" + ip + ":8787/navigation"), new Draft_10());
        } catch (URISyntaxException ex) {
            Logger.getLogger(WSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wsc;
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
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
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else if (!isIPv4) {
                            int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                            return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }
}
