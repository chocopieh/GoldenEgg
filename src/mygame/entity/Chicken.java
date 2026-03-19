package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;

public class Chicken {

    GamePanel gp;
    public int x, y;
    public int speed = 2;

    public Rectangle solidArea;
    public BufferedImage image;

    private String direction = "down";
    private final Random random = new Random();
    private int directionChangeCounter = 0;

    public Chicken(GamePanel gp, int x, int y) {
        this.gp = gp;
        this.x = x;
        this.y = y;

        solidArea = new Rectangle(8, 8, 48, 48);

        loadImage();
        pickRandomDirection();
        unstickIfNeeded();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(
                x + solidArea.x,
                y + solidArea.y,
                solidArea.width,
                solidArea.height
        );
    }

    private boolean willCollide(int nextX, int nextY) {
        Rectangle nextBounds = new Rectangle(
                nextX + solidArea.x,
                nextY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        for (Rectangle rect : gp.tileM.collisionRects) {
            if (nextBounds.intersects(rect)) {
                return true;
            }
        }

        if (nextX < 0 || nextY < 0 ||
            nextX + gp.tileSize > gp.screenWidth ||
            nextY + gp.tileSize > gp.screenHeight) {
            return true;
        }

        return false;
    }

    private void pickRandomDirection() {
        int value = random.nextInt(4);

        switch (value) {
            case 0 -> direction = "up";
            case 1 -> direction = "down";
            case 2 -> direction = "left";
            case 3 -> direction = "right";
        }
    }

    private boolean tryMove(String dir) {
        int nextX = x;
        int nextY = y;

        switch (dir) {
            case "up" -> nextY -= speed;
            case "down" -> nextY += speed;
            case "left" -> nextX -= speed;
            case "right" -> nextX += speed;
        }

        if (!willCollide(nextX, nextY)) {
            x = nextX;
            y = nextY;
            direction = dir;
            return true;
        }

        return false;
    }

    private void unstickIfNeeded() {
        if (!willCollide(x, y)) {
            return;
        }

        for (int radius = 4; radius <= 64; radius += 4) {
            if (!willCollide(x + radius, y)) {
                x += radius;
                return;
            }
            if (!willCollide(x - radius, y)) {
                x -= radius;
                return;
            }
            if (!willCollide(x, y + radius)) {
                y += radius;
                return;
            }
            if (!willCollide(x, y - radius)) {
                y -= radius;
                return;
            }
        }
    }

    public void update() {
        directionChangeCounter++;

        // thỉnh thoảng đổi hướng tự nhiên hơn
        if (directionChangeCounter > 90) {
            directionChangeCounter = 0;
            if (random.nextInt(100) < 25) {
                pickRandomDirection();
            }
        }

        // thử đi theo hướng hiện tại trước
        if (tryMove(direction)) {
            return;
        }

        // nếu bị chặn, thử các hướng khác ngay lập tức
        String[] directions = {"up", "down", "left", "right"};

        for (int i = 0; i < 8; i++) {
            String tryDir = directions[random.nextInt(4)];
            if (tryMove(tryDir)) {
                return;
            }
        }

        // nếu vẫn bí thì thử tự gỡ kẹt
        unstickIfNeeded();
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}