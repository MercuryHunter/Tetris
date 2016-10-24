package com.uja.tetris.gameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jonathanalp on 2016/08/03.
 */
public class Tetronimo extends GameObject {
    private GameBox gameBox;
    private Block[] piece;
    private int type, centreIndex;
    private boolean ghost;

    //private static final float time_side_move = 0.15F, time_between_rotations = 0.15F;
    private float time_till_drop, time_side_move, time_between_rotations;
    private int numDrops;
    private float timePassed, sideTimePassed, rotateTimePassed;
    private String sideDirection, rotateDirection;
    private boolean instantDrop;

    private boolean collided, set;

    private int level;
    private boolean spedUp;

    public Tetronimo(int type, GameBox gameBox) {
        this.gameBox = gameBox;

        this.type = type;
        piece = new Block[4];

        numDrops = 0;
        sideDirection = null;
        rotateDirection = null;

        timePassed = 0;
        sideTimePassed = 0;
        rotateTimePassed = 0;

        instantDrop = false;
        spedUp = false;

        ghost = false;

        generatePiece(0, GameBox.NUM_LINES_NOT_PRINTED);
    }

    // For ghosting only.
    private Tetronimo(Block[] piece, GameBox gameBox) {
        this.piece = piece;
        this.gameBox = gameBox;
        rotateDirection = null;
        timePassed = 0;
        sideTimePassed = 0;
        rotateTimePassed = 0;
        instantDrop = true;
        ghost = true;
    }

    public void generatePiece(int xOffset, int yOffset) {
        // Generate Piece
        switch (type) {
            case 0:
                piece[0] = new Block(3 + xOffset, 0 + yOffset, Color.BLUE);
                piece[1] = new Block(4 + xOffset, 0 + yOffset, Color.BLUE);
                piece[2] = new Block(5 + xOffset, 0 + yOffset, Color.BLUE);
                piece[3] = new Block(6 + xOffset, 0 + yOffset, Color.BLUE);
                centreIndex = 1;
                break;
            case 1:
                piece[0] = new Block(4 + xOffset, 0 + yOffset, Color.YELLOW);
                piece[1] = new Block(4 + xOffset, 1 + yOffset, Color.YELLOW);
                piece[2] = new Block(5 + xOffset, 0 + yOffset, Color.YELLOW);
                piece[3] = new Block(5 + xOffset, 1 + yOffset, Color.YELLOW);
                centreIndex = 1;
                break;
            case 2:
                piece[0] = new Block(3 + xOffset, 1 + yOffset, Color.PURPLE);
                piece[1] = new Block(4 + xOffset, 0 + yOffset, Color.PURPLE);
                piece[2] = new Block(4 + xOffset, 1 + yOffset, Color.PURPLE);
                piece[3] = new Block(5 + xOffset, 1 + yOffset, Color.PURPLE);
                centreIndex = 2;
                break;
            case 3:
                piece[0] = new Block(4 + xOffset, 0 + yOffset, Color.GREEN);
                piece[1] = new Block(5 + xOffset, 0 + yOffset, Color.GREEN);
                piece[2] = new Block(3 + xOffset, 1 + yOffset, Color.GREEN);
                piece[3] = new Block(4 + xOffset, 1 + yOffset, Color.GREEN);
                centreIndex = 0;
                break;
            case 4:
                piece[0] = new Block(3 + xOffset, 0 + yOffset, Color.RED);
                piece[1] = new Block(4 + xOffset, 0 + yOffset, Color.RED);
                piece[2] = new Block(4 + xOffset, 1 + yOffset, Color.RED);
                piece[3] = new Block(5 + xOffset, 1 + yOffset, Color.RED);
                centreIndex = 2;
                break;
            case 5:
                piece[0] = new Block(3 + xOffset, 0 + yOffset, Color.MAROON);
                piece[1] = new Block(3 + xOffset, 1 + yOffset, Color.MAROON);
                piece[2] = new Block(4 + xOffset, 1 + yOffset, Color.MAROON);
                piece[3] = new Block(5 + xOffset, 1 + yOffset, Color.MAROON);
                centreIndex = 1;
                break;
            case 6:
                piece[0] = new Block(5 + xOffset, 0 + yOffset, Color.OLIVE);
                piece[1] = new Block(3 + xOffset, 1 + yOffset, Color.OLIVE);
                piece[2] = new Block(4 + xOffset, 1 + yOffset, Color.OLIVE);
                piece[3] = new Block(5 + xOffset, 1 + yOffset, Color.OLIVE);
                centreIndex = 3;
                break;
        }
    }

    public Tetronimo getGhost() {
        Block[] newPieces = new Block[4];
        for (int i = 0; i < piece.length; i++)
            newPieces[i] = piece[i].ghostClone();
        Tetronimo ghost = new Tetronimo(newPieces, gameBox);
        ghost.update(0);
        return ghost;
    }

