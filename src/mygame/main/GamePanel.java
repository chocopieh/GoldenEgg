package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.tile.CollisionChecker;
import java.util.ArrayList;
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

    // TẠM THỜI cho world = screen
    // Nếu map của bạn lớn hơn màn hình thì đổi lại theo map thật
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // KHỞI TẠO HỆ THỐNG
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui;
    public PathFinder pFinder = new PathFinder(this);

    Thread gameThread;

    // TÊN NHÂN VẬT
    public String playerName = "Player";

    // KHỞI TẠO THỰC THỂ
    public Player player;
    public ArrayList<Chicken> chickens = new ArrayList<>();

    public GamePanel(Main main) {
        this.main = main;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        player = new Player(this, keyH);
        ui = new UI(this);
        setupGame();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;

        if (player != null) {
            player.name = playerName;
        }
    }

    public void setupGame() {
        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
        }

        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.escapePressed = false;

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

        if (keyH.escapePressed) {
            keyH.escapePressed = false;
            stopGameThread();
            main.showMenu();
            return;
        }

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