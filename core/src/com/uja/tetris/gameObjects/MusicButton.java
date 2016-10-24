package com.uja.tetris.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.utilities.MusicManager;

/**
 * Created by jonathanalp on 2016/08/08.
 */
public class MusicButton extends GameObject {

    private MusicManager musicManager;
    private Button musicButton;

    // Default Position
    public MusicButton(MusicManager musicManager) {
        this(musicManager, 350, 15);
    }

    public MusicButton(MusicManager musicManager, float x, float y) {
        this.musicManager = musicManager;
        musicButton = new Button("", "segoe.fnt", "sound_on.png", x, y, 40, 40);
        if (musicManager.muted()) musicButton.changeImage("sound_off.png");
    }

    public void handleInput() {
        if (musicButton.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            musicButton.declick();
            if (musicManager.muted())
                musicButton.changeImage("sound_on.png");
            else
                musicButton.changeImage("sound_off.png");
            musicManager.muteButton();
        }
    }

    public void update(float dt) {
        musicButton.update(dt);
    }

    public void render(SpriteBatch sb) {
        musicButton.render(sb);
    }

    public void dispose() {
        musicButton.dispose();
    }

}
