package com.uja.tetris.gameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by jonathanalp on 2016/08/04.
 */
public abstract class GameObject {

    protected GameObject() {
    }

    public abstract void update(float dt);

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
