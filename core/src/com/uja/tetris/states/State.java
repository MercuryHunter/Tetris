package com.uja.tetris.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public abstract class State {

    protected StateManager stateManager;
    protected Vector3 mouse;
    protected OrthographicCamera cam;

    protected State(StateManager stateManager) {
        this.stateManager = stateManager;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    protected abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
