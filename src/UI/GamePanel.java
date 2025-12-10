package UI;

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
import Logic.GameLogic;
import Data.Wall;
import Data.Item; 
import Logic.SoundPlayer;
import java.io.File;

public class GamePanel extends JPanel {
    
    // musica
    private SoundPlayer soundPlayer = new SoundPlayer();


    // Estados del juego
    private enum State { MENU, PLAYING, GAME_OVER }
    private State currentState = State.MENU;

    private GameLogic game; 
    private Image background;
    
    // Imágenes del menú
    private Image imgP1;
    private Image imgP2;
    private Image Fondo;
    
    //fuente de juegos
    private Font pressStartFont;
    
    // Configuración elegida en el menú
    private boolean isMode2Players = false;

    public GamePanel() {
        enterMenu();
        // Cargar fuente Press Start 2P
        try {
            pressStartFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("fonts/PressStart2P.ttf")   // Ruta del .ttf
            ).deriveFont(32f); // tamaño
        } catch (Exception e) {
            System.out.println("Error cargando fuente. Usando fallback.");
            pressStartFont = new Font("Monospaced", Font.BOLD, 32);
        }
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        // Cargar imágenes del menú
        Fondo = new ImageIcon("Fondo.png").getImage();
        imgP1 = new ImageIcon("P1.png").getImage();
        imgP2 = new ImageIcon("P2.png").getImage();
        
        
        background = new ImageIcon("Borde.png").getImage();
        // Inicialmente game es null hasta que se elija modo

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // --- 1. CONTROLES DEL MENÚ ---
                if (currentState == State.MENU) {
                    if (key == KeyEvent.VK_1) {
                        soundPlayer.stop(); //detener musica
                        startGame(false); // 1 Jugador
                    } else if (key == KeyEvent.VK_2) {
                        soundPlayer.stop(); //detener musica
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
                        enterMenu();
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
        g.setFont(pressStartFont.deriveFont(18f)); 
        g.setColor(Color.BLACK);
        String hud = isMode2Players ? "Rojo vs amarillo" : "Puntaje: " + game.getScore();
        g.drawString(hud, 112, 122); 
        g.setColor(Color.GRAY);
        g.drawString(hud, 110, 120);

        // --- PANTALLA GAME OVER ---
        if (currentState == State.GAME_OVER) {
            drawGameOverScreen(g);
        }
    }

    private void drawMenuScreen(Graphics g) {
        
        
        // Fondo del menú
        g.drawImage(Fondo, 0, 0, getWidth(), getHeight(), this);
        
        // --- IMÁGENES P1 Y P2 ---
        int imgWidth = 180;
        int imgHeight = 180;

        int p1X = (int)(getWidth() * 0.20) - imgWidth / 2;
        int pY = getHeight() / 2 - imgHeight;
        g.drawImage(imgP1, p1X, pY, imgWidth, imgHeight, this);

        int p2X = (int)(getWidth() * 0.80) - imgWidth / 2;
        g.drawImage(imgP2, p2X, pY, imgWidth, imgHeight, this);
        
        

        Font titleFont = new Font("Arial", Font.BOLD, 40);
        Font optionFont = new Font("Arial", Font.PLAIN, 25);

        g.setColor(Color.ORANGE);
        g.setFont(pressStartFont);
        centerText(g, "LARRY EL CONSTRUCTOR", getHeight() / 4);

        g.setColor(Color.WHITE);
        g.setFont(pressStartFont.deriveFont(18f));
        centerText(g, "Presiona '1' para Un Jugador", getHeight() / 2+50);
        centerText(g, "Presiona '2' para Dos Jugadores (Versus)", getHeight() / 2 + 90);
        
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 15));
        centerText(g, "Controles: P1 (Flechas) | P2 (WASD)", getHeight() - 50);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        Font titleFont = pressStartFont.deriveFont(50f);
        g.setFont(titleFont);
        Color strongRed = new Color(102, 11, 25);
        Font subFont = pressStartFont.deriveFont(18f);
        
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
        g.setColor(strongRed);
        centerText(g, title, getHeight() / 2 - 16);
        g.setColor(titleColor);
        centerText(g, title, getHeight() / 2 - 20);
        
        g.setFont(subFont);
        g.setColor(Color.WHITE); 
        centerText(g, "Presiona 'R' para Reiniciar", getHeight() / 2 + 30);
        g.setColor(Color.YELLOW);
        centerText(g, "Presiona 'M' para ir al Menú", getHeight() / 2 + 60);
    }
    private void enterMenu() {
        currentState = State.MENU;
        soundPlayer.stop(); // Detiene musica
        soundPlayer.playLoop("/Sonidos/musicaMenu.wav");
    }
    

    private void centerText(Graphics g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}