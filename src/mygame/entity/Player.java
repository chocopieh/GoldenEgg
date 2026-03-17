package mygame.entity;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;
import mygame.main.KeyHandler;

public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;
    public boolean hasEgg = false;
    public String name = "Player";
    private final int solidAreaDefaultX;
    private final int solidAreaDefaultY;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        solidArea = new Rectangle();
        solidArea.x = 12;
        solidArea.y = 24;
        solidArea.width = 24;
        solidArea.height = 20;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = gp.tileM.playerStartX;
        y = gp.tileM.playerStartY;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;
            gp.cChecker.checkTile(this);

            checkObjectInteraction();

            if (!collisionOn) {
                switch (direction) {
                    case "up":    y -= speed; break;
                    case "down":  y += speed; break;
                    case "left":  x -= speed; break;
                    case "right": x += speed; break;
                }
            }
        }
    }

    public void checkObjectInteraction() {
        if (!hasEgg && gp.tileM.eggRect != null) {
            String object = gp.cChecker.checkEntity(this, gp.tileM.eggRect, "Egg");
            if (object.equals("Egg")) {
                hasEgg = true;
                gp.tileM.eggRect = null;
                System.out.println("Bạn đã nhặt được trứng!");
            }
        }

        if (gp.tileM.houseRect != null) {
            String reachHome = gp.cChecker.checkEntity(this, gp.tileM.houseRect, "House");
            if (reachHome.equals("House")) {
                if (hasEgg) {
                    System.out.println("CHIẾN THẮNG!");
                } else {
                    System.out.println("Tìm trứng đã!");
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = down1;
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);

        // Vẽ tên nhân vật trên đầu
        g2.setFont(g2.getFont().deriveFont(18f));
        FontMetrics fm = g2.getFontMetrics();

        int textX = x + (gp.tileSize - fm.stringWidth(name)) / 2;
        int textY = y - 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(name, textX + 2, textY + 2);

        g2.setColor(Color.WHITE);
        g2.drawString(name, textX, textY);

        if (hasEgg) {
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString("GOT EGG!", x + 1, y - 21);

            g2.setColor(Color.YELLOW);
            g2.drawString("GOT EGG!", x, y - 22);
        }
    }
}