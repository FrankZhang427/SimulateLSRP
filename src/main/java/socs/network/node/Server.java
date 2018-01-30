package socs.network.node;

import socs.network.message.SOSPFPacket;

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
     * @param router the router instance which has the server running
     */
    public Server(Router router){
        this.router = router;
        try {
            this.serverSocket = new ServerSocket(router.rd.processPortNumber);
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

        try {
            serverSocket.close();
            System.out.println("Server Stopped");
        } catch (IOException ioe) {
            System.out.println("Error Found stopping server socket");
            System.exit(-1);
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
                try {
                    SOSPFPacket received = (SOSPFPacket) in.readObject();
                    System.out.println("");
                    if (received.sospfType == 0) {
                        System.out.println("received HELLO from " + received.srcIP + ";");

                        // check if the target router has already been attached
                        for (int i=0; i<4; i++) {
                            if (null != router.ports[i] && router.ports[i].router2.simulatedIPAddress.equals(received.dstIP)) {
                                System.err.println("This router has already been attached!");
                                return;
                            }
                        }

                        // create a RouterDescription for the remote router
                        RouterDescription remote_rd = new RouterDescription(received.srcProcessIP,
                                received.srcProcessPort, received.srcIP);
                        // create a link of these two routers
                        Link link = new Link(router.rd, remote_rd);
                        // put it into ports[]
                        int i;
                        for (i=0; i<4; i++) {
                            if (null == router.ports[i]) {
                                router.ports[i] = link;
                                router.ports[i].router2.status = RouterStatus.INIT;
                                break;
                            }
                        }
                        // no more free port
                        if ( i == 4) {
                            System.err.println("All ports are occupied, link cannot be established.");
                            return;
                        }
                        System.out.println("set " + received.srcIP + " state to INIT;");
                        SOSPFPacket sent = new SOSPFPacket(router.rd.processIPAddress, router.rd.processPortNumber,
                                router.rd.simulatedIPAddress, received.srcIP, (short) 0,
                                "", "", null);
                        out.writeObject(sent);
                        received = (SOSPFPacket) in.readObject();
                        if (received.sospfType == 0) {
                            System.out.println("received HELLO from " + received.srcIP + ";");
                            router.ports[i].router2.status = RouterStatus.TWO_WAY;
                            System.out.println("set " + received.srcIP + " state to TWO_WAY;");
                            System.out.print(">> ");
                        }
                        else {
                            // TODO: Expecting another HELLO!
                            System.err.println("Error in received packet!");
                        }

                    } else {
                        // TODO: LSAUPDATE?
                    }
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("Object class not found");
                }
            } catch (IOException ioe) {
                System.out.println("client socket streaming failed");
            }


        }
    }
}
