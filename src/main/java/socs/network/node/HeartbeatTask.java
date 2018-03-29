package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.SOSPFPacket;
import socs.network.message.LinkDescription;

import java.util.TimerTask;
import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;

public class HeartbeatTask extends TimerTask{
    private Router router;
    private int timeout = 10000;
    public HeartbeatTask(Router router) {
        super();
        this.router = router;
    }

    @Override
    public void run() {
        for (int i=0; i<4; i++) {
            if (null == router.ports[i]) continue;
            if (router.ports[i].router2.status == RouterStatus.TWO_WAY) {
                SOSPFPacket packet = new SOSPFPacket(router.ports[i].router1.processIPAddress, router.ports[i].router1.processPortNumber,
                        router.ports[i].router1.simulatedIPAddress, router.ports[i].router2.simulatedIPAddress, (short) 2,
                        "", "", null, router.ports[i].weight);

                try {
                    Socket clientSocket = new Socket(router.ports[i].router2.processIPAddress, router.ports[i].router2.processPortNumber);
                    // set the socket timeout
                    clientSocket.setSoTimeout(timeout);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    // send periodical hello packet
                    out.writeObject(packet);
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    // read a feedback packet
                    SOSPFPacket received = (SOSPFPacket) in.readObject();
                    // LSP is expected
                    if (received.sospfType == 1) {
//                        System.out.println("received feedback from " + received.srcIP + ";");
                        Vector<LSA> lsaArray = received.lsaArray;
                        // Obtaining an iterator
                        Iterator it = lsaArray.iterator();

                        while (it.hasNext()) {
                            LSA lsa = (LSA) it.next();
                            if (router.lsd._store.containsKey(lsa.linkStateID)) {
                                if (lsa.lsaSeqNumber > router.lsd._store.get(lsa.linkStateID).lsaSeqNumber) {
                                    router.lsd._store.put(lsa.linkStateID, lsa);
                                    System.out.println("LSA of " + lsa.linkStateID + " has been updated.");
                                    router.forwardLSP(received);
                                }
                            } else {
                                router.lsd._store.put(lsa.linkStateID, lsa);
                                router.forwardLSP(received);
                            }
                        }
                    } else {
                        System.err.println("Error in received packet!");
                    }
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (Exception e) {
                    // feedback not received before timeout OR connection failed
                    // update LSA of remote router
                    String remoteIP = router.ports[i].router2.simulatedIPAddress;
                    LSA remoteLSA = router.lsd._store.get(remoteIP);
                    remoteLSA.lsaSeqNumber++;
                    for (LinkDescription ld : remoteLSA.links) {
                        if (ld.linkID.equals(router.rd.simulatedIPAddress)) {
                            remoteLSA.links.remove(ld);
                        }
                    }

                    // update LSA of local router
                    LSA localLSA = router.lsd._store.get(router.rd.simulatedIPAddress);
                    localLSA.lsaSeqNumber++;
                    for (LinkDescription ld : localLSA.links) {
                        if (ld.linkID.equals(remoteIP)) {
                            localLSA.links.remove(ld);
                        }
                    }

                    // delete link in ports[]
                    System.out.println("\n" + router.ports[i].router2.simulatedIPAddress + " is disconnected.");
                    router.ports[i] = null;

                    // create lsa array
                    Vector<LSA> lsaArray = new Vector<LSA>();
                    Iterator it = router.lsd._store.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        lsaArray.add((LSA) pair.getValue());
                    }

                    // share the LSP with all neighbors
                    router.broadcastLSP(lsaArray);
                    System.out.print(">> ");
                    // close the socket?
                }
            }
        }
    }
}
