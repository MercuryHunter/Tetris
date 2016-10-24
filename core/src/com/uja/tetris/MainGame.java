package com.uja.tetris;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.states.MainMenuState;
import com.uja.tetris.states.StateManager;
import com.uja.tetris.utilities.HighscoreManager;
import com.uja.tetris.utilities.MusicManager;

public class MainGame extends ApplicationAdapter {
    public static final int HEIGHT = 440, WIDTH = 400; //480x200 (24 x 10) with 20px blocks
    public static final String TITLE = "Tetris";

    SpriteBatch batch;
    private StateManager stateManager;
    private MusicManager musicManager;
    private HighscoreManager highscoreManager;

    @Override
    public void create() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        batch = new SpriteBatch();
        musicManager = new MusicManager("theme_a.mp3");
        musicManager.play();
        highscoreManager = new HighscoreManager("highscores.txt");
        stateManager = new StateManager();
        stateManager.push(new MainMenuState(stateManager, musicManager, highscoreManager));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateManager.update(Gdx.graphics.getDeltaTime());
        stateManager.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        musicManager.dispose();
    }
}
