package com.uja.tetris.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.tetris.MainGame;
import com.uja.tetris.states.GameState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by jonathanalp on 2016/08/05.
 */
public class GameBox extends GameObject {
    public static final int NUM_LINES_NOT_PRINTED = 4;
    private static final int TIME_BETWEEN_LEVELS = 30;
    public static int blockSize = 20;
    // Size of tetris board
    public int boardWidth = 10, boardHeight = 24;
    public boolean twoPlayer;
    private Block[][] gameBoard;
    private Tetronimo currentPiece, ghostPiece, heldPiece;
    private Tetronimo currentPiece2, ghostPiece2, heldPiece2;
    private int score, scoreMultiplier;
    private boolean finished;
    private Queue<Tetronimo> pieceQueue;
    private OrthographicCamera mainCamera;
    private OrthographicCamera myCamera;
    private float levelTime;
    private int level;
    private GameState gameState;

    public GameBox(int xOffset, int yOffset, OrthographicCamera mainCamera, GameState gameState, int boardWidth, int boardHeight, int blockSize, boolean twoPlayer) {
        // Set up camera and offsets
        this.mainCamera = mainCamera;
        myCamera = new OrthographicCamera();
        myCamera.setToOrtho(true, MainGame.WIDTH, MainGame.HEIGHT);
        myCamera.position.x = (myCamera.viewportWidth / 2) - xOffset;
        myCamera.position.y = (myCamera.viewportHeight / 2) - yOffset;
        myCamera.update();

        this.gameState = gameState;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.blockSize = blockSize;
        this.twoPlayer = twoPlayer;

        // Gameplay Main
        gameBoard = new Block[this.boardHeight][this.boardWidth]; // Row, Column
        finished = false;
        levelTime = 0;
        level = 1;

        // Scoring
        score = 0;
        scoreMultiplier = 1;

        // Generate Pieces
        pieceQueue = new ArrayDeque<Tetronimo>();
        generateQueue();

        // Player Pieces
        currentPiece = generateNewPiece();
        currentPiece.activate(level);
        heldPiece = null;
        if (twoPlayer) {
            currentPiece2 = generateNewPiece();
            currentPiece2.generatePiece(10, NUM_LINES_NOT_PRINTED);
            currentPiece2.activate(level);
            heldPiece2 = null;
        }
    }

    public GameBox(int xOffset, int yOffset, OrthographicCamera mainCamera, GameState gameState) {
        this(xOffset, yOffset, mainCamera, gameState, 10, 24, 20, false);
    }

    public boolean collides(Tetronimo piece, String direction) {
        Block[] blocks = piece.blocks();
        // Can place blocks in mid air
        // TODO: REFACTOR, TOO LATE TO DO PROPERLY
        for (Block block : blocks) {
            if (collides((int) block.x(), (int) block.y())) return true;
            if (direction.equals("down")) {
                if (block.y() >= boardHeight - 1) return true;
                if (gameBoard[(int) block.y() + 1][(int) block.x()] != null) return true;
            } else if (direction.equals("left")) {
                if (block.x() <= 0) return true;
                if (gameBoard[(int) block.y()][(int) block.x() - 1] != null) return true;
            } else if (direction.equals("right")) {
                if (block.x() >= boardWidth - 1) return true;
                if (gameBoard[(int) block.y()][(int) block.x() + 1] != null) return true;
            }
        }
        return false;
    }

    public boolean twoPieceCollision(Tetronimo piece, String direction) {
        Block[] blocks = piece.blocks();
        for (Block block : blocks) {
            if (currentPiece.equals(piece)) {
                for (Block otherBlock : currentPiece2.blocks()) {
                    if (direction.equals("down")) {
                        if (block.x() == otherBlock.x() && block.y() + 1 == otherBlock.y())
                            return true;
                    } else if (direction.equals("left")) {
                        if (block.x() - 1 == otherBlock.x() && block.y() == otherBlock.y())
                            return true;
                    } else if (direction.equals("right")) {
                        if (block.x() + 1 == otherBlock.x() && block.y() == otherBlock.y())
                            return true;
                    }
                }
            } else if (currentPiece2.equals(piece)) {
                for (Block otherBlock : currentPiece.blocks()) {
                    if (direction.equals("down")) {
                        if (block.x() == otherBlock.x() && block.y() + 1 == otherBlock.y())
                            return true;
                    } else if (direction.equals("left")) {
                        if (block.x() - 1 == otherBlock.x() && block.y() == otherBlock.y())
                            return true;
                    } else if (direction.equals("right")) {
                        if (block.x() + 1 == otherBlock.x() && block.y() == otherBlock.y())
                            return true;
                    }
                }
            } else System.err.println("'THIS' IS BROKEN");
        }
        return false;
    }

