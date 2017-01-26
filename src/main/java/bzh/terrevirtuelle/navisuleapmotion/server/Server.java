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

import bzh.terrevirtuelle.navisuleapmotion.util.ARgeoData;
import bzh.terrevirtuelle.navisuleapmotion.util.ArCommand;
import bzh.terrevirtuelle.navisuleapmotion.util.ImportExportXML;
import bzh.terrevirtuelle.navisuleapmotion.util.ParserXML;
import bzh.terrevirtuelle.navisuleapmotion.views.PrimaryPresenter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.xml.bind.JAXBException;

/**
 *
 * @author serge
 * @date Jan 16, 2017
 */
public class Server {
/**
 * Grosse question:
 * Comment modifier le PrimaryPresenter depuis ici?
 */
    
    
    private final int port;
    private final PrimaryPresenter primaryPresenter;
    private SocketServerThread sst;
    private ParserXML customParser;

    public Server(int port, PrimaryPresenter primaryPresenter) {
        this.port = port;
        this.primaryPresenter = primaryPresenter;
        System.out.println("Starting Server");
        sst = new SocketServerThread();
        sst.start();
    }
    
    public void StopServer(){
        if(sst != null && sst.isAlive())
            sst.terminate();
    }
    
    protected void displayMessage(String msg){
        this.primaryPresenter.displayMessage(msg);
    }
    
    protected void handleCmd(ArCommand arcmd){
        if(arcmd == null)
            return;
        
        String cmd = arcmd.getArg();
        if(cmd == null)
            return;
        
        this.primaryPresenter.handleCmd(arcmd);
    }
    

    private class SocketServerThread extends Thread {

        private boolean isRunning = true;
        private HandlerServer hs;
        
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

        private Socket socket;
        private final int clientNumber;
        private boolean isRunning = true;
        private BufferedReader in;
        private PrintWriter out;

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
                    
                    ArCommand navCmd = null;
                    try {
                        navCmd = new ArCommand();
                        navCmd = ImportExportXML.imports(navCmd, new StringReader(input));
                    } catch (JAXBException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, ex.toString(), ex);
                    }
                    
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Received Message: "+navCmd.toString());
                    
                    final ArCommand cmd = navCmd;
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
                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Connection with client# " + clientNumber + " closed");
            }
        }
        
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
