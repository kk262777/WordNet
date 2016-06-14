import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            int id = Integer.valueOf(split[0]);
            String[] synset = split[1].split(" ");
            String definition = split[2];
            SynsetVertex t = new SynsetVertex(id, synset, definition);
            return Arrays.stream(synset).map(x -> new SimpleEntry<String, SynsetVertex>(x, t));
        }
    }

    /**
     * Noun to Vertex mapping {noun -> synsetVertex of this noun}
     */
    private Map<String, SynsetVertex> nounMap;

    /**
     * Adjacent list mapping {v.id -> adjList of v}
     */
    private final ArrayList<Integer>[] adj;

    /**
     * {id -> vertex of this id}
     */
    private final SynsetVertex[] idMap;

    /**
     * Number of vertices in this WordNet
     */
    private int V;


    public WordNet(String synsets, String hypernyms) {
        InputStream inputStream1 = getClass().getResourceAsStream("synsets3.txt");
        InputStream inputStream2 = getClass().getResourceAsStream("hypernyms15Path.txt");
        BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream1));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(inputStream2));
        nounMap = br1.lines().flatMap(SynsetVertex::toPairX).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
        for (Map.Entry<String, SynsetVertex> x : nounMap.entrySet()) {
            System.out.println(x.getKey() + " " + x.getValue().definition);
        }

        V = nounMap.size();
        idMap = new SynsetVertex[V];
        for (SynsetVertex each : nounMap.values()) {
            idMap[each.getId()] = each;
        }
        adj = (ArrayList<Integer>[]) new ArrayList[V];

        //a dag one root ?
        String line = null;
        String[] lineSplit = null;
        try {
            while ((line = br2.readLine()) != null) {
                lineSplit = line.split(",");
                int curV = Integer.valueOf(lineSplit[0]);
                if (curV > V) {
                    throw new java.lang.IllegalArgumentException();
                }
                List<Integer> curAdj = adj[curV];
                if (curAdj == null) {
                    curAdj = new ArrayList<>();
                }
                for (int i = 1; i < lineSplit.length; i++) {
                    curAdj.add(Integer.valueOf(lineSplit[i]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //let's check

    }
//
//    // returns all WordNet nouns
//    public Iterable<String> nouns() {
//
//    }
//
//    // is the word a WordNet noun?
//    public boolean isNoun(String word) {
//
//    }
//
//    // distance between nounA and nounB (defined below)
//    public int distance(String nounA, String nounB) {
//
//    }
//
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
