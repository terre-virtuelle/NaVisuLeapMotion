/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2017 Di Falco Nicola
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
package bzh.terrevirtuelle.navisuleapmotion.server;

import bzh.terrevirtuelle.navisuleapmotion.util.ParserXML;
import bzh.terrevirtuelle.navisuleapmotion.views.PrimaryPresenter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * 
 * @author Di Falco Nicola
 * @author serge
 */
public class Server {
    
    /**
     * Port used for communication (On this app, the value
     * shall be 8899)
     */
    private final int port;
    
    /**
     * The PrimaryPresenter (can be seen as an app page) using this Server instance
     */
    private final PrimaryPresenter primaryPresenter;
    
    /**
     * Inner class of this Project. Manages connections requests
     */
    private SocketServerThread sst;
    
    /**
     * XML Parser Used (alternative to JAXB)
     */
    private ParserXML customParser;

    /**
     * Main constructor of Server
     * @param port The port to listen to
     * @param primaryPresenter The PrimaryPresenter linked to this Server
     */
    public Server(int port, PrimaryPresenter primaryPresenter) {
        this.port = port;
        this.primaryPresenter = primaryPresenter;
        System.out.println("Starting Server");
        sst = new SocketServerThread();
        sst.start();
    }
    
    /**
     * Stops Server
     */
    public void StopServer(){
        if(sst != null && sst.isAlive())
            sst.terminate();
    }
    
    /**
     * Displays message in the PrimaryPresenter
     * @param msg The message to display
     */
    protected void displayMessage(String msg){
        this.primaryPresenter.displayMessage(msg);
    }
    
    /**
     * Handles the command (ArCommand format) given, and gives the command name 
     * to the PrimaryPresenter for processing
     * @param arcmd The command message to handle
     */
    protected void handleCmd(String arcmd){
        if(arcmd == null)
            return;
        
        customParser = new ParserXML(arcmd);
        
        String cmd = customParser.getCmd();
        Logger.getLogger(Server.class.getName()).log(Level.INFO, cmd);
        if(cmd == null)
            return;
        
        this.primaryPresenter.handleCmd(cmd);
    }
    
    /**
     * Inner class Handling connections requests
     */
    private class SocketServerThread extends Thread {

        /**
         * Is the server running
         */
        private boolean isRunning = true;
        
        /**
         * Inner class handling communications with a Client
         */
        private HandlerServer hs;
        
        /**
         * Starts Thread
         */
        @Override
        public void run() {
            try {
                int clientNumber = 0;
                ServerSocket listener = new ServerSocket(port);

                {
                    while (isRunning) {
                        hs = new HandlerServer(listener.accept(), clientNumber++);
                        hs.start();
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Stops Thread
         */
        public void terminate(){
            hs.terminate();
            isRunning = false;
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular
     * socket. The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private class HandlerServer extends Thread {

        /**
         * Socket to handle communication
         */
        private Socket socket;
        
        /**
         * Client's number
         */
        private final int clientNumber;
        
        /**
         * Is the handler running
         */
        private boolean isRunning = true;
        
        /**
         * BufferedRead to read the Socket
         */
        private BufferedReader in;
        
        /**
         * PrintWriter to write in the Socket
         */
        private PrintWriter out;

        /**
         * Main Constructor
         * @param socket The socket to listen/write to
         * @param clientNumber Client's number
         */
        public HandlerServer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the client a welcome
         * message then repeatedly reading strings and sending back the
         * capitalized version of the string.
         */
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Client connected: "+clientNumber);
                
                // Get messages from the client, line by line; return them
                // capitalized
                while (isRunning) {
                    System.out.println("Waiting Message");
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
                    
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Received Message: "+input);
                    
                    final String cmd = input;
                    Platform.runLater(() ->  handleCmd(cmd));
                }
                
            } catch (IOException e) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Couldn't close a socket, what's going on?");
                }
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Connection with client# " + clientNumber + " correctly closed");
            }
        }
        
        /**
         * Stops the Thread
         */
        public void terminate(){
            try {
                isRunning = false;
                in.close();
                in = null;
                out.close();
                out = null;
                socket.close();
                socket = null;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
