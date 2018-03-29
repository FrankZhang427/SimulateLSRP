package socs.network.message;

import java.io.*;
import java.util.Vector;

public class SOSPFPacket implements Serializable {

  //for inter-process communication
  public String srcProcessIP;
  public short srcProcessPort;

  //simulated IP address
  public String srcIP;
  public String dstIP;

  //common header
  public short sospfType; //0 - HELLO, 1 - LinkState Update, 2 - Periodical Hello
  public String routerID;

  //used by HELLO message to identify the sender of the message
  //e.g. when router A sends HELLO to its neighbor, it has to fill this field with its own
  //simulated IP address
  public String neighborID; //neighbor's simulated IP address

  //used by LSAUPDATE
  public Vector<LSA> lsaArray = null;

  //weight
  public short weight;

  /**
   * Constructor for SOSPFPacket class
   * @param srcProcessIP source process IP
   * @param srcProcessPort source Process Port
   * @param srcIP source simulated IP
   * @param dstIP destination simulated IP
   * @param sospfType Packet type 0 - HELLO, 1 - LSAUPDATE, 2 - Periodical Hello
   * @param routerID Router ID
   * @param neighborID Neighbor ID
   * @param lsaArray A vector of LSA for LSAUPDATE
   */
  public SOSPFPacket(String srcProcessIP, short srcProcessPort,
                     String srcIP, String dstIP, short sospfType,
                     String routerID, String neighborID, Vector<LSA> lsaArray,
                     short weight) {
    this.srcProcessIP = srcProcessIP;
    this.srcProcessPort = srcProcessPort;
    this.srcIP = srcIP;
    this.dstIP = dstIP;
    this.sospfType = sospfType;
    this.routerID = routerID;
    this.neighborID = neighborID;
    this.lsaArray = lsaArray;
    this.weight = weight;
  }
}
