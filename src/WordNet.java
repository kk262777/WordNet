import edu.princeton.cs.algs4.Digraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xgy on 13/06/16.
 */
public class WordNet {

    /**
     * Nested class for vertices in WordNet
     */
    private static class SynsetVertex {
        private String synset; // synset is a set of nouns {noun1, noun2, noun3}, they are synonym.
        private String definition;
        private int id; // id of this vertex

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

    }

    /**
     * Noun to Vertex mapping {noun -> synsetVertex of this noun}
     */
    private Map<String, List<Integer>> nounMap;

    /**
     * {id -> vertex of this id}
     */
    private SynsetVertex[] idMap;

    /**
     * Directed graph network
     */
    private Digraph digraph;

    /* Ancestor of all vertices */
    private int root = -1;

    /* Helper class to calculate the shortest ancestral path*/
    private SAP sap = null;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        try {
            File f1 = new File(synsets);
            File f2 = new File(hypernyms);

            FileReader fr1 = new FileReader(f1);
            FileReader fr2 = new FileReader(f2);
            BufferedReader br1 = new BufferedReader(fr1);
            BufferedReader br2 = new BufferedReader(fr2);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* check if cycle or not a rooted DAG */
        try {
            if (!checkValid()) {
                throw new IllegalArgumentException();
            }
        } catch (NullPointerException e) {
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
        boolean[] marked = new boolean[digraph.V()];
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

    /**
     * 1. check if all path ended at the same root
     * 2. check cycle
     * @param v
     * @param marked
     * @param visited
     * @return
     */
    private boolean cycleDFS(int v, boolean[] marked, boolean[] visited) {
        visited[v] = true;
        marked[v] = true;
        Iterable<Integer> curAdj = digraph.adj(v);
        Iterator<Integer> itr = curAdj.iterator();

        if (!itr.hasNext() && root == -1) {
            root = v;
        } else if (!itr.hasNext() && v != root) {
            return true;
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
        if (word == null) throw new NullPointerException();
        return nounMap.containsKey(word);
    }

    private String getSynsetById(int v) {
        return idMap[v].synset;
    }

    //     distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException();
        Iterable<Integer> a = nounMap.get(nounA);
        Iterable<Integer> b = nounMap.get(nounB);
        if (a == null || b == null) throw new IllegalArgumentException();
        if (sap == null) sap = new SAP(digraph);
        return sap.length(a, b);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException();
        Iterable<Integer> a = nounMap.get(nounA);
        Iterable<Integer> b = nounMap.get(nounB);
        if (a == null || b == null) throw new IllegalArgumentException();
        if (sap == null) sap = new SAP(digraph);
        return idMap[sap.ancestor(a, b)].synset;
    }

    // do unit testing of this class
    public static void main(String[] args) {
//        In in = new In("/home/xgy/IdeaProjects/WordNet/wordnet/digraph-wordnet.txt");
//        Digraph G = new Digraph(in);
//        SAP sap = new SAP(G);
////        int v = 65600;
//        List<Integer> v = Arrays.asList(81552);
////        int w = 12100;
//        List<Integer> w = Arrays.asList(22741, 51515);
////        int length = sap.length(v, w);
//        int length = sap.length(v, w);
//        int ancestor = sap.ancestor(v, w);
//        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        WordNet wordNet = new WordNet("/home/xgy/IdeaProjects/WordNet/wordnet/synsets15.txt", "/home/xgy/IdeaProjects/WordNet/wordnet/hypernyms15Tree.txt");
//        System.out.println(wordNet.sap("invalid", "bird"));
//        System.out.println(wordNet.distance("worm", "bird"));
//        System.out.println(wordNet.idMap[wordNet.root].synset);
//        Outcast out = new Outcast(wordNet);
//        System.out.println(out.outcast(new String[]{"horse", "zebra", "cat", "bear", "table"}));

    }
}
