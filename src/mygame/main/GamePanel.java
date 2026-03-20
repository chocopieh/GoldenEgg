package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.tile.CollisionChecker;
import mygame.entity.Chicken;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;

    // THIẾT LẬP MÀN HÌNH
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // World tạm = screen
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // KHỞI TẠO HỆ THỐNG
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public MouseHandler mouseH = new MouseHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui;
    public PathFinder pFinder = new PathFinder(this);
    public Sound bgMusic = new Sound();

    Thread gameThread;

    // GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int optionState = 3;

    // ÂM THANH
    public int musicVolume = 70;
    public int sfxVolume = 80;
    public boolean soundMuted = false;

    // TÊN NHÂN VẬT
    public String playerName = "Player";

    // THỰC THỂ
    public Player player;
    public ArrayList<Chicken> chickens = new ArrayList<>();

    public GamePanel(Main main) {
        this.main = main;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);

        player = new Player(this, keyH);
        ui = new UI(this);

        gameState = playState;

        setupGame();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;

        if (player != null) {
            player.name = playerName;
        }
    }

    public void setupGame() {
        bgMusic.setFile("/res/sound/bg.wav");
        applySoundSettings();
        bgMusic.loop();

        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
        }

        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.escapePressed = false;
        keyH.optionPressed = false;
        keyH.enterPressed = false;

        chickens.clear();

        int centerX = screenWidth / 2 - tileSize / 2;
        int centerY = screenHeight / 2 - tileSize / 2;

        chickens.add(new Chicken(this, centerX, centerY));
    }

    public void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {

        // phím O mở / đóng option
        if (keyH.optionPressed) {
            keyH.optionPressed = false;

            if (gameState == playState) {
                gameState = optionState;
            } else if (gameState == optionState) {
                gameState = playState;
            } else if (gameState == pauseState) {
                gameState = optionState;
            }
        }

        // phím ESC mở pause menu
        if (keyH.escapePressed) {
            keyH.escapePressed = false;

            if (gameState == playState) {
                gameState = pauseState;
                return;
            } else if (gameState == pauseState) {
                gameState = playState;
                return;
            } else if (gameState == optionState) {
                gameState = pauseState;
                return;
            }
        }

        if (gameState == playState) {

            if (player != null) {
                player.update();
            }

            for (Chicken chicken : chickens) {
                chicken.update();

                if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                    player.takeDamage(10);
                }
            }

            if (player != null && player.health <= 0) {
                player.triggerGameOver();
                return;
            }

        } else if (gameState == pauseState) {
            ui.updatePauseMenu();
        } else if (gameState == optionState) {
            ui.updateOptions();
        }
    }

    public void applySoundSettings() {
        if (soundMuted || musicVolume <= 0) {
            bgMusic.stop();
        } else {
            bgMusic.setVolume(convertPercentToDb(musicVolume));

            if (bgMusic.clip != null && !bgMusic.clip.isRunning()) {
                bgMusic.loop();
            }
        }
  }

   public float convertPercentToDb(int percent) {
        if (percent <= 0) return -80f; // tắt hẳn

        float minDb = -80f;
        float maxDb = 0f;

        return minDb + (maxDb - minDb) * (percent / 100f);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);

        for (Chicken chicken : chickens) {
            chicken.draw(g2);
        }

        if (player != null) {
            player.draw(g2);
        }

        tileM.drawForeground(g2);

        if (ui != null) {
            ui.draw(g2);
        }

        g2.dispose();
    }
}