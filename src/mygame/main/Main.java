package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {

    JFrame window;
    CardLayout cardLayout;
    JPanel container;

    GamePanel gamePanel;
    MenuPanel menuPanel;

    public Main() {

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Golden Egg");

        window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        try {
            ImageIcon logoIcon = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/res/tiles/logo.png"))
            );
            window.setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Không thể tải logo");
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        gamePanel = new GamePanel(this);
        menuPanel = new MenuPanel(this);

        container.add(menuPanel, "menu");
        container.add(gamePanel, "game");

        window.add(container);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        showMenu();
    }

    public void showMenu() {
        gamePanel.stopGameThread();

        // reset phím khi quay về menu
        gamePanel.keyH.upPressed = false;
        gamePanel.keyH.downPressed = false;
        gamePanel.keyH.leftPressed = false;
        gamePanel.keyH.rightPressed = false;
        gamePanel.keyH.escapePressed = false;

        cardLayout.show(container, "menu");
        container.revalidate();
        container.repaint();

        SwingUtilities.invokeLater(() -> {
            menuPanel.requestFocusInWindow();
            menuPanel.grabFocus();
        });

        menuPanel.playMenuMusic();
    }

    public void startGame(String playerName) {
        gamePanel.stopGameThread();

        // reset phím trước khi vào lại game
        gamePanel.keyH.upPressed = false;
        gamePanel.keyH.downPressed = false;
        gamePanel.keyH.leftPressed = false;
        gamePanel.keyH.rightPressed = false;
        gamePanel.keyH.escapePressed = false;

        gamePanel.setPlayerName(playerName);
        gamePanel.setupGame();

        cardLayout.show(container, "game");
        container.revalidate();
        container.repaint();

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            gamePanel.grabFocus();
            gamePanel.startGameThread();
        });
    }

    public static void main(String[] args) {
        new Main();
    }
}