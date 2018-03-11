package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.LinkDescription;

import java.util.*;

public class LinkStateDatabase {

  //linkID => LSAInstance
  HashMap<String, LSA> _store = new HashMap<String, LSA>();

  private RouterDescription rd = null;

  private WeightedGraph wg;
  private HashSet<String> settled;
  private HashSet<String> unsettled;
  private HashMap<String, Integer> distance;
  private HashMap<String, String> predecessors;

  public LinkStateDatabase(RouterDescription routerDescription) {
    rd = routerDescription;
    LSA l = initLinkStateDatabase();
    _store.put(l.linkStateID, l);
  }

  /**
   * output the shortest path from this router to the destination with the given IP address
   */
  String getShortestPath(String destinationIP) {
    //TODO: fill the implementation here
    String source = rd.simulatedIPAddress;
    wg = new WeightedGraph(this);
    settled = new HashSet<String>();
    unsettled = new HashSet<String>();
    distance = new HashMap<String, Integer>();
    predecessors = new HashMap<String, String>();

    distance.put(source,0);
    unsettled.add(source);

    while (unsettled.size() > 0) {
      String node = getMinimum(unsettled);
      unsettled.remove(node);
      findMinimalDistance(node);
      settled.add(node);
    }

    return getPath(destinationIP);
  }

  private void findMinimalDistance(String node) {
    List<String> neighbors = getNeighbors(node);
    for (String s : neighbors) {
      if (getShortestDistance(s) > getShortestDistance(node) + wg.edges[wg.find(node)][wg.find(s)]) {
        distance.put(s, getShortestDistance(node) + wg.edges[wg.find(node)][wg.find(s)]);
        predecessors.put(s, node);
        unsettled.add(s);
      }
    }
  }

  private List<String> getNeighbors(String node) {
    List<String> neighbors = new ArrayList<String>();
    int index = wg.find(node);
    for (int i = 0; i < wg.edges.length; i++) {
      if (wg.edges[index][i] >= 0 && !settled.contains(wg.myID[i])) neighbors.add(wg.myID[i]);
    }
    return neighbors;
  }

  private String getMinimum(HashSet<String> unsettled) {
    String minimum = null;
    for (String s : unsettled) {
      if (minimum == null) minimum = s;
      else {
        if (getShortestDistance(s) < getShortestDistance(minimum)) {
          minimum = s;
        }
      }
    }
    return minimum;
  }

  private int getShortestDistance(String destination) {
    Integer d = distance.get(destination);
    if (d == null) return Integer.MAX_VALUE;
    else return d;
  }

  private String getPath(String destination) {
    String current = destination;
    String result = "";
    if (predecessors.get(destination) == null) return "No path found!";
    result = destination + result;
    while (predecessors.get(current) != null) {
      String old = current;
      current = predecessors.get(current);
      result = current + " ->(" + wg.edges[wg.find(current)][wg.find(old)] + ") " + result;
    }
    return result;
  }

  //initialize the linkstate database by adding an entry about the router itself
  private LSA initLinkStateDatabase() {
    LSA lsa = new LSA();
    lsa.linkStateID = rd.simulatedIPAddress;
    lsa.lsaSeqNumber = Integer.MIN_VALUE;
    LinkDescription ld = new LinkDescription();
    ld.linkID = rd.simulatedIPAddress;
    ld.portNum = -1;
    ld.tosMetrics = 0;
    lsa.links.add(ld);
    return lsa;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (LSA lsa: _store.values()) {
      sb.append(lsa.linkStateID).append("(" + lsa.lsaSeqNumber + ")").append(":\t");
      for (LinkDescription ld : lsa.links) {
        sb.append(ld.linkID).append(",").append(ld.portNum).append(",").
                append(ld.tosMetrics).append("\t");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

}
