package socs.network.node;

import java.util.Iterator;
import java.util.Map;

import socs.network.message.*;

public class WeightedGraph {
    int n;
    short[][] edges;
    String[] myID;
    LSA[] myLSA;

    public WeightedGraph(LinkStateDatabase lsd) {
        n = lsd._store.size();
        edges = new short[n][n];
        myID = new String[n];
        myLSA = new LSA[n];
        clear();
        init(lsd);
    }

    /**
     * Initialize the graph using lsd
     * @param lsd
     */
    private void init(LinkStateDatabase lsd) {

        Iterator it = lsd._store.entrySet().iterator();
        for (int i = 0;it.hasNext(); i++) {
            Map.Entry pair = (Map.Entry)it.next();
            myID[i] = (String) pair.getKey();
            myLSA[i] = (LSA) pair.getValue();
        }

        for (int i = 0; i < myID.length; i++) {
            for (LinkDescription ld : myLSA[i].links) {
                int j = find(ld.linkID);
                if (j == -1) continue;
                else edges[i][j] = ld.tosMetrics;
            }
        }
    }

    /**
     * Find the index of IP in myID
     * @param IP
     * @return
     */
    public int find(String IP) {
        for (int i = 0; i < myID.length; i++) {
            if (myID[i].equals(IP)) return i;
        }
        return -1;
    }

    /**
     * clear edges and myID
     */
    private void clear() {
        for (int i = 0; i < n; i++) {
            myID[i] = "";
            for (int j = 0; j < n; j++){
                edges[i][j] = -1;
            }
        }
    }
}
