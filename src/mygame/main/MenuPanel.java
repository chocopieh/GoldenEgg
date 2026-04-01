package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

public class MenuPanel extends JPanel {

    private final Main main;
    private Image background;

    // Main menu
    private Rectangle startButton, settingsButton, guideButton, exitButton;

    // Settings
    private Rectangle backButton;
    private Rectangle musicBar, sfxBar;
    private Rectangle musicKnob, sfxKnob;
    private Rectangle musicMuteButton, sfxMuteButton;

    private String hoveredButton = "";
    private Font buttonFont;

    private final Sound menuMusic = new Sound();
    private final Sound clickSound = new Sound();

    private boolean inSettings = false;
    private boolean menuMusicPlaying = false;

    private boolean draggingMusic = false;
    private boolean draggingSfx = false;

    private int musicVolume = 70;
    private int sfxVolume = 80;

    private boolean musicMuted = false;
    private boolean sfxMuted = false;

    public MenuPanel(Main main) {
        this.main = main;
        setPreferredSize(new Dimension(1024, 768));
        setFocusable(true);

        loadCustomFont();
        loadBackground();
        updateLayoutBounds();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLayoutBounds();
                repaint();
            }
        });
        setupMouseEvents();

        playMenuMusic();
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            if (is != null) {
                buttonFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(32f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(buttonFont);
            } else {
                buttonFont = new Font("Arial", Font.BOLD, 26);
            }
        } catch (Exception e) {
            System.err.println("Không load được font, dùng font mặc định.");
            buttonFont = new Font("Arial", Font.BOLD, 26);
        }
    }

    private void loadBackground() {
        try {
            background = new ImageIcon(getClass().getResource("/res/ui/menu_bg.png")).getImage();
        } catch (Exception e) {
            System.err.println("Không thể tải menu_bg.png");
        }
    }

    private void updateLayoutBounds() {
        int w = getWidth() > 0 ? getWidth() : 1024;
        int h = getHeight() > 0 ? getHeight() : 768;

        int btnW = 240;
        int btnH = 52;
        int centerX = (w - btnW) / 2;

        // Main menu
        startButton = new Rectangle(centerX, 270, btnW, btnH);
        settingsButton = new Rectangle(centerX, 348, btnW, btnH);
        guideButton = new Rectangle(centerX, 426, btnW, btnH);
        exitButton = new Rectangle(centerX, 504, btnW, btnH);

        // Settings panel
        int panelW = 620;
        int panelH = 450;
        int panelX = (w - panelW) / 2;
        int panelY = 145;

        int sliderX = panelX + 185;   // khớp layout hiện tại
        int sliderW = 280;
        int sliderH = 10;

        musicBar = new Rectangle(sliderX, panelY + 195, sliderW, sliderH);
        sfxBar   = new Rectangle(sliderX, panelY + 300, sliderW, sliderH);

        if (musicKnob == null) musicKnob = new Rectangle(0, 0, 20, 26);
        if (sfxKnob == null) sfxKnob = new Rectangle(0, 0, 20, 26);

        musicMuteButton = new Rectangle(sliderX + sliderW + 95, panelY + 179, 100, 36);
        sfxMuteButton   = new Rectangle(sliderX + sliderW + 95, panelY + 284, 100, 36);

        // BACK để hẳn trong panel, không tràn xuống dưới
        backButton = new Rectangle((w - btnW) / 2, panelY + panelH - btnH - 20, btnW, btnH);

        updateKnobPositions();
    }

    private void setupMouseEvents() {
        MouseAdapter mouseHandler = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (!inSettings) return;

                if (musicKnob.contains(p) || isPointNearBar(p, musicBar)) {
                    draggingMusic = true;
                    updateSliderVolume(e.getX(), true);
                } else if (sfxKnob.contains(p) || isPointNearBar(p, sfxBar)) {
                    draggingSfx = true;
                    updateSliderVolume(e.getX(), false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boolean wasDraggingSfx = draggingSfx;
                draggingMusic = false;
                draggingSfx = false;

                if (wasDraggingSfx && !sfxMuted && sfxVolume > 0) {
                    playClickSound();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!inSettings) return;

                if (draggingMusic) {
                    updateSliderVolume(e.getX(), true);
                } else if (draggingSfx) {
                    updateSliderVolume(e.getX(), false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();

                if (!inSettings) {
                    if (startButton.contains(p)) {
                        playClickSound();
                        handleStartGame();

                    } else if (settingsButton.contains(p)) {
                        playClickSound();
                        inSettings = true;
                        hoveredButton = "";
                        repaint();

                    } else if (guideButton.contains(p)) {
                        playClickSound();
                        showGuide();

                    } else if (exitButton.contains(p)) {
                        playClickSound();
                        System.exit(0);
                    }
                } else {
                    if (musicMuteButton.contains(p)) {
                        toggleMusicMute();

                    } else if (sfxMuteButton.contains(p)) {
                        toggleSfxMute();

                    } else if (backButton.contains(p)) {
                        playClickSound();
                        inSettings = false;
                        hoveredButton = "";
                        repaint();

                    } else if (isPointNearBar(p, musicBar)) {
                        updateSliderVolume(e.getX(), true);

                    } else if (isPointNearBar(p, sfxBar)) {
                        updateSliderVolume(e.getX(), false);
                        if (!sfxMuted && sfxVolume > 0) {
                            playClickSound();
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                String lastHover = hoveredButton;

                if (!inSettings) {
                    if (startButton.contains(p)) hoveredButton = "start";
                    else if (settingsButton.contains(p)) hoveredButton = "settings";
                    else if (guideButton.contains(p)) hoveredButton = "guide";
                    else if (exitButton.contains(p)) hoveredButton = "exit";
                    else hoveredButton = "";
                } else {
                    if (backButton.contains(p)) hoveredButton = "back";
                    else if (musicMuteButton.contains(p)) hoveredButton = "musicMute";
                    else if (sfxMuteButton.contains(p)) hoveredButton = "sfxMute";
                    else hoveredButton = "";
                }

                if (!lastHover.equals(hoveredButton)) {
                    repaint();
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private boolean isPointNearBar(Point p, Rectangle bar) {
        Rectangle expanded = new Rectangle(bar.x - 12, bar.y - 16, bar.width + 24, 40);
        return expanded.contains(p);
    }

    private void updateSliderVolume(int mouseX, boolean isMusic) {
        Rectangle bar = isMusic ? musicBar : sfxBar;

        int relativeX = mouseX - bar.x;
        if (relativeX < 0) relativeX = 0;
        if (relativeX > bar.width) relativeX = bar.width;

        int volume = (int) Math.round((relativeX * 100.0) / bar.width);

        if (isMusic) {
            musicVolume = volume;
            if (musicVolume <= 0) {
                musicMuted = true;
            } else if (musicMuted) {
                musicMuted = false;
            }
            applyMenuMusicVolume();
        } else {
            sfxVolume = volume;
            if (sfxVolume <= 0) {
                sfxMuted = true;
            } else if (sfxMuted) {
                sfxMuted = false;
            }
        }

        updateKnobPositions();
        repaint();
    }

    private void updateKnobPositions() {
        int musicValue = musicMuted ? 0 : musicVolume;
        int sfxValue   = sfxMuted ? 0 : sfxVolume;

        int musicW = musicMuted ? 18 : 20;
        int musicH = musicMuted ? 24 : 26;
        musicKnob.setBounds(
                musicBar.x + (musicValue * musicBar.width / 100) - musicW / 2,
                musicBar.y - 8,
                musicW,
                musicH
        );

        int sfxW = sfxMuted ? 18 : 20;
        int sfxH = sfxMuted ? 24 : 26;
        sfxKnob.setBounds(
                sfxBar.x + (sfxValue * sfxBar.width / 100) - sfxW / 2,
                sfxBar.y - 8,
                sfxW,
                sfxH
        );
    }

    private void toggleMusicMute() {
        musicMuted = !musicMuted;
        applyMenuMusicVolume();
        playClickSound();
        repaint();
    }

    private void toggleSfxMute() {
        boolean willMute = !sfxMuted;
        if (!willMute) {
            sfxMuted = false;
            playClickSound();
        } else {
            sfxMuted = true;
        }
        repaint();
    }

    private void applyMenuMusicVolume() {
        if (!menuMusicPlaying) return;

        if (musicMuted || musicVolume <= 0) {
            menuMusic.setVolume(0);
        } else {
            menuMusic.setVolume(musicVolume);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateLayoutBounds();
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (!inSettings) {
            drawMainMenu(g2);
        } else {
            drawSettingsOverlay(g2);
        }

        g2.dispose();
    }

    private void drawMainMenu(Graphics2D g2) {
        drawGameButton(g2, startButton, "PLAY GAME", hoveredButton.equals("start"));
        drawGameButton(g2, settingsButton, "SETTINGS", hoveredButton.equals("settings"));
        drawGameButton(g2, guideButton, "HOW TO PLAY", hoveredButton.equals("guide"));
        drawGameButton(g2, exitButton, "QUIT", hoveredButton.equals("exit"));
    }

    private void drawSettingsOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int panelW = 620;
        int panelH = 450;
        int panelX = (getWidth() - panelW) / 2;
        int panelY = 145;

        g2.setColor(new Color(20, 12, 6, 225));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 28, 28);

        GradientPaint borderPaint = new GradientPaint(
                panelX, panelY, new Color(255, 225, 120),
                panelX, panelY + panelH, new Color(185, 110, 35)
        );
        g2.setPaint(borderPaint);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 28, 28);

        g2.setFont(buttonFont.deriveFont(40f));
        drawCenteredText(g2, "SETTINGS", getWidth(), 220);

        g2.setFont(buttonFont.deriveFont(18f));
        drawCenteredText(g2, "Adjust music and sound effects", getWidth(), 250);

        drawAudioRow(g2, "MUSIC", musicBar, musicKnob, musicMuteButton, musicVolume, musicMuted, hoveredButton.equals("musicMute"));
        drawAudioRow(g2, "SFX", sfxBar, sfxKnob, sfxMuteButton, sfxVolume, sfxMuted, hoveredButton.equals("sfxMute"));

        drawGameButton(g2, backButton, "BACK", hoveredButton.equals("back"));
    }

    private void drawAudioRow(Graphics2D g2, String label, Rectangle bar, Rectangle knob,
                              Rectangle muteButton, int value, boolean muted, boolean muteHovered) {

        g2.setFont(buttonFont.deriveFont(26f));

        g2.setColor(new Color(45, 25, 10));
        g2.drawString(label, bar.x + 2, bar.y - 18);

        g2.setColor(new Color(255, 240, 200));
        g2.drawString(label, bar.x, bar.y - 20);

        g2.setColor(new Color(60, 35, 12, 220));
        g2.fillRoundRect(bar.x, bar.y, bar.width, bar.height, 12, 12);

        int effectiveValue = muted ? 0 : value;
        int filledWidth = effectiveValue * bar.width / 100;

        GradientPaint fillPaint = new GradientPaint(
                bar.x, bar.y, new Color(255, 225, 120),
                bar.x + bar.width, bar.y, new Color(210, 130, 35)
        );
        g2.setPaint(fillPaint);
        g2.fillRoundRect(bar.x, bar.y, filledWidth, bar.height, 12, 12);

        g2.setColor(new Color(50, 25, 5));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(bar.x, bar.y, bar.width, bar.height, 12, 12);

        GradientPaint knobPaint = new GradientPaint(
                knob.x, knob.y,
                muted ? new Color(155, 155, 155) : new Color(255, 235, 150),
                knob.x, knob.y + knob.height,
                muted ? new Color(100, 100, 100) : new Color(215, 130, 35)
                );
                g2.setPaint(knobPaint);
                g2.fillRoundRect(knob.x, knob.y, knob.width, knob.height, 9, 9);

                g2.setColor(new Color(50, 25, 5));
                g2.drawRoundRect(knob.x, knob.y, knob.width, knob.height, 9, 9);
    }

    private void drawValueBadge(Graphics2D g2, String text, int x, int y, boolean muted) {
        g2.setFont(buttonFont.deriveFont(18f));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text) + 22;
        int h = 28;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(x + 2, y + 4, w, h, 12, 12);

        g2.setColor(muted ? new Color(120, 120, 120) : new Color(88, 50, 18));
        g2.fillRoundRect(x, y, w, h, 12, 12);

        g2.setColor(new Color(255, 240, 200));
        g2.drawRoundRect(x, y, w, h, 12, 12);

        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent() - 1;
        g2.drawString(text, tx, ty);
    }

    private void drawMuteButton(Graphics2D g2, Rectangle rect, String text, boolean hovered, boolean muted) {
        int yOffset = hovered ? -2 : 0;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(rect.x + 2, rect.y + 4, rect.width, rect.height, 10, 10);

        Color top = muted
                ? (hovered ? new Color(120, 180, 120) : new Color(85, 145, 85))
                : (hovered ? new Color(230, 120, 120) : new Color(175, 85, 85));

        Color bottom = muted
                ? (hovered ? new Color(70, 130, 70) : new Color(55, 95, 55))
                : (hovered ? new Color(180, 70, 70) : new Color(120, 55, 55));

        GradientPaint gp = new GradientPaint(
                rect.x, rect.y + yOffset, top,
                rect.x, rect.y + rect.height + yOffset, bottom
        );
        g2.setPaint(gp);
        g2.fillRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 10, 10);

        g2.setColor(new Color(45, 25, 10));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 10, 10);

        g2.setFont(buttonFont.deriveFont(18f));
        FontMetrics fm = g2.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int ty = rect.y + yOffset + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(new Color(255, 240, 200));
        g2.drawString(text, tx, ty);
    }

    private void drawCenteredText(Graphics2D g2, String text, int width, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;

        g2.setColor(new Color(45, 25, 10));
        g2.drawString(text, x + 2, y + 2);

        g2.setColor(new Color(255, 245, 210));
        g2.drawString(text, x, y);
    }

    private void drawGameButton(Graphics2D g2, Rectangle rect, String text, boolean isHovered) {
        int yOffset = isHovered ? -4 : 0;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(rect.x + 3, rect.y + 7, rect.width, rect.height, 12, 12);

        Color colorTop = isHovered ? new Color(255, 225, 120) : new Color(170, 110, 45);
        Color colorBottom = isHovered ? new Color(210, 130, 35) : new Color(110, 65, 15);

        GradientPaint gp = new GradientPaint(
                rect.x, rect.y + yOffset, colorTop,
                rect.x, rect.y + rect.height + yOffset, colorBottom
        );
        g2.setPaint(gp);
        g2.fillRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 12, 12);

        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(new Color(50, 25, 5));
        g2.drawRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 12, 12);

        g2.setFont(buttonFont.deriveFont(30f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + yOffset + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(new Color(45, 25, 10));
        g2.drawString(text, textX + 2, textY + 2);

        g2.setColor(isHovered ? Color.WHITE : new Color(255, 240, 200));
        g2.drawString(text, textX, textY);

        if (isHovered) {
            g2.setColor(new Color(255, 255, 255, 180));
            int decorSize = 8;
            g2.fillRect(textX - 22, textY - 14, decorSize, decorSize);
            g2.fillRect(textX + fm.stringWidth(text) + 14, textY - 14, decorSize, decorSize);

            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRect(rect.x + 10, rect.y + yOffset + 5, rect.width - 20, 2);
        }
    }

    private void handleStartGame() {
        if (main != null && main.gamePanel != null && main.gamePanel.hasSavedProgress()) {
            stopMenuMusic();
            main.showGame();
            main.gamePanel.resumeSavedGame();
            return;
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        NameInputDialog dialog = new NameInputDialog(parent);
        String name = dialog.showDialog();

        if (name != null && !name.trim().isEmpty()) {
            stopMenuMusic();
            main.startGame(name.trim());
        }
    }

    private void showGuide() {
        GuideDialog guide = new GuideDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        guide.showDialog();
    }

    public void playMenuMusic() {
        if (menuMusicPlaying) return;

        menuMusic.setFile("/res/audio/menu_music.wav");
        menuMusic.setVolume(musicMuted ? 0 : musicVolume);
        menuMusic.play();
        menuMusic.loop();
        menuMusicPlaying = true;
    }

    public void stopMenuMusic() {
        menuMusic.stop();
        menuMusicPlaying = false;
    }

    public void playClickSound() {
        if (sfxMuted || sfxVolume <= 0) return;

        clickSound.setFile("/res/audio/click.wav");
        clickSound.setVolume(sfxVolume);
        clickSound.play();
    }

    public int getMusicVolume() {
        return musicMuted ? 0 : musicVolume;
    }

    public int getSfxVolume() {
        return sfxMuted ? 0 : sfxVolume;
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public boolean isSfxMuted() {
        return sfxMuted;
    }
}