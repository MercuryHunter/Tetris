package com.uja.tetris.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.MainGame;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public class Button extends GameObject {
    private static final int HOVER_MOVE_DISTANCE = 2;
    private static final float BUTTON_COOLDOWN = 0.5F;

    BitmapFont font;
    GlyphLayout label;
    private float x, y, width, height;
    private boolean hover, clicked;
    private Texture image;
    private float time_passed;

    public Button(String text, String fontName, float x, float y, float width, float height) {
        this(text, fontName, "button.png", x, y, width, height);
    }

    public Button(String text, String fontName, String imageFile, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        image = new Texture(imageFile);
        font = new BitmapFont(Gdx.files.internal(fontName));
        label = new GlyphLayout(font, text);
        hover = false;
        clicked = false;
        time_passed = BUTTON_COOLDOWN;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void declick() {
        clicked = false;
    }

    public void changeImage(String imageFile) {
        image.dispose();
        image = new Texture(imageFile);
    }

    public void handleInput() {
        int xInput = Gdx.input.getX(), yInput = MainGame.HEIGHT - Gdx.input.getY();
        if (x < xInput && xInput < x + width && y < yInput && yInput < y + height) {
            hover = true;
            if (time_passed >= BUTTON_COOLDOWN && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                clicked = true;
                time_passed = 0;
            }
        } else hover = false;
    }

    @Override
    public void update(float dt) {
        time_passed += dt;
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(image, x + (hover ? HOVER_MOVE_DISTANCE : 0), y + (hover ? HOVER_MOVE_DISTANCE : 0), width, height);
        font.draw(sb, label, x + width / 2 - label.width / 2 + (hover ? HOVER_MOVE_DISTANCE : 0), y + height / 2 + label.height / 2 + (hover ? HOVER_MOVE_DISTANCE : 0));
    }

    @Override
    public void dispose() {
        font.dispose();
        image.dispose();
    }
}
