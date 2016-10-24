package com.uja.tetris.utilities;

/**
 * Created by jonathanalp on 2016/08/07.
 */
public class Highscore implements Comparable<Highscore> {

    private String name;
    private int score;

    public Highscore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public Highscore(String fileLine) {
        String[] split = fileLine.split("#");
        name = split[0];
        score = Integer.parseInt(split[1]);
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return name + "#" + score;
    }

    public String niceString() {
        return score + " - " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Highscore)) return false;
        Highscore other = (Highscore) o;
        return other.score == this.score;
    }

    @Override
    public int compareTo(Highscore o) {
        return o.score - this.score;
    }
}
