package mygame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.InputStream;

public class UI {

    GamePanel gp;

    Font titleFont;
    Font smallFont;
    int commandNum = 0;
    int pauseCommandNum = 0;

    private boolean draggingMusic = false;
    private boolean draggingSfx = false;
    private boolean muteClickLock = false;

    // vùng slider ở giao diện chính
    private int mainSliderX = 220;
    private int mainMusicY = 22;
    private int mainSfxY = 50;
    private int mainSliderWidth = 120;
    private int mainSliderHeight = 8;
    private int knobSize = 18;

    public UI(GamePanel gp) {
        this.gp = gp;

        titleFont = loadFont(16f);
        smallFont = loadFont(12f);
    }

    public void draw(Graphics2D g2) {
        drawPlayerHUD(g2);

        if (gp.gameState == gp.pauseState) {
            drawPauseScreen(g2);
        }

        if (gp.gameState == gp.optionState) {
            drawOptionScreen(g2);
        }
    }

    private void drawPlayerHUD(Graphics2D g2) {
        if (gp.player == null) return;

        int panelX = 16;
        int panelY = 16;
        int panelWidth = 180;
        int panelHeight = 60;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);

        g2.setColor(new Color(255, 240, 200));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.name, panelX + 10, panelY + 18);

        int barX = panelX + 10;
        int barY = panelY + 26;
        int barWidth = 110;
        int barHeight = 10;

        g2.setColor(Color.BLACK);
        g2.fillRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);

        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        int currentBar = (int) ((double) gp.player.health / gp.player.maxHealth * barWidth);

        if (gp.player.health > 60) {
            g2.setColor(new Color(80, 220, 120));
        } else if (gp.player.health > 30) {
            g2.setColor(new Color(255, 190, 80));
        } else {
            g2.setColor(new Color(255, 80, 80));
        }

        g2.fillRect(barX, barY, currentBar, barHeight);

        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillRect(barX, barY, currentBar, 2);

        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.health + "/" + gp.player.maxHealth,
                barX + barWidth + 8, barY + 9);

        String eggText = gp.player.hasEgg ? "EGG: YES" : "EGG: NO";

        g2.setColor(gp.player.hasEgg ? new Color(255, 230, 90) : Color.LIGHT_GRAY);
        g2.drawString(eggText, panelX + 10, panelY + 48);
    }

    private void drawMainVolumeHUD(Graphics2D g2) {
        int panelX = 205;
        int panelY = 12;
        int panelWidth = 190;
        int panelHeight = 62;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        g2.setColor(new Color(255, 240, 200));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);

        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);

        g2.drawString("Music", panelX + 10, mainMusicY + 8);
        g2.drawString("SFX", panelX + 10, mainSfxY + 8);

        drawSlider(g2, mainSliderX, mainMusicY, mainSliderWidth, mainSliderHeight, gp.musicVolume, "music", true);
        drawSlider(g2, mainSliderX, mainSfxY, mainSliderWidth, mainSliderHeight, gp.sfxVolume, "sfx", true);
    }

    private void drawPauseScreen(Graphics2D g2) {
        int frameX = gp.tileSize * 4;
        int frameY = gp.tileSize * 2;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 7;

        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        g2.setFont(titleFont.deriveFont(28f));
        g2.drawString("PAUSED", frameX + 70, frameY + 40);

        g2.setFont(titleFont.deriveFont(20f));

        int textX = frameX + 70;
        int textY = frameY + 110;

        g2.drawString("Continue", textX, textY);
        if (pauseCommandNum == 0) g2.drawString(">", textX - 28, textY);

        g2.drawString("Volume", textX, textY + 55);
        if (pauseCommandNum == 1) g2.drawString(">", textX - 28, textY + 55);

        g2.drawString("Exit", textX, textY + 110);
        if (pauseCommandNum == 2) g2.drawString(">", textX - 28, textY + 110);

        g2.setFont(smallFont.deriveFont(16f));
        g2.drawString("UP / DOWN de chon", frameX + 45, frameY + frameHeight - 40);
        g2.drawString("ENTER de xac nhan, ESC de tiep tuc", frameX + 45, frameY + frameHeight - 18);
    }

    private void drawOptionScreen(Graphics2D g2) {

        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 12;
        int frameHeight = gp.tileSize * 8;

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(frameX, frameY, frameWidth, frameHeight, 20, 20);

        g2.setFont(titleFont.deriveFont(28f));
        g2.drawString("OPTIONS", frameX + 30, frameY + 40);

        g2.setFont(titleFont.deriveFont(18f));

        int labelX = frameX + 40;
        int barX = frameX + 220;
        int musicY = frameY + 100;
        int sfxY = frameY + 170;
        int muteY = frameY + 250;

        g2.setColor(Color.WHITE);
        g2.drawString("Music", labelX, musicY + 15);
        g2.drawString("SFX", labelX, sfxY + 15);
        g2.drawString("Mute: " + (gp.soundMuted ? "ON" : "OFF"), labelX, muteY + 15);

        drawSlider(g2, barX, musicY, 260, 20, gp.musicVolume, "music", true);
        drawSlider(g2, barX, sfxY, 260, 20, gp.sfxVolume, "sfx", true);

        handleMuteClick(labelX, muteY);

        g2.setFont(smallFont.deriveFont(16f));
        g2.drawString("Keo thanh de chinh am luong", labelX, frameY + frameHeight - 55);
        g2.drawString("Chi keo chuot de chinh am luong", labelX, frameY + frameHeight - 35);
        g2.drawString("Nhan ESC de quay lai", labelX, frameY + frameHeight - 15);
    }

    private void drawSlider(Graphics2D g2, int x, int y, int width, int height, int value, String type, boolean allowDrag) {

        g2.setColor(new Color(70, 70, 70));
        g2.fillRoundRect(x, y, width, height, 10, 10);

        int fillWidth = width * value / 100;
        g2.setColor(new Color(80, 220, 120));
        g2.fillRoundRect(x, y, fillWidth, height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 10, 10);

        int knobCenterX = x + fillWidth;
        if (knobCenterX < x) knobCenterX = x;
        if (knobCenterX > x + width) knobCenterX = x + width;

        int knobX = knobCenterX - knobSize / 2;
        int knobY = y + height / 2 - knobSize / 2;

        g2.setColor(Color.BLACK);
        g2.fillOval(knobX - 1, knobY - 1, knobSize + 2, knobSize + 2);

        g2.setColor(Color.WHITE);
        g2.fillOval(knobX, knobY, knobSize, knobSize);

        g2.setColor(Color.BLACK);
        g2.drawOval(knobX, knobY, knobSize, knobSize);

        g2.setColor(Color.WHITE);
        g2.drawString(value + "%", x + width + 15, y + height + 5);

        if (allowDrag) {
            handleSliderDrag(x, y, width, height, type);
        }
    }

    private void handleSliderDrag(int x, int y, int width, int height, String type) {

        int mx = gp.mouseH.mouseX;
        int my = gp.mouseH.mouseY;

        int currentValue = type.equals("music") ? gp.musicVolume : gp.sfxVolume;
        int fillWidth = width * currentValue / 100;
        int knobCenterX = x + fillWidth;
        if (knobCenterX < x) knobCenterX = x;
        if (knobCenterX > x + width) knobCenterX = x + width;

        int knobX = knobCenterX - knobSize / 2;
        int knobY = y + height / 2 - knobSize / 2;

        boolean insideBar = mx >= x && mx <= x + width && my >= y - 8 && my <= y + height + 8;
        boolean insideKnob = mx >= knobX && mx <= knobX + knobSize && my >= knobY && my <= knobY + knobSize;

        if (gp.mouseH.pressed) {
            if (type.equals("music")) {
                if ((insideBar || insideKnob || draggingMusic) && !draggingSfx) {
                    draggingMusic = true;
                }
            }
            if (type.equals("sfx")) {
                if ((insideBar || insideKnob || draggingSfx) && !draggingMusic) {
                    draggingSfx = true;
                }
            }
        } else {
            draggingMusic = false;
            draggingSfx = false;
        }

        if (type.equals("music") && draggingMusic) {
            int newValue = (mx - x) * 100 / width;
            if (newValue < 0) newValue = 0;
            if (newValue > 100) newValue = 100;

            gp.musicVolume = newValue;
            gp.applySoundSettings();
        }

        if (type.equals("sfx") && draggingSfx) {
            int newValue = (mx - x) * 100 / width;
            if (newValue < 0) newValue = 0;
            if (newValue > 100) newValue = 100;

            gp.sfxVolume = newValue;
            gp.applySoundSettings();
        }
    }

    private void handleMuteClick(int x, int y) {
        int width = 160;

        boolean inside =
                gp.mouseH.mouseX >= x &&
                gp.mouseH.mouseX <= x + width &&
                gp.mouseH.mouseY >= y - 18 &&
                gp.mouseH.mouseY <= y + 6;

        if (gp.mouseH.pressed && inside && !muteClickLock) {
            gp.soundMuted = !gp.soundMuted;
            gp.applySoundSettings();
            muteClickLock = true;
        }

        if (!gp.mouseH.pressed) {
            muteClickLock = false;
        }
    }

    public void updatePauseMenu() {

        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;
            pauseCommandNum--;
            if (pauseCommandNum < 0) pauseCommandNum = 2;
        }

        if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;
            pauseCommandNum++;
            if (pauseCommandNum > 2) pauseCommandNum = 0;
        }

        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;

            if (pauseCommandNum == 0) {
                gp.gameState = gp.playState;
            } else if (pauseCommandNum == 1) {
                gp.gameState = gp.optionState;
            } else if (pauseCommandNum == 2) {
                gp.stopGameThread();
                gp.main.showMenu();
            }
        }
    }

    public void updateOptions() {
        // để trống hoặc giữ nhẹ, vì volume chỉnh bằng chuột
    }

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
}