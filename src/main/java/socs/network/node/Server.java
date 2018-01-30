package socs.network.node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable{
    private ServerSocket serverSocket;
    private Router router;
    private boolean on = true;

    public Server(int port, Router router){
        this.router = router;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Port cannot be used");
        }
    }

    public void turn_off() {
        this.on = false;
    }

    public void run() {
        while (on) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientHandle = new ClientThread(clientSocket);
                clientHandle.start();
            } catch (Exception e) {
                if (e instanceof IOException)
                    System.out.println("Fail to accept");
            }
        }

        try {
            serverSocket.close();
            System.out.println("Server Stopped");
        } catch (IOException ioe) {
            System.out.println("Error Found stopping server socket");
            System.exit(-1);
        }
    }

    class ClientThread extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        public ClientThread(Socket s) {
            clientSocket = s;
        }

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
