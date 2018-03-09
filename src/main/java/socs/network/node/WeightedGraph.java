package socs.network.node;

import java.util.Iterator;
import java.util.Map;

import socs.network.message.*;

public class WeightedGraph {
    int n;
    short[][] edges;
    String[] myID;
    public WeightedGraph(LinkStateDatabase lsd) {
        n = lsd._store.size();
        edges = new short[n][n];
        myID = new String[n];
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
            it.remove();
        }

        for (int i = 0; i < myID.length; i++) {
            String key = myID[i];
            LSA lsa = lsd._store.get(key);
            for (LinkDescription ld : lsa.links) {
                int j = find(ld.linkID);
                if (j == -1) continue;
                else edges[i][j] = ld.tosMetrics;
            }
        }
    }

    private int find(String IP) {
        for (int i = 0; i < myID.length; i++) {
            if (myID[i].equals(IP)) return i;
        }
        return -1;
    }
}
