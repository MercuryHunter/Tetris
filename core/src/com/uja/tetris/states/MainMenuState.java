package com.uja.tetris.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.MainGame;
import com.uja.tetris.gameObjects.Button;
import com.uja.tetris.gameObjects.MusicButton;
import com.uja.tetris.utilities.HighscoreManager;
import com.uja.tetris.utilities.MusicManager;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public class MainMenuState extends State {

    private Texture background;
    private Button startButton, startDualButton, controlsButton, highscoresButton, exitButton;
    private MusicButton musicButton;
    private MusicManager musicManager;
    private HighscoreManager highscoreManager;

    public MainMenuState(StateManager stateManager, MusicManager musicManager, HighscoreManager highscoreManager) {
        super(stateManager);
        this.musicManager = musicManager;
        this.highscoreManager = highscoreManager;
        cam.setToOrtho(false, MainGame.WIDTH, MainGame.HEIGHT);
        background = new Texture("background.png");
        startButton = new Button("Start", "segoe.fnt", 110, 230, 180, 50);
        startDualButton = new Button("Two-Player", "segoe.fnt", 110, 175, 180, 50);
        controlsButton = new Button("Controls", "segoe.fnt", 110, 120, 180, 50);
        highscoresButton = new Button("Highscores", "segoe.fnt", 110, 65, 180, 50);
        exitButton = new Button("Exit", "segoe.fnt", 110, 10, 180, 50);
        musicButton = new MusicButton(musicManager);
    }

    @Override
    public void handleInput() {
        if (startButton.isClicked())
            stateManager.set(new GameState(stateManager, musicManager, highscoreManager, false));

        if (startDualButton.isClicked())
            stateManager.set(new GameState(stateManager, musicManager, highscoreManager, true));

        if (exitButton.isClicked()) {
            highscoreManager.writeHighscoresToFile();
            Gdx.app.exit();
        }

        if (highscoresButton.isClicked()) {
            highscoresButton.declick();
            stateManager.push(new DialogState(stateManager, 2, musicManager, highscoreManager.getTop10()));
        }

        if (controlsButton.isClicked()) {
            controlsButton.declick();
            String controls =
                    "Single Player\n" +
                            "Left + Right Arrow Keys\n" +
                            "Down Arrow Key - Speedup\n" +
                            "Up Arrow Key - Rotate (Left)\n" +
                            "Z and X - rotate Left and Right\n" +
                            "Left-Ctrl - Hold\nSpace - Instant Drop\n\n" +
                            "Two Player\n" +
                            "WASD and Left-Ctrl (P1)\n" +
                            "Arrow Keys and Shift (P2)\n\n" +
                            "M - Mute\n" +
                            "Esc - Pause\n" +
                            "Any key to leave pause menu";
            stateManager.push(new DialogState(stateManager, 2, musicManager, controls));
        }

        musicButton.handleInput();
    }

    @Override
    public void update(float dt) {
        startButton.update(dt);
        startDualButton.update(dt);
        controlsButton.update(dt);
        highscoresButton.update(dt);
        exitButton.update(dt);
        musicButton.update(dt);
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        startButton.render(sb);
        startDualButton.render(sb);
        controlsButton.render(sb);
        highscoresButton.render(sb);
        exitButton.render(sb);
        musicButton.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        startButton.dispose();
        startDualButton.dispose();
        controlsButton.dispose();
        highscoresButton.dispose();
        exitButton.dispose();
        musicButton.dispose();
    }
}
