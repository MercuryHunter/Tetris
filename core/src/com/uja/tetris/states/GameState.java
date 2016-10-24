package com.uja.tetris.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.MainGame;
import com.uja.tetris.gameObjects.Button;
import com.uja.tetris.gameObjects.GameBox;
import com.uja.tetris.gameObjects.MusicButton;
import com.uja.tetris.gameObjects.Tetronimo;
import com.uja.tetris.utilities.HighscoreManager;
import com.uja.tetris.utilities.MusicManager;

import javax.swing.JOptionPane;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public class GameState extends State {
    private BitmapFont font;
    private GlyphLayout scoreLabel, levelLabel;

    private Texture background;
    private Button exitButton, pauseButton;
    private MusicButton musicButton;

    private GameBox game;
    private boolean finished, twoPlayer;

    private OrthographicCamera nextPieceCamera, heldPieceCamera, heldPieceCamera2;

    private MusicManager musicManager;
    private HighscoreManager highscoreManager;

    public GameState(StateManager stateManager, MusicManager musicManager, HighscoreManager highscoreManager, boolean twoPlayer) {
        super(stateManager);
        this.musicManager = musicManager;
        this.highscoreManager = highscoreManager;
        this.twoPlayer = twoPlayer;
        cam.setToOrtho(false, MainGame.WIDTH, MainGame.HEIGHT);

        // UI elements
        if (twoPlayer) {
            background = new Texture("two_player_game_board.png");
            font = new BitmapFont(Gdx.files.internal("segoeSmall.fnt"));
            pauseButton = new Button("Pause", "segoeSmall.fnt", 330, 120, 55, 40);
            exitButton = new Button("Exit", "segoeSmall.fnt", 330, 65, 55, 40);
            musicButton = new MusicButton(musicManager);
        } else {
            background = new Texture("game_board.png");
            font = new BitmapFont(Gdx.files.internal("segoe.fnt"));
            pauseButton = new Button("Pause", "segoeSmall.fnt", 240, 15, 50, 40);
            exitButton = new Button("Exit", "segoeSmall.fnt", 300, 15, 50, 40);
            musicButton = new MusicButton(musicManager);
        }
        scoreLabel = new GlyphLayout(font, "0 (1x)");
        levelLabel = new GlyphLayout(font, "Level: 1");

        // Game screen
        if (twoPlayer)
            game = new GameBox(15, 125, cam, this, 20, 24, 15, true);
        else
            game = new GameBox(20, 20, cam, this);
        finished = false;

        if (twoPlayer) {
            nextPieceCamera = new OrthographicCamera(80, 40);
            nextPieceCamera.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
            nextPieceCamera.position.x = 120;
            nextPieceCamera.position.y = 157.5F;
            nextPieceCamera.update();

            // To display held piece 1
            heldPieceCamera = new OrthographicCamera(80, 40);
            heldPieceCamera.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
            heldPieceCamera.position.x = 220;
            heldPieceCamera.position.y = 157.5F;
            heldPieceCamera.update();

            // To display held piece 2
            heldPieceCamera2 = new OrthographicCamera(80, 40);
            heldPieceCamera2.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
            heldPieceCamera2.position.x = 170;
            heldPieceCamera2.position.y = 157.5F;
            heldPieceCamera2.update();
        } else {
            // To display next piece (Eff cameras - how does this even place it in the right place?)
            nextPieceCamera = new OrthographicCamera(80, 40);
            nextPieceCamera.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
            nextPieceCamera.position.x = -10;//310;
            nextPieceCamera.position.y = -10;
            nextPieceCamera.update();

            // To display held piece
            heldPieceCamera = new OrthographicCamera(80, 40);
            heldPieceCamera.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
            heldPieceCamera.position.x = -10;//310;
            heldPieceCamera.position.y = -105;
            heldPieceCamera.update();
        }
    }

    private void setScoreLabel() {
        CharSequence text = game.getScore() + " (" + game.getScoreMultiplier() + "x)";
        scoreLabel.setText(font, text);
    }

    private void setLevelLabel() {
        CharSequence text = "Level: " + game.getLevel();
        levelLabel.setText(font, text);
    }

    private void endGame() {
        // Add highscore, go back to main screen.
        DialogState dialogState = new DialogState(stateManager, 1, musicManager, "");
        stateManager.push(dialogState);
        String name = dialogState.getResultString();
        highscoreManager.addHighscore(name, game.getScore());
        stateManager.set(new MainMenuState(stateManager, musicManager, highscoreManager));
    }

    public void playRotateSound(float volume) {
        musicManager.playRotateSound(volume);
    }

    public void playSetSound(float volume) {
        musicManager.playSetSound(volume);
    }

    @Override
    public void handleInput() {
        if (pauseButton.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pauseButton.declick();
            stateManager.push(new PauseState(stateManager, musicManager));
        }

        musicButton.handleInput();

        if (exitButton.isClicked()) {
            exitButton.declick();
            DialogState dialogState = new DialogState(stateManager, 0, musicManager, "");
            stateManager.push(dialogState);
            int result = dialogState.getResultInt();
            if (result == JOptionPane.YES_OPTION) {
                finished = true;
                endGame();
                return;
            }

            //stateManager.push(new DialogState(stateManager));
            /*
            Stage stage = new Stage();
            Gdx.input.setInputProcessor(stage);
            new Dialog("Exit", new Skin()) {
                protected void result (Object object) {
                    System.out.println("Chosen: " + object);
                }
            }.text("Are you you sure you want to quit?").button("Yes", true).button("No", false).key(Input.Keys.ENTER, true)
                    .key(Input.Keys.ESCAPE, false).show(stage);
            */
        }
    }

    @Override
    public void update(float dt) {
        if (finished) return;

        game.update(dt);
        if (game.finished()) {
            endGame();
            finished = true;
        }

        setScoreLabel();
        setLevelLabel();

        pauseButton.update(dt);
        exitButton.update(dt);
        musicButton.update(dt);

        handleInput();
    }

    // TODO: Steampunk theme
    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        if (twoPlayer) {
            font.draw(sb, scoreLabel, 295 + 47.5F - scoreLabel.width / 2, 425 - 20 + scoreLabel.height / 2);
            font.draw(sb, levelLabel, 295 + 47.5F - levelLabel.width / 2, 370 - 20 + levelLabel.height / 2);
        } else {
            font.draw(sb, scoreLabel, 240 + 70 - scoreLabel.width / 2, 420 - 40 + scoreLabel.height / 2);
            font.draw(sb, levelLabel, 240 + 70 - levelLabel.width / 2, 320 - 40 + levelLabel.height / 2);
        }
        pauseButton.render(sb);
        exitButton.render(sb);
        musicButton.render(sb);
        sb.end();

        // Upcoming Block - camera movement incoming.
        sb.setProjectionMatrix(nextPieceCamera.combined);
        sb.begin();
        Tetronimo upcoming = game.viewNextBlock();
        upcoming.render(sb);
        sb.end();
        sb.setProjectionMatrix(cam.combined);

        // Held Piece
        sb.setProjectionMatrix(heldPieceCamera.combined);
        sb.begin();
        Tetronimo held = game.getHeldPiece();
        if (held != null) held.render(sb);
        sb.end();
        sb.setProjectionMatrix(cam.combined);

        if (twoPlayer) {
            sb.setProjectionMatrix(heldPieceCamera2.combined);
            sb.begin();
            Tetronimo held2 = game.getHeldPiece2();
            if (held2 != null) held2.render(sb);
            sb.end();
            sb.setProjectionMatrix(cam.combined);
        }

        // Only GameBox stays outside of the sb.begin for any gameObject;
        game.render(sb);
    }

    @Override
    public void dispose() {
        game.dispose();
        font.dispose();
        background.dispose();
        exitButton.dispose();
        pauseButton.dispose();
        musicButton.dispose();
    }
}
