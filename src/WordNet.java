import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xgy on 13/06/16.
 */
public class WordNet {
    // constructor takes the name of the two input files

    /**
     * Nested class for vertices in WordNet
     */
    private static class SynsetVertex {
        String[] synset; // synset is a set of nouns {noun1, noun2, noun3}, they are synonym.
        String definition;
        int id; // id of this vertex

        private SynsetVertex(int id, String[] synsetGroup, String definition) {
            this.id = id;
            this.synset = synsetGroup;
            this.definition = definition;
        }

        public String[] getSynset() {
            return synset;
        }

        public int getId() {
            return id;
        }

        public static Stream<SimpleEntry<String, SynsetVertex>> toPairX(String line) {
            String[] split = line.split(",");
            String[] nouns = split[1].split(" ");
            SynsetVertex vertex = new SynsetVertex(Integer.valueOf(split[0]), nouns, split[2]);
            return Arrays.stream(nouns).map(noun -> new SimpleEntry<String, SynsetVertex>(noun, vertex));
        }
    }

    /**
     * Noun to Vertex mapping {noun -> synsetVertex of this noun}
     */
//    private Map<String, SynsetVertex> nounMap;
    private Map<String, List<Integer>> nounMap;

    /**
     * Adjacent list mapping {v.id -> adjList of v}
     */
//    private final List<Integer>[] adj;

    /**
     * Indegree of vertex {indegree: v.id -> numOfPrevVertices of v}
     */
//    private final int[] indegree;

    /**
     * {id -> vertex of this id}
     */
    private final SynsetVertex[] idMap;

    private Digraph digraph;

    /**
     * Number of vertices in this WordNet
     */
    private int V;

    /* Ancestor of all vertices */
    private int root = -1;


    public WordNet(String synsets, String hypernyms) throws RuntimeException {
        BufferedReader br1 = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("synsets15.txt")));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("hypernyms15Tree.txt")));
        nounMap = new HashMap<>();
        /* read synset */

        List<SynsetVertex> tmpList = br1.lines()
                .map(line -> {
                    String[] split = line.split(",");
                    int id = Integer.parseInt(split[0]);
                    String[] nouns = split[1].split(" ");
                    Arrays.stream(nouns).forEach(noun ->
                            nounMap.computeIfAbsent(noun, k -> new ArrayList<Integer>()).add(id)
                    );
                    return new SynsetVertex(id, nouns, split[2]);
                })
                .collect(Collectors.toList());

        digraph = new Digraph(tmpList.size());
        idMap = new SynsetVertex[tmpList.size()];
        for (SynsetVertex vertex : tmpList) {
            idMap[vertex.getId()] = vertex;
        }

        br2.lines().forEach(line -> {
            String[] lineSplit = line.split(",");
            int v = Integer.parseInt(lineSplit[0]);
            for (int i = 1; i < lineSplit.length; i++) {
                digraph.addEdge(v, Integer.parseInt(lineSplit[i]));
            }
        });





//        String line = null;
//        String[] lineSplit = null;
//        try {
//            while ((line = br1.readLine()) != null) {
//                String[] split = line.split(",");
//                String[] nouns = split[1].split(" ");
//                int id = Integer.parseInt(split[0]);
//                SynsetVertex vertex = new SynsetVertex(id, nouns, split[2]);
//                Arrays.stream(nouns).forEach(noun -> {
//                    nounMap.computeIfAbsent(noun, k -> new ArrayList<Integer>()).add(id);
//                    // :)
////                    List<Integer> l = nounMap.putIfAbsent(noun, Arrays.asList(id));
////                    if (l != null) {
////                        l.add(id);
////                    }
//                });
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        nounMap = br1.lines()
//                .flatMap(SynsetVertex::toPairX)
//                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

        /* output checking */
        for (Map.Entry<String, List<Integer>> x : nounMap.entrySet()) {
            System.out.println(x.getKey() + " " + x.getValue());
        }
//
//        V = nounMap.values().size();
//        idMap = new SynsetVertex[V];
//        for (SynsetVertex each : nounMap.values()) {
//            idMap[each.getId()] = each; /* idMap: id -> synsetVertex }*/
//        }
//        indegree = new int[V];
//        adj = (List<Integer>[]) new ArrayList[V];
//        for (int i = 0; i < V; i++) {
//            adj[i] = new ArrayList<Integer>();
//        }
//
//        String line = null;
//        String[] lineSplit = null;
//        try {
//            while ((line = br2.readLine()) != null) {
//                lineSplit = line.split(",");
//                int v = Integer.valueOf(lineSplit[0]);
//                if (v > V) {
//                    throw new java.lang.IllegalArgumentException();
//                }
//                List<Integer> curAdj = adj[v];
//                for (int i = 1; i < lineSplit.length; i++) {
//                    int w = Integer.valueOf(lineSplit[i]);
//                    curAdj.add(w);
//                    indegree[w] = v;
//                }
//                adj[v] = curAdj;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        for (int i = 0; i < digraph.V(); i++) {
            if ( digraph.adj(i) == null) continue;
            for (int e : digraph.adj(i)) {
                System.out.print(i + "->" + e + " ");
            }
            System.out.println();
        }

        if (!checkValid()) {
            throw new IllegalArgumentException();
        }


    }

    /**
     * Walk through all vertex
     * For a rooted DAG:
     * a) End of all path started from any vertex is the same
     * b) No cycle
     */
    private boolean checkValid() {
        boolean marked[] = new boolean[digraph.V()];
        boolean[] visited = new boolean[digraph.V()];
        boolean isCycle;
        for (int v = 0; v < digraph.V(); v++) {
            if (!marked[v]) {
                isCycle = cycleDFS(v, marked, visited);
                if (isCycle) return false;
            }
        }
        return true;
    }

    private boolean cycleDFS(int v, boolean[] marked, boolean[] visited) {
        visited[v] = true;
        marked[v] = true;
        Iterable<Integer> curAdj = digraph.adj(v);
        Iterator<Integer> itr = curAdj.iterator();

        if (!itr.hasNext() && root == -1) {
            root = v;
        } else if (!itr.hasNext() && v != root) {
            return false;
        }

        while (itr.hasNext()) {
            int w = itr.next();
            if (!marked[w]) {
                if (cycleDFS(w, marked, visited)) {
                    return true;
                }
            } else if (visited[w]) {
                return true;
            }
        }
        visited[v] = false;
        return false;
    }


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nounMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
//    public int distance(String nounA, String nounB) throws IllegalArgumentException {
////        SynsetVertex a = nounMap.get(nounA);
////        SynsetVertex b = nounMap.get(nounB);
//        if (nounA == null || nounB == null) throw new IllegalArgumentException();
//
//    }

//    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
//    // in a shortest ancestral path (defined below)
//    public String sap(String nounA, String nounB) {
//
//    }
//
//    // do unit testing of this class
//    public static void main(String[] args) {
//
//    }
}
