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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GamePanel extends JPanel {
    
    private SoundPlayer soundPlayer = new SoundPlayer();

    // NUEVO ESTADO: INSTRUCTIONS
    private enum State { MENU, INSTRUCTIONS, PLAYING, TUTORIAL, GAME_OVER }
    private State currentState = State.MENU;

    private GameLogic game; 
    private Image background, imgP1, imgP2, Fondo;
    private Font pressStartFont;
    private boolean isMode2Players = false;

    // Tutorial Vars
    private int tutorialStep = 0;
    private String tutorialMessage = "";
    private int startPlayerX, startPlayerY; 
    private int tutorialTimer = 0; 

    // High Score
    private int highScore = 0; 
    private final String SCORE_FILE = "highscore.dat";

    public GamePanel() {
        loadHighScore(); 
        enterMenu();     

        try {
            pressStartFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/PressStart2P.ttf")).deriveFont(32f); 
        } catch (Exception e) {
            System.out.println("Error fuente.");
            pressStartFont = new Font("Monospaced", Font.BOLD, 32);
        }
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        Fondo = new ImageIcon("Fondo.png").getImage();
        imgP1 = new ImageIcon("P1.png").getImage();
        imgP2 = new ImageIcon("P2.png").getImage();
        background = new ImageIcon("Borde.png").getImage();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // 1. MENÚ
                if (currentState == State.MENU) {
                    if (key == KeyEvent.VK_1) {
                        soundPlayer.stop(); 
                        startGame(false);
                    } else if (key == KeyEvent.VK_2) {
                        // Ir a instrucciones antes de jugar
                        currentState = State.INSTRUCTIONS;
                        repaint();
                    } else if (key == KeyEvent.VK_T) {
                        soundPlayer.stop();
                        startTutorial(true); 
                    }
                }
                
                // 2. INSTRUCCIONES 2P
                else if (currentState == State.INSTRUCTIONS) {
                    if (key == KeyEvent.VK_ENTER) {
                        soundPlayer.stop();
                        startGame(true); // Iniciar juego 2P
                    }
                }
                
                // 3. JUGANDO
                else if (currentState == State.PLAYING || currentState == State.TUTORIAL) {
                    handleMovementKeys(key);
                }
                
                // 4. GAME OVER
                else if (currentState == State.GAME_OVER) {
                    if (key == KeyEvent.VK_R) {
                        if (game instanceof Logic.TutorialLogic) startTutorial(false); 
                        else startGame(isMode2Players);
                    } else if (key == KeyEvent.VK_M) {
                        enterMenu(); 
                        repaint();
                    }
                }
            }
        });

        Timer timer = new Timer(16, e -> {
            if (currentState == State.PLAYING || currentState == State.TUTORIAL) {
                game.updateGame(getWidth(), getHeight());
                
                // High Score (Solo Single Player)
                if (!isMode2Players && !(game instanceof Logic.TutorialLogic)) {
                    if (game.getScoreP1() > highScore) {
                        highScore = game.getScoreP1();
                        saveHighScore();
                    }
                }
                
                if (currentState == State.TUTORIAL) checkTutorialAutoAdvance();

                if (game.checkGameOver(getWidth(), getHeight())) {
                    currentState = State.GAME_OVER;
                }
            }
            repaint();
        });
        timer.start();
    }
    
    private void loadHighScore() {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(SCORE_FILE))) {
            highScore = dis.readInt();
        } catch (IOException e) { highScore = 0; }
    }

    private void saveHighScore() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(SCORE_FILE))) {
            dos.writeInt(highScore);
        } catch (IOException e) { }
    }

    private void handleMovementKeys(int key) {
        if (game == null) return;
        // P1 (Solo si está vivo)
        if (game.isP1Active() && game.getPlayer1() != null) {
            if (key == KeyEvent.VK_UP) game.getPlayer1().setDirectionUp();
            if (key == KeyEvent.VK_DOWN) game.getPlayer1().setDirectionDown();
            if (key == KeyEvent.VK_LEFT) game.getPlayer1().setDirectionLeft();
            if (key == KeyEvent.VK_RIGHT) game.getPlayer1().setDirectionRight();
        }
        // P2 (Solo si está vivo)
        if (isMode2Players && game.isP2Active() && game.getPlayer2() != null) {
            if (key == KeyEvent.VK_W) game.getPlayer2().setDirectionUp();
            if (key == KeyEvent.VK_S) game.getPlayer2().setDirectionDown();
            if (key == KeyEvent.VK_A) game.getPlayer2().setDirectionLeft();
            if (key == KeyEvent.VK_D) game.getPlayer2().setDirectionRight();
        }
    }

    private void startGame(boolean twoPlayers) {
        this.isMode2Players = twoPlayers;
        game = new GameLogic(getWidth(), getHeight(), twoPlayers);
        currentState = State.PLAYING;
    }

    private void startTutorial(boolean resetStep) {
        this.isMode2Players = false;
        game = new Logic.TutorialLogic(getWidth(), getHeight());
        currentState = State.TUTORIAL;
        if (resetStep) tutorialStep = 0;
        startPlayerX = game.getPlayer1().getX();
        startPlayerY = game.getPlayer1().getY();
        updateTutorialScript();
    }

    private void updateTutorialScript() {
        if (!(game instanceof Logic.TutorialLogic)) return;
        Logic.TutorialLogic tutorial = (Logic.TutorialLogic) game;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        tutorialTimer = 0;

        switch (tutorialStep) {
            case 0: tutorialMessage = "BIENVENIDO. (Espera...)"; break;
            case 1: tutorialMessage = "Usa las FLECHAS para moverte."; break;
            case 2: 
                tutorialMessage = "Objetivo: Recoge el ladrillo VERDE."; 
                if (!tutorial.hasItem()) tutorial.forceSpawnBrick(cx + 100, cy);
                break;
            case 3: tutorialMessage = "¡Bien! Dejaste un muro y ganaste PUNTOS."; break;
            case 4: 
                tutorialMessage = "Recoge la DINAMITA (Naranja)."; 
                if (!tutorial.hasItem()) tutorial.forceSpawnDynamite(cx - 100, cy);
                break;
            case 5: tutorialMessage = "¡Boom! La dinamita limpia muros."; break;
            case 6: tutorialMessage = "CUIDADO: Los bordes y muros grises TE MATAN."; break;
            case 7: tutorialMessage = "¡TUTORIAL COMPLETADO!"; break;
        }
    }

    private void checkTutorialAutoAdvance() {
        tutorialTimer++;
        switch (tutorialStep) {
            case 0: if (tutorialTimer > 120) nextTutorialStep(); break;
            case 1: if (game.getPlayer1().getX() != startPlayerX || game.getPlayer1().getY() != startPlayerY) nextTutorialStep(); break;
            case 2: if (game.getCurrentItem() == null) nextTutorialStep(); break;
            case 3: if (tutorialTimer > 180) nextTutorialStep(); break;
            case 4: if (game.getCurrentItem() == null) nextTutorialStep(); break;
            case 5: if (tutorialTimer > 180) nextTutorialStep(); break;
            case 6: if (tutorialTimer > 240) nextTutorialStep(); break;
            case 7: 
                if (tutorialTimer > 180) { 
                    if(game != null) game.stopSound(); 
                    enterMenu(); 
                    repaint(); 
                } 
                break;
        }
    }
    
    private void nextTutorialStep() {
        tutorialStep++;
        updateTutorialScript();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentState == State.MENU) {
            drawMenuScreen(g);
            return;
        }
        
        // DIBUJAR PANTALLA DE INSTRUCCIONES 2P
        if (currentState == State.INSTRUCTIONS) {
            drawInstructionsScreen(g);
            return;
        }

        if (game == null) return; 

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // Grid
        g.setColor(new Color(200, 200, 200, 50)); 
        for (int x = game.getMarginLeft(); x < getWidth() - game.getMarginLeft(); x += game.getTileSize()) {
            g.drawLine(x, game.getMarginTop(), x, getHeight() - game.getMarginTop());
        }
        for (int y = game.getMarginTop(); y < getHeight() - game.getMarginTop(); y += game.getTileSize()) {
            g.drawLine(game.getMarginLeft(), y, getWidth() - game.getMarginLeft(), y);
        }

        // Items
        Item item = game.getCurrentItem(); 
        if (item != null) {
            g.setColor(item.getColor()); 
            g.fillRect(item.getX(), item.getY(), item.getSize(), item.getSize());
            g.setColor(Color.BLACK);
            g.drawRect(item.getX(), item.getY(), item.getSize(), item.getSize());
        }
        // Walls
        for (Wall wall : game.getWalls()) {
            g.setColor(wall.getColor());
            g.fillRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
            g.setColor(Color.BLACK);
            g.drawRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
        }

        // Jugadores (SOLO SI ESTÁN VIVOS)
        if (game.isP1Active() && game.getPlayer1() != null) {
            g.setColor(game.getPlayer1().getColor());
            g.fillRect(game.getPlayer1().getX(), game.getPlayer1().getY(), 
            game.getPlayer1().getSize(), game.getPlayer1().getSize());
        }
        if (isMode2Players && game.isP2Active() && game.getPlayer2() != null) {
            g.setColor(game.getPlayer2().getColor()); 
            g.fillRect(game.getPlayer2().getX(), game.getPlayer2().getY(), 
                    game.getPlayer2().getSize(), game.getPlayer2().getSize());
        }

        // HUD SUPERIOR
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), 90); 

        g.setFont(pressStartFont.deriveFont(20f));
        
        if (isMode2Players) {
            // MODO VERSUS
            g.setColor(Color.RED); 
            g.drawString("P1: " + game.getScoreP1(), 50, 55);
            
            g.setColor(Color.WHITE); 
            g.drawString("VS", getWidth()/2 - 20, 55);
            
            g.setColor(Color.BLUE); 
            g.drawString("P2: " + game.getScoreP2(), getWidth() - 200, 55);
            
            // Subtitulo objetivo
            g.setColor(Color.YELLOW);
            g.setFont(pressStartFont.deriveFont(12f));
            centerText(g, "¡GANA QUIEN TENGA MÁS PUNTOS!", 80);
        } else {
            // MODO SOLO
            g.setColor(Color.WHITE); g.drawString("SCORE", 50, 40);
            g.setColor(Color.YELLOW);
            g.drawString(String.format("%06d", game.getScoreP1()), 50, 70);

            if (!(game instanceof Logic.TutorialLogic)) {
                g.setColor(Color.WHITE); g.drawString("HIGH SCORE", 500, 40);
                g.setColor(Color.RED);
                g.drawString(String.format("%06d", highScore), 500, 70);
            } else {
                g.setColor(Color.CYAN); g.drawString("MODO TUTORIAL", 450, 60);
            }
        }

        // Tutorial Overlay
        if (currentState == State.TUTORIAL) {
            g.setColor(new Color(0, 0, 0, 180)); 
            g.fillRect(0, getHeight() - 100, getWidth(), 100);
            
            g.setColor(Color.DARK_GRAY); g.fillRect(0, getHeight() - 10, getWidth(), 10);
            g.setColor(Color.GREEN);
            int progressWidth = (int)((tutorialStep / 7.0) * getWidth());
            g.fillRect(0, getHeight() - 10, progressWidth, 10);

            g.setColor(Color.CYAN);
            g.setFont(pressStartFont.deriveFont(12f));
            centerText(g, "LECCIÓN " + (tutorialStep + 1) + "/8", getHeight() - 70);
            g.setColor(Color.WHITE);
            g.setFont(pressStartFont.deriveFont(14f));
            centerText(g, tutorialMessage, getHeight() - 40);
        }

        if (currentState == State.GAME_OVER) {
            drawGameOverScreen(g);
        }
    }

    private void drawMenuScreen(Graphics g) {
        g.drawImage(Fondo, 0, 0, getWidth(), getHeight(), this);
        int imgW = 180; int imgH = 180;
        g.drawImage(imgP1, (int)(getWidth()*0.2)-imgW/2, getHeight()/2-imgH, imgW, imgH, this);
        g.drawImage(imgP2, (int)(getWidth()*0.8)-imgW/2, getHeight()/2-imgH, imgW, imgH, this);
        
        g.setColor(Color.ORANGE);
        g.setFont(pressStartFont.deriveFont(40f));
        centerText(g, "Block City", getHeight() / 4);

        g.setColor(Color.WHITE);
        g.setFont(pressStartFont.deriveFont(18f));
        centerText(g, "Presiona '1' para Un Jugador", getHeight() / 2+50);
        centerText(g, "Presiona '2' para Dos Jugadores", getHeight() / 2 + 90);
        g.setColor(Color.CYAN);
        centerText(g, "Presiona 'T' para Tutorial", getHeight() / 2 + 130);
        
        g.setColor(Color.YELLOW);
        g.setFont(pressStartFont.deriveFont(14f));
        centerText(g, "HIGH SCORE: " + String.format("%06d", highScore), getHeight() - 80);
    }
    
    // NUEVA PANTALLA: INSTRUCCIONES 2P
    private void drawInstructionsScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setFont(pressStartFont.deriveFont(30f));
        g.setColor(Color.ORANGE);
        centerText(g, "MODO VERSUS", 150);
        
        g.setFont(pressStartFont.deriveFont(16f));
        g.setColor(Color.WHITE);
        centerText(g, "El objetivo es conseguir más puntos.", 220);
        centerText(g, "Si chocas, ¡quedan tus puntos pero mueres!", 250);
        centerText(g, "El juego acaba cuando ambos caen.", 280);
        
        // Controles P1
        g.setColor(Color.RED);
        g.drawString("JUGADOR 1 (ROJO)", 100, 350);
        g.setColor(Color.WHITE);
        g.drawString("Mover: FLECHAS", 100, 380);
        
        // Controles P2
        g.setColor(Color.BLUE);
        g.drawString("JUGADOR 2 (AZUL)", 450, 350);
        g.setColor(Color.WHITE);
        g.drawString("Mover: W A S D", 450, 380);
        
        g.setColor(Color.GREEN);
        centerText(g, "PRESIONA [ENTER] PARA COMENZAR", getHeight() - 100);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        Font titleFont = pressStartFont.deriveFont(50f);
        g.setFont(titleFont);
        Color strongRed = new Color(102, 11, 25);
        Font subFont = pressStartFont.deriveFont(18f);
        
        String title = "GAME OVER";
        Color titleColor = Color.WHITE;
        String subtitle = "";

        if (isMode2Players) {
            // Mostrar puntajes finales
            subtitle = "P1: " + game.getScoreP1() + "  -  P2: " + game.getScoreP2();
            
            switch (game.getWinner()) {
                case 1 -> { title = "¡GANA ROJO!"; titleColor = Color.RED; }
                case 2 -> { title = "¡GANA AZUL!"; titleColor = Color.BLUE; }
                default -> { title = "¡EMPATE!"; }
            }
        } 

        g.setFont(titleFont);
        g.setColor(strongRed);
        centerText(g, title, getHeight() / 2 - 16);
        g.setColor(titleColor);
        centerText(g, title, getHeight() / 2 - 20);
        
        if (!subtitle.isEmpty()) {
            g.setFont(subFont);
            g.setColor(Color.CYAN);
            centerText(g, subtitle, getHeight() / 2 + 30);
        }
        
        g.setFont(subFont);
        g.setColor(Color.WHITE); 
        centerText(g, "Presiona 'R' para Reiniciar", getHeight() / 2 + 80);
        g.setColor(Color.YELLOW);
        centerText(g, "Presiona 'M' para ir al Menú", getHeight() / 2 + 110);
    }
    
    private void enterMenu() {
        currentState = State.MENU;
        soundPlayer.stop(); 
        soundPlayer.playLoop("/Sonidos/musicaMenu.wav");
    }

    private void centerText(Graphics g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}