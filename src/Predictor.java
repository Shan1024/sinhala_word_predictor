import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Shan
 */
public class Predictor {

    private static int n = 3;
    private static HashMap<String, HashMap<String, Double>> nGram;
    private static Gson gson;

    public Predictor() {
        nGram = new HashMap<>();
        gson = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting().create();
    }

    public void loadPredictor() {

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("data.json"));

            Type typeOfHashMap = new TypeToken<HashMap<String, HashMap<String, Double>>>() {
            }.getType();
            //convert the json string back to object
            nGram = gson.fromJson(br, typeOfHashMap);

            String json = gson.toJson(nGram);

            System.out.println("Predictor:\n" + json);

        } catch (FileNotFoundException ex) {
            System.out.println("Ex: " + ex);
        }

    }

    public void savePredictor() {

        String json = gson.toJson(nGram);

        FileWriter writer;
        try {
            writer = new FileWriter("data.json");
            writer.write(json);
            writer.close();
        } catch (IOException ex) {
            System.out.println("Ex: " + ex);
        }

    }

    public boolean containsKey(String key) {
        return nGram.containsKey(key);
    }

    /* Needed for scope */
    public void addKey(String key, String word) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        result.put(word, 1.0);
        nGram.put(key, result);
    }

    /* Prediction methods */
    public  LinkedList<Entry>  predict(String key) {
        String temp = "";

        LinkedList<Entry> entries = new LinkedList<>();

        if (containsKey(key)) {

            HashMap<String, Double> map = nGram.get(key);

            for (String s : map.keySet()) {
                Entry entry = new Entry(s, map.get(s));
                entries.add(entry);
            }

            Collections.sort(entries);
            System.out.println(entries);

            return entries;
//            return entries.get(0).getWord();
//            return temp;
        } else {
            return null;
        }
    }

    public void addSentence(String sentence) {

        sentence = sentence.toLowerCase();

        String[] words = sentence.split("[\\s]");
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

    private static final HashMap<String, Double> createResult(String s) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        result.put(s, 1.0);
        return result;
    }
//    static final double predValue(HashMap<String, Double> h) {
//        double max = 0;
//        for (String s : h.keySet()) {
//            if (h.get(s) > max) {
//                max = h.get(s);
//            }
//        }
//        return max;
//    }

    public void showMap() {
        System.out.println("--------------------");
        String json = gson.toJson(nGram);
        System.out.println(json);
        System.out.println("++++++++++++++++++++");
    }

    public static void main(String[] args) {

        new Predictor().test();

    }

    private void test() {
//            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
//                .setPrettyPrinting().create();
//        nGram = new HashMap<>();

//        HashMap<String, Double> level2 = new HashMap<>();
//
//        level2.put("going", 1.25);
//        level2.put("shan", 2.25);
//
//        nGram.put("i am", level2);
//        nGram.put("who are", level2);
//        String json = gson.toJson(nGram);
//
//        System.out.println(json);
//
//        Type typeOfHashMap = new TypeToken<HashMap<String, HashMap<String, Double>>>() {
//        }.getType();
//        HashMap<String, HashMap<String, Double>> newMap = gson.fromJson(json, typeOfHashMap); // This type must match TypeToken
//
//        System.out.println(gson.toJson(newMap));
        //save the dictionary
        //saveDictionary();
        //load the dictionary
//        loadPredictor();
//        System.out.println(nGram.get("i am").get("shan"));
//        predict("i am");
        showMap();

        addSentence("My name is shan");
        addSentence("I am from Tangalle");

        showMap();

        addSentence("I am going to Tangalle today");

        showMap();

    }
}