    public boolean collides(int x, int y) {
        if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) return true;
        if (gameBoard[y][x] != null) return true;
        return false;
    }

    // Make a bag of pieces to generate according to the tetris rules
    private void generateQueue() {
        ArrayList<Integer> bag = new ArrayList<Integer>();
        for (int i = 0; i < 7; i++) bag.add(i);

        while (bag.size() > 0) {
            pieceQueue.add(new Tetronimo(bag.remove((int) (Math.random() * bag.size())), this));
        }
    }

    public Queue<Tetronimo> getBlockQueue() {
        return pieceQueue;
    }

    public Tetronimo viewNextBlock() {
        return pieceQueue.peek();
    }

    private Tetronimo generateNewPiece() {
        if (pieceQueue.size() < 4)
            generateQueue();

        return pieceQueue.poll();
    }

    private void hold() {
        // Add first time
        if (heldPiece == null) {
            // Check if can hold:
            Tetronimo newVersion = new Tetronimo(viewNextBlock().getType(), this);
            newVersion.generatePiece(0, currentPiece.getNumDrops() + NUM_LINES_NOT_PRINTED);
            if (collides(newVersion, "down")) {
                newVersion.dispose();
                return;
            }

            // Hold:
            heldPiece = currentPiece;
            heldPiece.generatePiece(0, NUM_LINES_NOT_PRINTED);
            currentPiece = generateNewPiece();
            currentPiece.activate(level);
            currentPiece.generatePiece(0, heldPiece.getNumDrops() + NUM_LINES_NOT_PRINTED);
            currentPiece.setDrops(heldPiece.getNumDrops());
            heldPiece.resetDrops();
        }
        // Swap out
        else {
            // Check if can hold:
            Tetronimo newVersion = new Tetronimo(heldPiece.getType(), this);
            newVersion.generatePiece(0, currentPiece.getNumDrops() + NUM_LINES_NOT_PRINTED);
            if (collides(newVersion, "down")) {
                newVersion.dispose();
                return;
            }

            // Hold:
            Tetronimo temp = heldPiece;
            heldPiece = currentPiece;
            heldPiece.generatePiece(0, NUM_LINES_NOT_PRINTED);
            currentPiece = temp;
            currentPiece.generatePiece(0, heldPiece.getNumDrops() + NUM_LINES_NOT_PRINTED);
            currentPiece.setDrops(heldPiece.getNumDrops());
            heldPiece.resetDrops();
        }
    }

    // Eff it - rushing this - copy paste (BLEGH)
    private void hold2() {
        // Add first time
        if (heldPiece2 == null) {
            // Check if can hold:
            Tetronimo newVersion = new Tetronimo(viewNextBlock().getType(), this);
            newVersion.generatePiece(10, currentPiece2.getNumDrops() + NUM_LINES_NOT_PRINTED);
            if (collides(newVersion, "down")) {
                newVersion.dispose();
                return;
            }

            // Hold:
            heldPiece2 = currentPiece2;
            heldPiece2.generatePiece(10, NUM_LINES_NOT_PRINTED);
            currentPiece2 = generateNewPiece();
            currentPiece2.activate(level);
            currentPiece2.generatePiece(10, heldPiece2.getNumDrops() + NUM_LINES_NOT_PRINTED);
            currentPiece2.setDrops(heldPiece2.getNumDrops());
            heldPiece2.resetDrops();
        }
        // Swap out
        else {
            // Check if can hold:
            Tetronimo newVersion = new Tetronimo(heldPiece2.getType(), this);
            newVersion.generatePiece(10, currentPiece2.getNumDrops() + NUM_LINES_NOT_PRINTED);
            if (collides(newVersion, "down")) {
                newVersion.dispose();
                return;
            }

            // Hold:
            Tetronimo temp = heldPiece2;
            heldPiece2 = currentPiece2;
            heldPiece2.generatePiece(10, NUM_LINES_NOT_PRINTED);
            currentPiece2 = temp;
            currentPiece2.generatePiece(10, heldPiece2.getNumDrops() + NUM_LINES_NOT_PRINTED);
            currentPiece2.setDrops(heldPiece2.getNumDrops());
            heldPiece2.resetDrops();
        }
    }

    public Tetronimo getHeldPiece() {
        return heldPiece;
    }

    public Tetronimo getHeldPiece2() {
        return heldPiece2;
    }

    // Clear lines
    private void checkLines() {
        int linesRemoved = 0;
        for (int i = 0; i < boardHeight; i++) {
            boolean lineRemovable = true;
            Block[] line = gameBoard[i];
            for (Block block : line)
                if (block == null) lineRemovable = false;

            if (lineRemovable) {
                linesRemoved++;
                // Remove Line
                for (int j = 0; j < boardWidth; j++) {
                    gameBoard[i][j].dispose();
                    gameBoard[i][j] = null;
                }
                // Drop other pieces
                for (int k = i - 1; k >= 0; k--) {
                    for (int j = 0; j < boardWidth; j++) {
                        Block block = gameBoard[k][j];
                        if (block != null) {
                            block.move("down");
                            gameBoard[k + 1][j] = block;
                            gameBoard[k][j] = null;
                        }
                    }
                }
            }
        }

        // Increase or reset multiplier depending on number of lines removed
        if (linesRemoved >= 4) scoreMultiplier++;
        else if (linesRemoved > 0) scoreMultiplier = 1;

        if (linesRemoved == 1)
            score += 40 * level;
        else if (linesRemoved == 2)
            score += 100 * level;
        else if (linesRemoved == 3)
            score += 300 * level;
        else if (linesRemoved == 4)
            score += 1200 * level;

    }

    public void endGame() {
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    public int getScore() {
        return score;
    }

    public int getScoreMultiplier() {
        return scoreMultiplier;
    }

    public int getLevel() {
        return level;
    }

    public void playRotateSound(float volume) {
        gameState.playRotateSound(volume);
    }

    public void playSetSound(float volume) {
        gameState.playSetSound(volume);
    }

    public void handleInput() {
        if (twoPlayer) {
            if (Gdx.input.isKeyPressed(Input.Keys.A))
                currentPiece.setSideDirection("left");
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                currentPiece.setSideDirection("right");
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                currentPiece2.setSideDirection("left");
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                currentPiece2.setSideDirection("right");

            if (Gdx.input.isKeyPressed(Input.Keys.W))
                currentPiece.doRotation("left");
            if (Gdx.input.isKeyPressed(Input.Keys.UP))
                currentPiece2.doRotation("left");

            if (currentPiece.isSpedUp() && !Gdx.input.isKeyPressed(Input.Keys.S))
                currentPiece.normalSpeed();
            else if (Gdx.input.isKeyPressed(Input.Keys.S) && !currentPiece.isSpedUp())
                currentPiece.speedUp();
            if (currentPiece2.isSpedUp() && !Gdx.input.isKeyPressed(Input.Keys.DOWN))
                currentPiece2.normalSpeed();
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !currentPiece2.isSpedUp())
                currentPiece2.speedUp();

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT))
                hold();
            if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT))
                hold2();
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                currentPiece.setSideDirection("left");
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                currentPiece.setSideDirection("right");

            if (Gdx.input.isKeyPressed(Input.Keys.Z) || Gdx.input.isKeyPressed(Input.Keys.UP))
                currentPiece.doRotation("left");
            if (Gdx.input.isKeyPressed(Input.Keys.X))
                currentPiece.doRotation("right");

            if (currentPiece.isSpedUp() && !Gdx.input.isKeyPressed(Input.Keys.DOWN))
                currentPiece.normalSpeed();
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !currentPiece.isSpedUp())
                currentPiece.speedUp();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                currentPiece.instantDrop();

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT))
                hold();
        }
    }

    @Override
    public void update(float dt) {
        /* TODO: What to do on game end
           Change all blocks colours?
           Flash Game Over */
        if (finished) return;

        // Levels
        levelTime += dt;
        if (levelTime >= TIME_BETWEEN_LEVELS) {
            levelTime = 0;
            level++;
        }

        handleInput();

        if (currentPiece != null) {
            if (currentPiece.isSet()) {
                // Dispose of tetronimo into pieces and replace
                Block[] pieces = currentPiece.blocks();
                for (Block block : pieces) gameBoard[(int) block.y()][(int) block.x()] = block;
                currentPiece.dispose();
                currentPiece = generateNewPiece();
                currentPiece.activate(level);
            } else currentPiece.update(dt);
        }

        if (currentPiece2 != null) {
            if (currentPiece2.isSet()) {
                // Dispose of tetronimo into pieces and replace
                Block[] pieces = currentPiece2.blocks();
                for (Block block : pieces) gameBoard[(int) block.y()][(int) block.x()] = block;
                currentPiece2.dispose();
                currentPiece2 = generateNewPiece();
                currentPiece2.generatePiece(10, NUM_LINES_NOT_PRINTED);
                currentPiece2.activate(level);
            } else currentPiece2.update(dt);
        }

        // Show ghost pieces only before level 10.
        if (level <= 10) {
            if (ghostPiece != null) ghostPiece.dispose();
            ghostPiece = currentPiece.getGhost();
            if (twoPlayer) {
                if (ghostPiece2 != null) ghostPiece2.dispose();
                ghostPiece2 = currentPiece2.getGhost();
            }
        } else {
            ghostPiece = null;
            ghostPiece2 = null;
        }

        checkLines();
    }

    @Override
    public void render(SpriteBatch sb) {
        // Move camera
        sb.setProjectionMatrix(myCamera.combined);

        sb.begin();
        // Render Blocks
        for (int i = NUM_LINES_NOT_PRINTED; i < gameBoard.length; i++) {
            for (Block block : gameBoard[i]) {
                if (block != null) block.render(sb);
            }
        }
        // Render Current Tetronimo
        if (currentPiece != null) currentPiece.render(sb);
        if (currentPiece2 != null) currentPiece2.render(sb);
        if (ghostPiece != null) ghostPiece.render(sb);
        if (ghostPiece2 != null) ghostPiece2.render(sb);
        sb.end();

        // Move camera back
        sb.setProjectionMatrix(mainCamera.combined);
    }

    @Override
    public void dispose() {
        for (Block[] line : gameBoard) {
            for (Block block : line) {
                if (block != null) block.dispose();
            }
        }
        if (currentPiece != null) currentPiece.dispose();
        if (ghostPiece != null) ghostPiece.dispose();
    }

}
