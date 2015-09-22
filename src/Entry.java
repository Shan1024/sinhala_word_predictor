/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Shan
 */
public class Entry implements Comparable<Entry> {

    private String word;
    private double value;

    public Entry(String word, double value) {
        this.word = word;
        this.value = value;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(Entry o) {
        if (getValue() > o.getValue()) {
            return -1;
        } else if (getValue() < o.getValue()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "[" + word + ": " + value + "]";
    }

}
