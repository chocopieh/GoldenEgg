package mygame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.InputStream;

public class UI {

    GamePanel gp;

    Font titleFont;
    Font smallFont;
    Font bigFont;

    // Nút qua màn
    public Rectangle nextLevelBtn = new Rectangle(340, 390, 340, 80);

    // 🔥 THÊM DÒNG NÀY
    public Rectangle backBtn = new Rectangle(340, 400, 340, 80);

    public UI(GamePanel gp) {
        this.gp = gp;

        // load font pixel
        titleFont = loadFont(16f);
        smallFont = loadFont(12f);
        bigFont = loadFont(28f);
    }

    public void draw(Graphics2D g2) {
        drawPlayerHUD(g2);

        if (gp.gameState == gp.STATE_LEVEL_COMPLETE) {
            drawLevel1WinScreen(g2);
        } else if (gp.gameState == gp.STATE_GAME_WIN) {
            drawGameWinScreen(g2);
        }
    }

    private void drawPlayerHUD(Graphics2D g2) {
        if (gp.player == null) return;

        int panelX = 16;
        int panelY = 16;
        int panelWidth = 180;
        int panelHeight = 60;

        // nền HUD
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);

        // viền pixel style
        g2.setColor(new Color(255, 240, 200));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        // ===== NAME =====
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.name, panelX + 10, panelY + 18);

        // ===== HP BAR =====
        int barX = panelX + 10;
        int barY = panelY + 26;
        int barWidth = 110;
        int barHeight = 10;

        // viền ngoài
        g2.setColor(Color.BLACK);
        g2.fillRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);

        // nền bar
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        // lượng máu
        int currentBar = (int) ((double) gp.player.health / gp.player.maxHealth * barWidth);

        if (gp.player.health > 60) {
            g2.setColor(new Color(80, 220, 120));
        } else if (gp.player.health > 30) {
            g2.setColor(new Color(255, 190, 80));
        } else {
            g2.setColor(new Color(255, 80, 80));
        }

        g2.fillRect(barX, barY, currentBar, barHeight);

        // highlight
        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillRect(barX, barY, currentBar, 2);

        // text HP
        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.health + "/" + gp.player.maxHealth,
                barX + barWidth + 8, barY + 9);

        // ===== EGG STATUS =====
        String eggText = gp.player.hasEgg ? "EGG: YES" : "EGG: NO";

        g2.setColor(gp.player.hasEgg ? new Color(255, 230, 90) : Color.LIGHT_GRAY);
        g2.drawString(eggText, panelX + 10, panelY + 48);
    }
    private void drawLevel1WinScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(loadFont(34f));
        g2.setColor(new Color(255, 230, 90));
        String title = "LEVEL 1 COMPLETE!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 220);

        g2.setFont(loadFont(18f));
        g2.setColor(Color.WHITE);
        String sub = "Ban da chien thang level 1";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 280);

        boolean hover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);

        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        g2.setFont(loadFont(22f));
        String btnText = "NEXT LEVEL";
        int btnTextX = nextLevelBtn.x + nextLevelBtn.width / 2 - g2.getFontMetrics().stringWidth(btnText) / 2;
        int btnTextY = nextLevelBtn.y + 48;
        g2.drawString(btnText, btnTextX, btnTextY);
    }

    private void drawLevelCompleteScreen(Graphics2D g2) {
        // nền tối
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // tiêu đề
        g2.setFont(loadFont(34f));
        g2.setColor(new Color(255, 230, 90));
        String title = "LEVEL COMPLETE!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 220);

        // mô tả
        g2.setFont(loadFont(18f));
        g2.setColor(Color.WHITE);
        String sub = "Ban da hoan thanh man choi";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 280);

        // hover nút
        boolean hover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);

        // nền nút
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        // viền nút
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        // chữ nút
        g2.setFont(loadFont(22f));
        g2.setColor(Color.BLACK);
        String btnText = "NEXT LEVEL";
        int btnTextX = nextLevelBtn.x + nextLevelBtn.width / 2 - g2.getFontMetrics().stringWidth(btnText) / 2;
        int btnTextY = nextLevelBtn.y + 48;
        g2.drawString(btnText, btnTextX, btnTextY);
    }

    // ===== LOAD FONT =====
    private Font loadFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");

            if (is == null) {
                throw new RuntimeException("Không tìm thấy font!");
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }
    private void drawGameWinScreen(Graphics2D g2) {
        // nền tối
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // tiêu đề
        g2.setFont(loadFont(40f));
        g2.setColor(new Color(255, 230, 90));
        String title = "YOU WIN!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 240);

        // mô tả
        g2.setFont(loadFont(20f));
        g2.setColor(Color.WHITE);
        String sub = "BAN DA HOAN THANH TOAN BO GAME";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 300);

        // hover nút
        boolean hover = backBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);

        // nền nút
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);

        // viền nút
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);

        // chữ nút
        g2.setFont(loadFont(22f));
        g2.setColor(Color.BLACK);
        String btnText = "BACK TO MENU";
        int btnTextX = backBtn.x + backBtn.width / 2 - g2.getFontMetrics().stringWidth(btnText) / 2;
        int btnTextY = backBtn.y + 48;
        g2.drawString(btnText, btnTextX, btnTextY);
       }
}