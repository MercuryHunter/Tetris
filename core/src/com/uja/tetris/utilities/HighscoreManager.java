package com.uja.tetris.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jonathanalp on 2016/08/07.
 */
public class HighscoreManager {

    String highscoreFile;
    private ArrayList<Highscore> highscores;

    public HighscoreManager(String highscoreFile) {
        this.highscoreFile = highscoreFile;
        readInHighscores();
    }

    public void readInHighscores() {
        highscores = new ArrayList<Highscore>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(highscoreFile));
            String line;
            while ((line = br.readLine()) != null)
                highscores.add(new Highscore(line));
            br.close();
        } catch (Exception e) {
            System.err.println("ERROR READING HIGHSCORE FILE");
            e.printStackTrace();
        }
    }

    public void addHighscore(String name, int score) {
        addHighscore(new Highscore(name, score));
    }

    public void addHighscore(Highscore highscore) {
        int position = Arrays.binarySearch(highscores.toArray(new Highscore[highscores.size()]), highscore);
        if (position < 0) {
            position += 1;
            position -= 2 * position;
        }
        highscores.add(position, highscore);
    }

    // Should be called on program exit.
    public void writeHighscoresToFile() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(highscoreFile, false));
            for (Highscore highscore : highscores)
                pw.println(highscore);
            pw.close();
        } catch (Exception e) {
            System.err.println("ERROR WRITING TO HIGHSCORE FILE");
            e.printStackTrace();
        }
    }

    public String getTop10() {
        StringBuilder output = new StringBuilder();
        output.append("Highscores:\n");
        for (int i = 0; i < Math.min(highscores.size(), 10); i++)
            output.append(i + 1 + ") " + highscores.get(i).niceString() + "\n");
        return output.toString();
    }
}
