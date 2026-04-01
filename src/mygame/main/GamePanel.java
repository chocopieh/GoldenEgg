package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.entity.Chicken;
import mygame.tile.CollisionChecker;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;
    private boolean hasSavedProgress = false;

    // ===== GAME STATE =====
    public final int STATE_PLAY = 0;
    public final int STATE_LEVEL_COMPLETE = 1;
    public final int STATE_LEVEL2_PLAY = 2;
    public final int STATE_GAME_WIN = 3;
    public final int STATE_PAUSE = 4;
    public final int STATE_GAME_COMPLETED = 5;
    public final int STATE_GAME_OVER = 6;

    public int gameState = STATE_PLAY;

    // SCREEN
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // WORLD
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui;
    public PathFinder pFinder = new PathFinder(this);
    public MouseHandler mouseH = new MouseHandler();

    public Sound gameOverMusic = new Sound();
    public Sound victoryMusic = new Sound();
    public Sound eggSound = new Sound();

    public int currentLevel = 1;
    Thread gameThread;

    // ENTITIES
    public String playerName = "Player";
    public Player player;
    public ArrayList<Chicken> chickens = new ArrayList<>();

    public GamePanel(Main main) {
        this.main = main;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);

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

    public boolean hasSavedProgress() {
        return hasSavedProgress;
    }

    public void saveProgressAndBackToMenu() {
        hasSavedProgress = true;
        gameState = STATE_PAUSE;
        keyH.escapePressed = false;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        stopAllSounds();
        main.showMenu();
    }

    public void resumeSavedGame() {
        hasSavedProgress = false;
        keyH.escapePressed = false;
        mouseH.resetClick();
        gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
        refreshAudioSettings();
        requestFocusInWindow();
    }

    public void clearSavedProgress() {
        hasSavedProgress = false;
    }

    // ===== SETTINGS LINK =====
    private MenuPanel getMenuPanel() {
        return (main != null) ? main.menuPanel : null;
    }

    public int getMusicVolume() {
        MenuPanel mp = getMenuPanel();
        return (mp != null) ? mp.getMusicVolume() : 70;
    }

    public int getSfxVolume() {
        MenuPanel mp = getMenuPanel();
        return (mp != null) ? mp.getSfxVolume() : 80;
    }

    public boolean isMusicMuted() {
        MenuPanel mp = getMenuPanel();
        return mp != null && mp.isMusicMuted();
    }

    public boolean isSfxMuted() {
        MenuPanel mp = getMenuPanel();
        return mp != null && mp.isSfxMuted();
    }

    public void refreshAudioSettings() {
        if (victoryMusic != null && victoryMusic.isLoaded()) {
            victoryMusic.setVolume(isMusicMuted() ? 0 : getMusicVolume());
        }

        if (gameOverMusic != null && gameOverMusic.isLoaded()) {
            gameOverMusic.setVolume(isMusicMuted() ? 0 : getMusicVolume());
        }

        if (eggSound != null && eggSound.isLoaded()) {
            eggSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());
        }
    }

    public void setupGame() {
        gameState = STATE_PLAY;
        hasSavedProgress = false;
        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;

            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        chickens.clear();
        spawnInitialChickens();

        eggSound.setFile("/res/audio/egg.wav");
        eggSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        tileM.resetMapObjects();
    }

    // Menu music giờ do MenuPanel quản lý
    public void playMenuMusic() {
        if (main != null && main.menuPanel != null) {
            main.menuPanel.playMenuMusic();
        }
    }

    public void stopMenuMusic() {
        if (main != null && main.menuPanel != null) {
            main.menuPanel.stopMenuMusic();
        }
    }

    public void playVictoryMusic() {
        System.out.println("Goi playVictoryMusic");
        victoryMusic.stop();
        victoryMusic.setFile("/res/audio/victory.wav");

        if (victoryMusic.isLoaded()) {
            victoryMusic.setVolume(isMusicMuted() ? 0 : getMusicVolume());
            victoryMusic.play();
        } else {
            System.out.println("Khong load duoc victory.wav");
        }
    }

    public void showLevel1WinScreen() {
        clearSavedProgress();
        gameState = STATE_LEVEL_COMPLETE;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
    }

    public void playGameOverMusic() {
        gameOverMusic.stop();
        gameOverMusic.setFile("/res/audio/gameover.wav");

        if (gameOverMusic.isLoaded()) {
            gameOverMusic.setVolume(isMusicMuted() ? 0 : getMusicVolume());
            gameOverMusic.play();
        }
    }

    public void playEggSound() {
        if (!eggSound.isLoaded()) {
            eggSound.setFile("/res/audio/egg.wav");
        }

        eggSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());
        eggSound.play();
    }

    private void spawnInitialChickens() {
        chickens.clear();
        chickens.add(new Chicken(this, 192, 128));
        chickens.add(new Chicken(this, 64, 304));
        chickens.add(new Chicken(this, 896, 192));
        chickens.add(new Chicken(this, 480, 64));
        chickens.add(new Chicken(this, 512, 384));
        chickens.add(new Chicken(this, 896, 640));
        chickens.add(new Chicken(this, 448, 608));
        chickens.add(new Chicken(this, 624, 544));
    }

    public void startGameThread() {
        stopMenuMusic();

        if (gameThread != null) {
            gameThread = null;
        }

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

        Thread currentThread = Thread.currentThread();

        while (gameThread == currentThread) {
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
        if (gameState == STATE_LEVEL_COMPLETE) {
            if (mouseH.clicked && ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.clicked = false;
                startLevel2();
            }
            return;
        }

        if (gameState == STATE_GAME_WIN) {
            if (mouseH.clicked && ui.backBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();
                clearSavedProgress();
                stopAllSounds();
                stopGameThread();
                main.showMenu();
            }
            return;
        }

        if (gameState == STATE_GAME_OVER) {
            return;
        }

        if (keyH.escapePressed) {
            keyH.escapePressed = false;

            if (gameState == STATE_PLAY || gameState == STATE_LEVEL2_PLAY) {
                gameState = STATE_PAUSE;

                if (player != null) {
                    player.stopFootstepSound();
                }

                mouseH.resetClick();
                return;

            } else if (gameState == STATE_PAUSE) {
                hasSavedProgress = false;
                gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
                mouseH.resetClick();
                refreshAudioSettings();
                return;
            }
        }

        if (gameState == STATE_PAUSE) {
            if (mouseH.clicked) {
                if (ui.continueBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    hasSavedProgress = false;
                    gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
                    refreshAudioSettings();
                    return;
                }

                if (ui.menuBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    saveProgressAndBackToMenu();
                    return;
                }
            }
            return;
        }

        if (gameState == STATE_GAME_COMPLETED) {
            if (mouseH.clicked && ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();
                restartGame();
            } else if (mouseH.clicked && ui.backBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();
                clearSavedProgress();
                stopAllSounds();
                stopGameThread();
                main.showMenu();
            }
            return;
        }

        if (gameState != STATE_PLAY && gameState != STATE_LEVEL2_PLAY) {
            return;
        }

        if (player != null) {
            player.update();
            tileM.checkItemCollisions(player.getBounds());

            if (tileM.houseRect != null && player.hasEgg && player.getBounds().intersects(tileM.houseRect)) {
                if (currentLevel == 1) {
                    showLevel1WinScreen();
                    return;
                } else if (currentLevel == 2) {
                    showGameCompletedScreen();
                    return;
                }
            }
        }

        for (int i = 0; i < chickens.size(); i++) {
            Chicken chicken = chickens.get(i);

            if (chicken.alive) {
                chicken.update();

                if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                    if (!player.invincible) {
                        player.takeDamage(10);
                    }
                }
            } else {
                chicken.respawnCounter++;

                if (chicken.respawnCounter >= 300) {
                    chicken.respawn();
                }
            }
        }

        if (player != null && player.health <= 0) {
            player.triggerGameOver();
            return;
        }

        tileM.update();
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
    }

    public void startNewGame() {
        clearSavedProgress();
        gameState = STATE_PLAY;
        currentLevel = 1;
        tileM.loadLevelMap(1);
        setupGame();
        stopAllSounds();
        stopGameThread();
        startGameThread();
    }

    public void completeLevel() {
        clearSavedProgress();
        gameState = STATE_LEVEL_COMPLETE;
        playVictoryMusic();
    }

    public void startLevel2() {
        clearSavedProgress();
        currentLevel = 2;
        gameState = STATE_LEVEL2_PLAY;

        tileM.loadLevelMap(2);

        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;

            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        chickens.clear();
        spawnInitialChickens();
        refreshAudioSettings();
    }

    public void stopAllSounds() {
        victoryMusic.stop();
        gameOverMusic.stop();
        eggSound.stop();

        if (player != null) {
            player.stopFootstepSound();
        }
    }

    public void showGameWinScreen() {
        clearSavedProgress();
        gameState = STATE_GAME_WIN;

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
    }

    public void showGameCompletedScreen() {
        clearSavedProgress();
        gameState = STATE_GAME_COMPLETED;
        mouseH.clicked = false;

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
        repaint();
    }

    public void restartGame() {
        clearSavedProgress();
        gameState = STATE_PLAY;
        currentLevel = 1;
        tileM.loadLevelMap(1);
        setupGame();
        stopAllSounds();
        stopGameThread();
        startGameThread();
    }
}