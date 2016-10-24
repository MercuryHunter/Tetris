package com.uja.tetris.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.MainGame;
import com.uja.tetris.gameObjects.MusicButton;
import com.uja.tetris.utilities.MusicManager;

/**
 * Created by jonathanalp on 2016/08/05.
 */
public class PauseState extends State {

    private Texture background;
    private MusicButton musicButton;

    protected PauseState(StateManager stateManager, MusicManager musicManager) {
        super(stateManager);
        musicButton = new MusicButton(musicManager, 250, 200);
        cam.setToOrtho(false, MainGame.WIDTH, MainGame.HEIGHT);
        background = new Texture("pauseScreen.png");
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyPressed(Input.Keys.M))
            stateManager.pop();

        musicButton.handleInput();
    }

    @Override
    public void update(float dt) {
        musicButton.update(dt);
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        musicButton.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        musicButton.dispose();
    }
}
