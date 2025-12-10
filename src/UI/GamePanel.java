package ui;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import logic.GameLogic;
import data.Wall;
import data.Item; 

public class GamePanel extends JPanel {

    // Estados del juego
    private enum State { MENU, PLAYING, GAME_OVER }
    private State currentState = State.MENU;

    private GameLogic game; 
    private Image background;
    
    // Configuración elegida en el menú
    private boolean isMode2Players = false;

    public GamePanel() {
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        background = new ImageIcon("Borde.png").getImage();
        // Inicialmente game es null hasta que se elija modo

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // --- 1. CONTROLES DEL MENÚ ---
                if (currentState == State.MENU) {
                    if (key == KeyEvent.VK_1) {
                        startGame(false); // 1 Jugador
                    } else if (key == KeyEvent.VK_2) {
                        startGame(true);  // 2 Jugadores
                    }
                }
                
                // --- 2. CONTROLES DURANTE EL JUEGO ---
                else if (currentState == State.PLAYING) {
                    // P1
                    if (key == KeyEvent.VK_UP) game.getPlayer1().setDirectionUp();
                    if (key == KeyEvent.VK_DOWN) game.getPlayer1().setDirectionDown();
                    if (key == KeyEvent.VK_LEFT) game.getPlayer1().setDirectionLeft();
                    if (key == KeyEvent.VK_RIGHT) game.getPlayer1().setDirectionRight();

                    // P2 (Solo si es multi)
                    if (isMode2Players) {
                        if (key == KeyEvent.VK_W) game.getPlayer2().setDirectionUp();
                        if (key == KeyEvent.VK_S) game.getPlayer2().setDirectionDown();
                        if (key == KeyEvent.VK_A) game.getPlayer2().setDirectionLeft();
                        if (key == KeyEvent.VK_D) game.getPlayer2().setDirectionRight();
                    }
                }
                
                // --- 3. CONTROLES GAME OVER ---
                else if (currentState == State.GAME_OVER) {
                    if (key == KeyEvent.VK_R) {
                        startGame(isMode2Players); // Reinicia mismo modo
                    } else if (key == KeyEvent.VK_M) {
                        currentState = State.MENU; // Vuelve al menú
                        repaint();
                    }
                }
            }
        });

        // Loop del juego
        Timer timer = new Timer(16, e -> {
            if (currentState == State.PLAYING) {
                game.updateGame(getWidth(), getHeight());
                if (game.checkGameOver(getWidth(), getHeight())) {
                    currentState = State.GAME_OVER;
                }
            }
            repaint();
        });
        timer.start();
    }
    
    private void startGame(boolean twoPlayers) {
        this.isMode2Players = twoPlayers;
        game = new GameLogic(getWidth(), getHeight(), twoPlayers);
        currentState = State.PLAYING;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- PANTALLA DE MENÚ ---
        if (currentState == State.MENU) {
            drawMenuScreen(g);
            return;
        }

        // --- DIBUJADO DEL JUEGO (FONDO Y ELEMENTOS) ---
        if (game == null) return; // Seguridad

        // 1. Fondo
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // 2. Cuadrícula
        g.setColor(new Color(200, 200, 200, 50)); 
        for (int x = game.getMarginLeft(); x < getWidth() - game.getMarginLeft(); x += game.getTileSize()) {
            g.drawLine(x, game.getMarginTop(), x, getHeight() - game.getMarginTop());
        }
        for (int y = game.getMarginTop(); y < getHeight() - game.getMarginTop(); y += game.getTileSize()) {
            g.drawLine(game.getMarginLeft(), y, getWidth() - game.getMarginLeft(), y);
        }

        // 3. Ítem
        Item item = game.getCurrentItem(); 
        if (item != null) {
            g.setColor(item.getColor()); 
            g.fillRect(item.getX(), item.getY(), item.getSize(), item.getSize());
            g.setColor(Color.BLACK);
            g.drawRect(item.getX(), item.getY(), item.getSize(), item.getSize());
        }

        // 4. Muros
        for (Wall wall : game.getWalls()) {
            g.setColor(wall.getColor());
            g.fillRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
            g.setColor(Color.BLACK);
            g.drawRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
        }

        // 5. Jugadores
        if (game.getPlayer1() != null) {
            g.setColor(game.getPlayer1().getColor());
            g.fillRect(game.getPlayer1().getX(), game.getPlayer1().getY(), 
                       game.getPlayer1().getSize(), game.getPlayer1().getSize());
        }
        
        if (isMode2Players && game.getPlayer2() != null) {
            g.setColor(game.getPlayer2().getColor());
            g.fillRect(game.getPlayer2().getX(), game.getPlayer2().getY(), 
                       game.getPlayer2().getSize(), game.getPlayer2().getSize());
        }

        // 6. HUD
        g.setColor(Color.BLACK);
        String hud = isMode2Players ? "Rojo vs Azul" : "Puntaje: " + game.getScore();
        g.drawString(hud, 112, 122); 
        g.setColor(Color.WHITE);
        g.drawString(hud, 110, 120);

        // --- PANTALLA GAME OVER ---
        if (currentState == State.GAME_OVER) {
            drawGameOverScreen(g);
        }
    }

    private void drawMenuScreen(Graphics g) {
        // Fondo del menú
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        Font titleFont = new Font("Arial", Font.BOLD, 40);
        Font optionFont = new Font("Arial", Font.PLAIN, 25);

        g.setColor(Color.ORANGE);
        g.setFont(titleFont);
        centerText(g, "LARRY EL CONSTRUCTOR", getHeight() / 3);

        g.setColor(Color.WHITE);
        g.setFont(optionFont);
        centerText(g, "Presiona '1' para Un Jugador", getHeight() / 2);
        centerText(g, "Presiona '2' para Dos Jugadores (Versus)", getHeight() / 2 + 40);
        
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 15));
        centerText(g, "Controles: P1 (Flechas) | P2 (WASD)", getHeight() - 50);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        Font titleFont = new Font("Arial", Font.BOLD, 50);
        Font subFont = new Font("Arial", Font.BOLD, 20);
        
        String title;
        Color titleColor = Color.WHITE;

        if (isMode2Players) {
            switch (game.getWinner()) {
                case 1 -> { title = "¡JUGADOR 1 GANA!"; titleColor = Color.RED; }
                case 2 -> { title = "¡JUGADOR 2 GANA!"; titleColor = Color.BLUE; }
                default -> { title = "¡EMPATE!"; }
            }
        } else {
            title = "¡GAME OVER!";
            titleColor = Color.RED;
        }

        g.setFont(titleFont);
        g.setColor(titleColor);
        centerText(g, title, getHeight() / 2 - 20);
        
        g.setFont(subFont);
        g.setColor(Color.WHITE); 
        centerText(g, "Presiona 'R' para Reiniciar", getHeight() / 2 + 30);
        g.setColor(Color.YELLOW);
        centerText(g, "Presiona 'M' para ir al Menú", getHeight() / 2 + 60);
    }

    private void centerText(Graphics g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}