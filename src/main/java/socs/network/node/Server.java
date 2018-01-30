package socs.network.node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implementation of a runnable server class for socket programming
 * @author Zhiguo Zhang; Zhaoqi Xu
 */
public class Server implements Runnable{
    private ServerSocket serverSocket;
    private Router router;
    private boolean on = true;

    /**
     * Constructor for Server class
     * @param port the process port number of server
     * @param router the router instance which has the server running
     */
    public Server(int port, Router router){
        this.router = router;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Port cannot be used");
        }
    }

    /**
     * Method to turn off server
     */
    public void turn_off() {
        this.on = false;
    }

    /**
     * Method implementing Runnable interface
     * Accepting income client connect() calls
     */
    public void run() {
        while (on) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientServiceThread client = new ClientServiceThread(clientSocket);
                client.start();
            } catch (Exception e) {
                if (e instanceof IOException)
                    System.out.println("Fail to accept");
            }
        }
    }

    class ClientServiceThread extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        /**
         * Constructor for ClientServiceThread
         * @param s the client socket to listen
         */
        public ClientServiceThread(Socket s) {
            super();
            clientSocket = s;
        }

        /**
         * Method invoked by Thread.start()
         */
        @Override
        public void run() {
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                while (true) {
                    // TODO: ADD CODE HERE
                }
            } catch (IOException ioe) {
                System.out.println("client socket streaming failed");
            }


        }
    }
}
