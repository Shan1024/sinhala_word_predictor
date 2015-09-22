import java.io.*;
import java.util.*;

public class AutoComplete {

    static LinkedList<String> sentences = new LinkedList<String>();

    public static void main(String[] args) throws FileNotFoundException {
        /* Define Variables */
        int n = 3;
        Hashtable<String, Hashtable<String, Double>> nGram = new Hashtable<String, Hashtable<String, Double>>();
        Scanner inFile = new Scanner(new File("searchterms.txt"));
        Scanner input = new Scanner(System.in);

        /* Output for progress tracking */
        System.out.println("Reading in search data...");

        /* Populate the LinkedList sentences with data from AOL search dataset */
        while (inFile.hasNext()) {
            String unparsed = inFile.nextLine().intern();
            String[] parsed = unparsed.split("\t");
            sentences.add("<S> " + parsed[1] + " </S>");
        }
        inFile.close();

        /* Output for progress tracking */
        System.out.println("Successfully archived searches.");
        System.out.println("Creating 3 grams...");

        /* Split sentences into words */
        for (String s : sentences) {
            String[] words = s.split("[\\s]");
            for (int i = 0; i <= words.length - n; i++) {
                if (nGram.containsKey(words[i] + " " + words[i + 1])) {
                    //Output for testing
                    //System.out.println("MATCH FOUND! ("+ words[i+2]+") Incrementing...");
                    if (nGram.get(words[i] + " " + words[i + 1]).containsKey(words[i + 2])) {
                        double v = nGram.get(words[i] + " " + words[i + 1]).get(words[i + 2]);
                        v++;
                        nGram.get(words[i] + " " + words[i + 1]).put(words[i + 2], v);
                    } else {
                        nGram.get(words[i] + " " + words[i + 1]).put(words[i + 2], 1.0);
                    }
                } else {
                    //Output for testing
                    //System.out.println("No match found. Adding..." + words[i+2]);
                    nGram.put(words[i] + " " + words[i + 1], createResult(words[i + 2]));
                }
            }
        }

        /* Output for progress tracking */
        System.out.println("Successfully created 3 grams.");

        /* Loop so you can play with this forever */
        String sTerm = "";
        while (true) {
            /* Request User Input */
            System.out.println("Please enter your search terms (or type /q to quit):");
            sTerm = input.nextLine();
            if (sTerm.equalsIgnoreCase("/q")) {
                break;
            }
            /* Format user input */
            String[] terms = sTerm.split("[\\s]");
            if (terms.length < 2) {
                sTerm = "<S> " + terms[0];
                //Output for testing
                //System.out.println(sTerm);
            } else {
                sTerm = terms[terms.length - 2] + " " + terms[terms.length - 1];
                //Output for testing
                //System.out.println(sTerm);
            }

            /* Normalize to percent values */
            double sum = 0;
            try {
                for (String s : nGram.get(sTerm).keySet()) {
                    sum += nGram.get(sTerm).get(s);
                }
                for (String s : nGram.get(sTerm).keySet()) {
                    nGram.get(sTerm).put(s, nGram.get(sTerm).get(s) / sum);
                }
            } catch (Exception NullPointerException) {
                System.out.println("Search query not found in database.");
            }
            /* Give prediction */
            try {
                System.out.println("Prediction: " + prediction(nGram.get(sTerm)) + " (" + Math.round(predValue(nGram.get(sTerm)) * 100) + "%)");
            } catch (Exception NullPointerException) {
                System.out.println("Cannot make a prediction.");
            }

            /* Testing block */
            //System.out.println(nGram.get(sTerm).keySet());
            //System.out.println(nGram.get(sTerm).values());
        }
        input.close();
    }

    /* Needed for scope */
    static final Hashtable<String, Double> createResult(String s) {
        Hashtable<String, Double> result = new Hashtable<String, Double>();
        result.put(s, 1.0);
        return result;
    }

    /* Prediction methods */
    static final String prediction(Hashtable<String, Double> h) {
        String key = "";
        double max = 0;
        for (String s : h.keySet()) {
            if (h.get(s) > max) {
                max = h.get(s);
                key = s;
            }
        }
        return key;
    }

    static final double predValue(Hashtable<String, Double> h) {
        double max = 0;
        for (String s : h.keySet()) {
            if (h.get(s) > max) {
                max = h.get(s);
            }
        }
        return max;
    }
}
