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

import bzh.terrevirtuelle.navisuleapmotion.views.PrimaryPresenter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static List<String> static_Data = new LinkedList<>();
    
    public static List<String> getStatic_Data(){
        return static_Data;
    }

    public Server(int port, PrimaryPresenter primaryPresenter) {
        this.port = port;
        this.primaryPresenter = primaryPresenter;
        System.out.println("Starting Server");
        new SocketServerThread().start();
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                int clientNumber = 0;
                ServerSocket listener = new ServerSocket(port);
                {
                    while (true) {
                        new HandlerServer(listener.accept(), clientNumber++).start();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular
     * socket. The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class HandlerServer extends Thread {

        private final Socket socket;
        private final int clientNumber;

        public HandlerServer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the client a welcome
         * message then repeatedly reading strings and sending back the
         * capitalized version of the string.
         */
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Logger.getLogger(Server.class.getName()).log(Level.INFO, "Client connected: "+clientNumber);
                
                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                    System.out.println("Waiting Message");
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Message received: "+input);
                    static_Data.add(input);
                    
                    out.println(input.toUpperCase());
                }
                
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message. In this case we just write the message to the
         * server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
}
