package com.uja.tetris.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.utilities.MusicManager;

import javax.swing.JOptionPane;

/**
 * Created by jonathanalp on 2016/08/09.
 */
public class DialogState extends State {

    int resultInt = -1000;
    String resultString = null;
    MusicManager musicManager;

    // TODO: Make Dialogs - they're built-in and difficult, but will stop the music issues.
    protected DialogState(StateManager stateManager, int type, MusicManager musicManager, String message) {
        super(stateManager);
        this.musicManager = musicManager;

        // Are you sure exit type
        if (type == 0) {
            resultInt = JOptionPane.showConfirmDialog(null, "Are you sure?");
        }
        // Highscore type
        // Start: (new Thread() { public void run() {
        // End:  }}).start();
        else if (type == 1) {
            resultString = JOptionPane.showInputDialog("Game.. Over..\nEnter your name for the highscore board");
        } else if (type == 2) {
            JOptionPane.showMessageDialog(null, message);
            resultInt = 1;
        }
    }

    public int getResultInt() {
        return resultInt;
    }

    public String getResultString() {
        return resultString;
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if (resultInt != -1000 || resultString != null)
            stateManager.pop();
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
