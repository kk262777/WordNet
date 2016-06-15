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
        String synset; // synset is a set of nouns {noun1, noun2, noun3}, they are synonym.
        String definition;
        int id; // id of this vertex

        private SynsetVertex(int id, String synsetGroup, String definition) {
            this.id = id;
            this.synset = synsetGroup;
            this.definition = definition;
        }

        public String getSynset() {
            return synset;
        }

        int getId() {
            return id;
        }

        public static Stream<SimpleEntry<String, SynsetVertex>> toPairX(String line) {
            String[] split = line.split(",");
            String[] nouns = split[1].split(" ");
            SynsetVertex vertex = new SynsetVertex(Integer.valueOf(split[0]), split[1], split[2]);
            return Arrays.stream(nouns).map(noun -> new SimpleEntry<String, SynsetVertex>(noun, vertex));
        }
    }

    /**
     * Noun to Vertex mapping {noun -> synsetVertex of this noun}
     */
    private Map<String, List<Integer>> nounMap;

    /**
     * {id -> vertex of this id}
     */
    private final SynsetVertex[] idMap;

    /**
     * Directed graph network
     */
    private Digraph digraph;

    /* Ancestor of all vertices */
    private int root = -1;


    public WordNet(String synsets, String hypernyms) throws RuntimeException {
        BufferedReader br1 = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(synsets)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(hypernyms)));
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
                    return new SynsetVertex(id, split[1], split[2]);
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

        /* output checking */
//        for (Map.Entry<String, List<Integer>> x : nounMap.entrySet()) {
//            System.out.println(x.getKey() + " " + x.getValue());
//        }
//        for (int i = 0; i < digraph.V(); i++) {
//            if (digraph.adj(i) == null) continue;
//            for (int e : digraph.adj(i)) {
//                System.out.print(i + "->" + e + " ");
//            }
//            System.out.println();
//        }

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

    //     distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) throws IllegalArgumentException {
        Iterable<Integer> a = nounMap.get(nounA);
        Iterable<Integer> b = nounMap.get(nounB);
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        SAP sap = new SAP(digraph);
        return sap.length(a, b);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Iterable<Integer> a = nounMap.get(nounA);
        Iterable<Integer> b = nounMap.get(nounB);
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        SAP sap = new SAP(digraph);
        return idMap[sap.ancestor(a, b)].synset;
    }

    // do unit testing of this class
    public static void main(String[] args) {
//        In in = new In("/home/xgy/IdeaProjects/WordNet/src/digraph1.txt");
//        Digraph G = new Digraph(in);
//        SAP sap = new SAP(G);
////        int v = 1;
//        List<Integer> v = Arrays.asList(7, 3);
////        int w = 6;
//        List<Integer> w = Arrays.asList(10, 2);
////        int length = sap.length(v, w);
//        int length = sap.length(v, w);
//        int ancestor = sap.ancestor(v, w);
//        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println(wordNet.sap("worm", "bird"));
        System.out.println(wordNet.distance("worm", "bird"));
        System.out.println(wordNet.idMap[wordNet.root].synset);

    }
}
