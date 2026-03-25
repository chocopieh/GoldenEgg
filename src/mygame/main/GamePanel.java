package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.entity.Chicken;
import mygame.tile.CollisionChecker;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;
     
    public Sound menuMusic = new Sound();
    public boolean isMenuMusicPlaying = false;
    // ===== GAME STATE =====
    public final int STATE_PLAY = 0;
    public final int STATE_LEVEL_COMPLETE = 1;
    public final int STATE_LEVEL2_PLAY = 2;
    public final int STATE_GAME_WIN = 3;
    public final int STATE_PAUSE = 4;
    public final int STATE_GAME_COMPLETED = 5;  // Thêm trạng thái hoàn thành game

    public int gameState = STATE_PLAY;

    // THIẾT LẬP MÀN HÌNH (64x16 x 64x12)
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // THIẾT LẬP THẾ GIỚI (Cùng kích thước với màn hình trong ví dụ này)
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;
    // KHỞI TẠO HỆ THỐNG
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

    // THỰC THỂ (ENTITIES)
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
        if (player != null) player.name = playerName;
    }

    public void setupGame() {
        // Reset các đối tượng và các trạng thái
        tileM.resetMapObjects();  // Reset bản đồ (các vật phẩm, đối tượng)
        if (player != null) {
            player.setDefaultValues();  // Cài đặt lại giá trị mặc định cho người chơi
            player.name = this.playerName;

            // Đặt lại vị trí của người chơi về điểm xuất phát của level 1
            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        chickens.clear();  // Xóa danh sách gà
        spawnInitialChickens();  // Tạo lại các gà cho cấp độ
        eggSound.setFile("/res/audio/egg.wav");  // Tạo lại âm thanh trứng
        tileM.resetMapObjects();
    }
    
    public void playMenuMusic() {
    if (!isMenuMusicPlaying) {
            menuMusic.setFile("/res/audio/menu.wav");
            if (menuMusic.isLoaded()) {
                menuMusic.play();
                menuMusic.loop();
                isMenuMusicPlaying = true;
            }
        }
    }

    public void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            isMenuMusicPlaying = false;
        }
    }
    
    public void playVictoryMusic() {
        System.out.println("Gọi playVictoryMusic");

        victoryMusic.setFile("/res/audio/victory.wav");

        if (victoryMusic.isLoaded()) {
            victoryMusic.play();
        } else {
            System.out.println("Không load được victory.wav");
        }
    }

    public void showLevel1WinScreen() {
        gameState = STATE_LEVEL_COMPLETE;
        mouseH.resetClick();
        playVictoryMusic(); // nếu bạn muốn phát nhạc chiến thắng
    }
    
    public void playGameOverMusic() {
        gameOverMusic.stop();
        gameOverMusic.setFile("/res/audio/gameover.wav");
        if (gameOverMusic.isLoaded()) {
            gameOverMusic.play();
        }
    }

    private void spawnInitialChickens() {
        // Thêm gà tại vị trí cụ thể hoặc ngẫu nhiên
        int centerX = screenWidth / 2 - tileSize / 2;
        int centerY = screenHeight / 2 - tileSize / 2;
        chickens.add(new Chicken(this, centerX, centerY));
    }

    public void startGameThread() {
        stopMenuMusic();

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
        // Nếu game ở trạng thái "Level Complete"
        if (gameState == STATE_LEVEL_COMPLETE) {
            if (mouseH.clicked && ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.clicked = false;
                startLevel2();  // Tiến tới cấp độ 2
            }
            return;
        }

        // Nếu game ở trạng thái "Game Win"
        if (gameState == STATE_GAME_WIN) {
            if (mouseH.clicked && ui.backBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();
                stopAllSounds();
                stopGameThread();
                main.showMenu();  // Quay lại menu chính
            }
            return;
        }

        // ESC = Pause / Resume
        if (keyH.escapePressed) {
            keyH.escapePressed = false;

            if (gameState == STATE_PLAY) {
                gameState = STATE_PAUSE;

                if (player != null) {
                    player.stopFootstepSound();
                }
                return;

            } else if (gameState == STATE_PAUSE) {
                gameState = STATE_PLAY;
                return;
            }
        }
        // Nếu game ở trạng thái "Game Completed"
        if (gameState == STATE_GAME_COMPLETED) {
            // Kiểm tra nút "Chơi lại"
            if (mouseH.clicked && ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();  // Reset trạng thái clicked
                restartGame();  // Chơi lại game từ đầu
            }

            // Kiểm tra nút "Quay về Menu"
            else if (mouseH.clicked && ui.backBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();  // Reset trạng thái clicked
                stopAllSounds();  // Dừng tất cả âm thanh
                stopGameThread();  // Dừng game
                main.showMenu();  // Quay lại menu chính
            }
            return;  // Dừng cập nhật nếu game đã hoàn thành
        }
         // Kiểm tra trạng thái "Level 2" và điều kiện hoàn thành game
        if (gameState == STATE_LEVEL2_PLAY) {
            if (player != null && player.hasEgg && player.getBounds().intersects(tileM.houseRect)) {
                showGameCompletedScreen();  // Hiển thị màn hình hoàn thành game
                return;
            }
        }

        // Nếu game đang ở trạng thái "Pause", không làm gì thêm
        if (gameState == STATE_PAUSE) {
            return;
        }

        // Nếu game không phải đang ở trạng thái "Play", không làm gì thêm
        if (gameState != STATE_PLAY) return;

        // 2. Cập nhật Player và Kiểm tra nhặt đồ
        if (player != null) {
            player.update();
            tileM.checkItemCollisions(player.getBounds());
            if (tileM.houseRect != null && player.hasEgg && player.getBounds().intersects(tileM.houseRect)) {
                if (currentLevel == 1) {
                    showLevel1WinScreen();  // Hiển thị màn hình "Level Complete" khi hoàn thành Level 1
                    return;
                } else if (currentLevel == 2) {
                    showGameCompletedScreen(); // dùng cái bạn đã có
                    return;
                }
            }
        }

        // 3. Cập nhật danh sách Gà (Dùng Iterator để xóa an toàn khi gà chết)
        Iterator<Chicken> iterator = chickens.iterator();
        while (iterator.hasNext()) {
            Chicken chicken = iterator.next();

            if (chicken.life <= 0) {
                iterator.remove();
                continue;
            }

            chicken.update();

            // Va chạm gây sát thương cho người chơi
            if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                player.takeDamage(10); 
            }
        }

        // 4. Kiểm tra điều kiện Thất bại
        if (player != null && player.health <= 0) {
            player.triggerGameOver();
            return;
        }

        // 5. Cập nhật logic bản đồ (animation lơ lửng của vật phẩm)
        tileM.update();  
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- VẼ THEO THỨ TỰ LỚP (Z-INDEX) ---

        // Lớp 1: Bản đồ nền & Vật phẩm (Trứng/Vũ khí sẽ được TileManager quản lý ẩn/hiện)
        tileM.draw(g2);

        // Lớp 2: Các thực thể (Gà)
        for (Chicken chicken : chickens) {
            chicken.draw(g2);
        }

        // Lớp 3: Người chơi (Vẽ sau gà để đè lên gà khi đi sát)
        if (player != null) {
            player.draw(g2);
           
        }

        // Lớp 4: Tiền cảnh (Cây cối che đầu nhân vật)
        tileM.drawForeground(g2);

        // Lớp 5: Giao diện người dùng (UI luôn nằm trên cùng)
        if (ui != null) {
            ui.draw(g2);
        }
        if (gameState == STATE_PAUSE) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(40f));
            g2.drawString("PAUSED", screenWidth / 2 - 90, screenHeight / 2 - 20);

            g2.setFont(g2.getFont().deriveFont(22f));
            g2.drawString("Nhan ESC de tiep tuc", screenWidth / 2 - 110, screenHeight / 2 + 30);
        }
        g2.dispose();
    }
    
    public void startNewGame() {
        // Reset tất cả các trạng thái game
        gameState = STATE_PLAY;  // Trạng thái bắt đầu chơi game
        currentLevel = 1; // Bắt đầu từ level 1
        tileM.loadLevelMap(1); // 🔥 QUAN TRỌNG: load lại map 1
        setupGame();  // Cài đặt lại game
        stopAllSounds();  // Dừng âm thanh
        stopGameThread();  // Dừng thread game cũ
        startGameThread();  // Bắt đầu thread game mới
    }
    
    public void completeLevel() {
        gameState = STATE_LEVEL_COMPLETE;
        playVictoryMusic();
    }
    
   public void startLevel2() {
        currentLevel = 2;
        gameState = STATE_PLAY;

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
    }
    public void stopAllSounds() {
        menuMusic.stop();
        victoryMusic.stop();
        gameOverMusic.stop();
        eggSound.stop();

        if (player != null) {
            player.stopFootstepSound();
        }
    }

    public void showGameWinScreen() {
        gameState = STATE_GAME_WIN;
        playVictoryMusic();
    }
  
    public void showGameCompletedScreen() {
        gameState = STATE_GAME_COMPLETED;
        mouseH.clicked = false;
        if (player != null) player.stopFootstepSound();
        playVictoryMusic();
        repaint();
    }
   public void restartGame() {
        // Reset trạng thái game và cấp độ
        gameState = STATE_PLAY;  // Trạng thái chơi game
        currentLevel = 1;  // Quay lại level 1
        tileM.loadLevelMap(1);
        setupGame();  // Cài đặt lại game (sẽ reset tất cả các đối tượng)
        stopAllSounds();  // Dừng tất cả âm thanh
        stopGameThread();  // Dừng thread game cũ
        startGameThread();  // Bắt đầu thread game mới
    }
}