    public void activate(int level) {
        // Check if the player loses
        for (int i = 0; i < piece.length; i++) {
            if (gameBox.collides((int) piece[i].x(), (int) piece[i].y())) {
                gameBox.endGame();
            }
        }
        // Set time based on level
        this.level = level;
        normalSpeed();
    }

    public boolean isCollided() {
        return collided;
    }

    public boolean isSet() {
        return set;
    }

    public Block[] blocks() {
        return piece;
    }

    public int getType() {
        return type;
    }

    public void rotate(String direction) {
        Block centre = piece[centreIndex];
        for (int i = 0; i < piece.length; i++) {
            if (i != centreIndex) {
                Block current = piece[i];
                Vector2 currentPosition = current.getPosition();
                int xDist = (int) currentPosition.x - (int) centre.x();
                int yDist = (int) currentPosition.y - (int) centre.y();

                if (direction.equals("left")) currentPosition.add(-xDist + yDist, -yDist - xDist);
                else currentPosition.add(-xDist - yDist, -yDist + xDist);

                current.setPosition(currentPosition);
            }
        }
        gameBox.playRotateSound(0.1F);
    }

    public boolean canRotate(String direction) {
        if (type == 1) return false;
        Block centre = piece[centreIndex];
        for (int i = 0; i < piece.length; i++) {
            if (i != centreIndex) {
                Block current = piece[i];
                Vector2 currentPosition = current.getPosition();
                int xDist = (int) currentPosition.x - (int) centre.x();
                int yDist = (int) currentPosition.y - (int) centre.y();

                int newX, newY;
                if (direction.equals("left")) {
                    newX = (int) currentPosition.x - xDist + yDist;
                    newY = (int) currentPosition.y - yDist - xDist;
                } else {
                    newX = (int) currentPosition.x - xDist - yDist;
                    newY = (int) currentPosition.y - yDist + xDist;
                }
                if (gameBox.collides(newX, newY)) return false;
            }
        }
        return true;
    }

    public void setSideDirection(String sideDirection) {
        if (sideTimePassed > time_side_move) this.sideDirection = sideDirection;
    }

    public void doRotation(String rotation) {
        if (rotateTimePassed > time_between_rotations) rotateDirection = rotation;
    }

    public void speedUp() {
        spedUp = true;
        time_till_drop = Math.max(1.0F / 60F, time_till_drop - 10.0F / 60.0F);
        time_side_move = Math.min(0.15F, time_till_drop);
        time_between_rotations = time_side_move;
    }

    public void normalSpeed() {
        spedUp = false;
        time_till_drop = Math.max(1.0F / 60.0F, (20.0F - level) * (1.0F / 60.0F));
        time_side_move = Math.min(0.15F, time_till_drop);
        time_between_rotations = time_side_move;
    }

    public int getNumDrops() {
        return numDrops;
    }

    public void resetDrops() {
        numDrops = 0;
    }

    public void setDrops(int drops) {
        numDrops = drops;
    }

    public void instantDrop() {
        instantDrop = true;
    }

    public boolean isSpedUp() {
        return spedUp;
    }

    @Override
    public void update(float dt) {
        timePassed += dt;
        sideTimePassed += dt;
        rotateTimePassed += dt;

        if (rotateDirection != null && canRotate(rotateDirection)) {
            rotate(rotateDirection);
            rotateDirection = null;
            rotateTimePassed = 0;

            // Rotated piece may not be on top of anything anymore
            if (!gameBox.collides(this, "down")) {
                collided = false;
                set = false;
            }
        }

        if (sideDirection != null && !gameBox.collides(this, sideDirection)) {
            if (!gameBox.twoPlayer || !gameBox.twoPieceCollision(this, sideDirection)) {
                for (Block block : piece) block.move(sideDirection);
                sideDirection = null;
                sideTimePassed = 0;

                // Side-moved piece may not be on top of anything anymore
                if (!gameBox.collides(this, "down")) {
                    collided = false;
                    set = false;
                }
            }
        }

        if (timePassed > time_till_drop) {
            if (collided) {
                set = true;
                gameBox.playSetSound(0.3F);
            } else if (gameBox.collides(this, "down"))
                collided = true;
            else if (gameBox.twoPlayer && gameBox.twoPieceCollision(this, "down"))
                collided = false;
            else {
                numDrops++;
                for (Block block : piece) block.move("down");
            }
            timePassed -= time_till_drop;
        }

        if (instantDrop) {
            while (!gameBox.collides(this, "down")) {
                for (Block block : piece) block.move("down");
                numDrops++;
            }
        }
    }

    // UGLY
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tetronimo)) return false;
        Tetronimo otherPiece = (Tetronimo) other;
        for (int i = 0; i < piece.length; i++) {
            if (piece[i].x() != otherPiece.piece[i].x()) return false;
            if (piece[i].y() != otherPiece.piece[i].y()) return false;
        }
        return true;
    }

    @Override
    public void render(SpriteBatch sb) {
        for (Block block : piece) block.render(sb);
    }

    @Override
    public void dispose() {
        if (ghost)
            for (Block block : piece)
                block.dispose();
    }

}
