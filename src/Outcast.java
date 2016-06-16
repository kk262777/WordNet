/**
 * Created by xgy on 15/06/16.
 */
public class Outcast {
    private WordNet net;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.net = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] sums = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            int sum = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (j != i) {
                    sum += net.distance(nouns[i], nouns[j]);
                }
            }
            sums[i] = sum;
        }
        int maxSum = Integer.MIN_VALUE;
        int index = 11;
        for (int i = 0; i < sums.length; i++) {
            if (maxSum < sums[i]) {
                maxSum = sums[i];
                index = i;
            }
        }
        return nouns[index];
    }

    // see test client below
    public static void main(String[] args) {

    }
}
