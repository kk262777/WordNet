import edu.princeton.cs.algs4.Digraph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by xgy on 15/06/16.
 */
public class SAP {
    Digraph digraph;

    private static class Walker {
        int location;
        char represent;
        int length;

        public Walker(char represent, int v, int length) {
            this.represent = represent;
            this.location = v;
            this.length = length;
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] path = new int[digraph.V()];

        Queue<Walker> queue = new LinkedList<>();
        queue.offer(new Walker('v', v, 0));
        queue.offer(new Walker('w', w, 0));

        while (!queue.isEmpty()) {
            Walker cur = queue.poll();

            if (path[cur.location] != 0) {
                if (cur.represent == 'v' && path[cur.location] < 0) {
                    return Math.abs(cur.length) + Math.abs(path[cur.location]);
                } else if (cur.represent == 'w' && path[cur.location] > 0) {
                    return Math.abs(cur.length) + Math.abs(path[cur.location]);
                } else {
                    //found walked path, stop
                }
            } else{
                boolean used = false;
                path[cur.location] = cur.length;
                for (int next : digraph.adj(cur.location)) {
                    if (cur.represent == 'v') queue.offer(new Walker(cur.represent, next, cur.length + 1));
                    else queue.offer(new Walker(cur.represent, next, cur.length - 1));
                }
            }
        }
        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {

    }

//    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
//    public int length(Iterable<Integer> v, Iterable<Integer> w) {
//
//    }
//
//    // a common ancestor that participates in shortest ancestral path; -1 if no such path
//    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
//
//    }
//
//    // do unit testing of this class
//    public static void main(String[] args) {
//
//    }
}
