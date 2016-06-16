import edu.princeton.cs.algs4.Digraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by xgy on 15/06/16.
 */
public class SAP {
    private Digraph digraph;
    /**
     * {cache: (v,w) -> (length, ancestor)}
     */
    private Map<String, String> cache;

    private static class Walker {
        private int location;
        private char represent;
        private int length;

        public Walker(char represent, int v, int length) {
            this.represent = represent;
            this.location = v;
            this.length = length;
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G);
        this.cache = new HashMap<>();
    }

    /**
     * {v1,v2,v3} {w1,w2,w3}
     * put all vertices to queues and let them update shortest path
     * @param queue
     * @param v
     * @param w
     * @return
     */
    private int[] sapFinder(Queue<Walker> queue, int v, int w) {
//        String key = null;
//        if (v != -1) {
//            if (v <= w) {
//                key = "" + v + "," + w;
//            } else {
//                key = "" + w + "," + v;
//            }
//            String result = cache.get(key);
//            if (result != null) {
//                String[] split = result.split(",");
//                return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
//            }
//        }
        int[] res = new int[2];
        int[] path = new int[digraph.V()];
        res[0] = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Walker cur = queue.poll();
            if (cur.length > res[0]) {
                break;
            }
            if (path[cur.location] != 0) {
                if ((cur.represent == 'v' && path[cur.location] < 0) || (cur.represent == 'w' && path[cur.location] > 0)) {
                    int curLength = Math.abs(cur.length) + Math.abs(path[cur.location]) - 2;
                    if (res[0] >= curLength) {
//                        System.out.println(cur.location);
                        res[0] = curLength;
                        res[1] = cur.location;
                    }
                    if (cur.location == v || cur.location == w) continue;
                    for (int next : digraph.adj(cur.location)) {
                        if (cur.represent == 'v') queue.offer(new Walker(cur.represent, next, cur.length + 1));
                        else queue.offer(new Walker(cur.represent, next, cur.length - 1));
                    }
                }
                //else found walked path, stop
            } else {
                //Corner case : smaller step
                path[cur.location] = cur.length;
                for (int next : digraph.adj(cur.location)) {
                    if (cur.represent == 'v') queue.offer(new Walker(cur.represent, next, cur.length + 1));
                    else queue.offer(new Walker(cur.represent, next, cur.length - 1));
                }
            }
        }

        if (v != -1) {
            if (res[0] == Integer.MAX_VALUE) {
//                cache.put(key, "-1,-1");
                return new int[]{-1, -1};
            }
//            cache.put(key, "" + res[0] + "," + res[1]);
            return res;
        } else {
            if (res[0] == Integer.MAX_VALUE) return new int[]{-1, -1};
            else return res;
        }
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        Queue<Walker> queue = new LinkedList<>();
        queue.offer(new Walker('v', v, 1));
        queue.offer(new Walker('w', w, -1));
        int[] result = sapFinder(queue, v, w);
        return result[0];

    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Queue<Walker> queue = new LinkedList<>();
        queue.offer(new Walker('v', v, 1));
        queue.offer(new Walker('w', w, -1));

        int[] result = sapFinder(queue, v, w);
        return result[1];

    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        Queue<Walker> queue = new LinkedList<>();
        for (int vW : v) {
            queue.offer(new Walker('v', vW, 1));
        }
        for (int wW : w) {
            queue.offer((new Walker('w', wW, -1)));
        }

        int[] result = sapFinder(queue, -1, -1);
        return result[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        Queue<Walker> queue = new LinkedList<>();
        for (int vW : v) {
            queue.offer(new Walker('v', vW, 1));
        }
        for (int wW : w) {
            queue.offer(new Walker('w', wW, -1));
        }

        int[] result = sapFinder(queue, -1, -1);
        return result[1];


    }

}
