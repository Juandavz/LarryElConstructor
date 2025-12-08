package ui;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

import logic.GameLogic;
import data.Wall;
import data.Item; 

public class GamePanel extends JPanel {

    private GameLogic game; 
    private Image background;
    private boolean isGameOver = false;

    public GamePanel() {
        // --- ARREGLO DE RESPONSIVIDAD ---
        this.setFocusable(true); // Permite recibir teclas
        this.requestFocusInWindow(); // Pide el control del teclado INMEDIATAMENTE
        
        background = new ImageIcon("Borde.png").getImage();
        game = new GameLogic(800, 600); 

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Ahora los inputs van directo a la memoria de intenciones
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP    -> game.getLarry().setDirectionUp();
                    case KeyEvent.VK_DOWN  -> game.getLarry().setDirectionDown();
                    case KeyEvent.VK_LEFT  -> game.getLarry().setDirectionLeft();
                    case KeyEvent.VK_RIGHT -> game.getLarry().setDirectionRight();
                }
            }
        });

        // Loop del juego
        Timer timer = new Timer(16, e -> {
            if (!isGameOver) {
                // Pasamos el tamaño actual para chequear bordes
                game.updateGame(getWidth(), getHeight());
                
                if (game.checkGameOver(getWidth(), getHeight())) {
                    isGameOver = true;
                }
            }
            repaint();
        });
        timer.start();
    }
    
    // Si el usuario hace clic en la ventana, aseguramos que recupere el foco
    // (A veces pasa que al cambiar de ventana se pierde)
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Fondo
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // 2. Cuadrícula (Tenue)
        g.setColor(new Color(200, 200, 200, 50)); 
        for (int x = game.getMarginLeft(); x < getWidth() - game.getMarginLeft(); x += game.getTileSize()) {
            g.drawLine(x, game.getMarginTop(), x, getHeight() - game.getMarginTop());
        }
        for (int y = game.getMarginTop(); y < getHeight() - game.getMarginTop(); y += game.getTileSize()) {
            g.drawLine(game.getMarginLeft(), y, getWidth() - game.getMarginLeft(), y);
        }

        // 3. Ítem
        Item item = game.getCurrentItem(); 
        g.setColor(item.getColor()); 
        g.fillRect(item.getX(), item.getY(), item.getSize(), item.getSize());
        g.setColor(Color.BLACK);
        g.drawRect(item.getX(), item.getY(), item.getSize(), item.getSize());

        // 4. Muros
        for (Wall wall : game.getWalls()) {
            g.setColor(wall.getColor());
            g.fillRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
            g.setColor(Color.BLACK);
            g.drawRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
        }

        // 5. Larry
        g.setColor(Color.RED);
        g.fillRect(game.getLarry().getX(), game.getLarry().getY(), 
                   game.getLarry().getSize(), game.getLarry().getSize());
        
        // 6. Puntaje
        g.setColor(Color.BLACK); 
        g.drawString("Puntaje: " + game.getScore(), 112, 122); 
        g.setColor(Color.WHITE);
        g.drawString("Puntaje: " + game.getScore(), 110, 120);
        
        if(isGameOver) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", getWidth()/2 - 30, getHeight()/2);
        }
    }
}