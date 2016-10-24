package com.uja.tetris.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public class Block extends GameObject {

    private Vector2 position;
    private Color colour;
    private ShapeRenderer shapeRenderer;

    public Block(int x, int y, Color colour) {
        setPosition(x, y);
        this.colour = colour;
        shapeRenderer = new ShapeRenderer();
    }

    public Block ghostClone() {
        return new Block((int) position.x, (int) position.y, new Color(colour.r, colour.g, colour.b, 0.3F));
    }

    public void move(String direction) {
        if (direction.equals("left")) position.add(-1, 0);
        else if (direction.equals("right")) position.add(1, 0);
        else if (direction.equals("down")) position.add(0, 1);
    }

    public void setPosition(int x, int y) {
        position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float x() {
        return position.x;
    }

    public float y() {
        return position.y;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {
        // Don't print top blocks
        if (position.y < GameBox.NUM_LINES_NOT_PRINTED) return;

        sb.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colour);
        shapeRenderer.rect(position.x * GameBox.blockSize, (position.y - GameBox.NUM_LINES_NOT_PRINTED) * GameBox.blockSize, GameBox.blockSize, GameBox.blockSize);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
        sb.begin();
    }

    // TODO: Pop on dispose?
    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

}
