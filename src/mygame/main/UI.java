package mygame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.InputStream;
import java.awt.GradientPaint;

public class UI {

    GamePanel gp;

    Font titleFont;
    Font smallFont;
    Font bigFont;

    // Nút qua màn
    public Rectangle nextLevelBtn = new Rectangle(340, 380, 340, 80);
    public Rectangle backBtn = new Rectangle(340, 490, 340, 80);

    public Rectangle continueBtn;
    public Rectangle menuBtn;

    public UI(GamePanel gp) {
        this.gp = gp;

        // load font pixel cho HUD
        titleFont = loadFont(16f);
        smallFont = loadFont(12f);
        bigFont = loadFont(28f);

        continueBtn = new Rectangle(340, 360, 340, 80);
        menuBtn = new Rectangle(340, 470, 340, 80);
    }

    public void draw(Graphics2D g2) {
        drawPlayerHUD(g2);

        if (gp.gameState == gp.STATE_LEVEL_COMPLETE) {
            drawLevel1WinScreen(g2);
        } else if (gp.gameState == gp.STATE_GAME_WIN) {
            drawGameWinScreen(g2);
        } else if (gp.gameState == gp.STATE_GAME_COMPLETED) {
            drawGameCompletedScreen(g2);
        } else if (gp.gameState == gp.STATE_PAUSE) {
            drawPauseScreen(g2);
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
        // 1. Vẽ nền tối làm mờ màn hình game
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Khung chiến thắng
        int frameW = 420;
        int frameH = 250;
        int frameX = (gp.screenWidth - frameW) / 2;
        int frameY = (gp.screenHeight - frameH) / 2;

        // 3. Thân khung
        Color topColor = new Color(85, 55, 25);
        Color bottomColor = new Color(45, 28, 12);
        GradientPaint gpPaint = new GradientPaint(frameX, frameY, topColor, frameX, frameY + frameH, bottomColor);
        g2.setPaint(gpPaint);
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 30, 30);

        // 4. Viền khung
        g2.setColor(new Color(150, 120, 70));
        g2.setStroke(new java.awt.BasicStroke(4));
        g2.drawRoundRect(frameX, frameY, frameW, frameH, 30, 30);

        int centerY = frameY + 25;

        // Giữ font cũ hỗ trợ dấu cho phần tiếng Việt
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        g2.setColor(new Color(255, 230, 90));
        String title = "CHIẾN THẮNG VANG DỘI!";
        drawCenteredText(g2, title, frameX, frameW, centerY += 35);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub1 = "Lũ gà đã bất lực nhìn " + gp.player.name + " mang trứng đi!";
        drawCenteredText(g2, sub1, frameX, frameW, centerY += 42);

        g2.setFont(new Font("Arial", Font.ITALIC, 15));
        g2.setColor(new Color(255, 180, 100));
        String sub2 = "Thử thách thực sự đang chờ đợi bạn ở phía trước...";
        drawCenteredText(g2, sub2, frameX, frameW, centerY += 30);

        int btnW = 260;
        int btnH = 60;
        int btnX = frameX + (frameW - btnW) / 2;
        int btnY = frameY + frameH - btnH - 25;
        nextLevelBtn.setBounds(btnX, btnY, btnW, btnH);

        boolean hover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 20, 20);

        g2.setColor(new Color(50, 30, 0));
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 20, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(new Color(50, 30, 0));
        drawCenteredTextInButton(g2, "NEXT LEVEL", nextLevelBtn);
    }

    private void drawLevelCompleteScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(loadFont(32f));
        g2.setColor(new Color(255, 230, 90));
        String title = "LEVEL COMPLETE!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 220);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub = "Bạn đã hoàn thành màn chơi";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 270);

        boolean hover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);

        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "NEXT LEVEL", nextLevelBtn);
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
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(loadFont(36f));
        g2.setColor(new Color(255, 230, 90));
        String title = "YOU WIN!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 235);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub = "Bạn đã hoàn thành toàn bộ game";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 285);

        boolean hover = backBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);

        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "BACK TO MENU", backBtn);
    }

    private void drawGameCompletedScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(loadFont(32f));
        g2.setColor(new Color(255, 230, 90));
        String title = "BAN DA HOAN THANH GAME!";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 220);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub = "Chơi lại hoặc quay về màn hình chính";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 265);

        boolean playAgainHover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(playAgainHover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 25, 25);
        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "CHOI LAI", nextLevelBtn);

        boolean backToMenuHover = backBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(backToMenuHover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 25, 25);
        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "QUAY VE MENU", backBtn);
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(loadFont(32f));
        g2.setColor(new Color(255, 230, 90));
        String title = "PAUSED";
        int titleX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 220);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub = "Chọn tiếp tục hoặc quay về menu";
        int subX = gp.screenWidth / 2 - g2.getFontMetrics().stringWidth(sub) / 2;
        g2.drawString(sub, subX, 265);

        boolean continueHover = continueBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(continueHover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(continueBtn.x, continueBtn.y, continueBtn.width, continueBtn.height, 25, 25);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(continueBtn.x, continueBtn.y, continueBtn.width, continueBtn.height, 25, 25);

        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "CONTINUE", continueBtn);

        boolean menuHover = menuBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(menuHover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height, 25, 25);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height, 25, 25);

        g2.setFont(loadFont(20f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, "MAIN MENU", menuBtn);
    }

    private void drawCenteredText(Graphics2D g2, String text, int areaX, int areaWidth, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = areaX + (areaWidth - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    private void drawCenteredTextInButton(Graphics2D g2, String text, Rectangle btn) {
        FontMetrics fm = g2.getFontMetrics();
        int x = btn.x + (btn.width - fm.stringWidth(text)) / 2;
        int y = btn.y + ((btn.height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
    }
}
