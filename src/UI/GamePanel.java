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
// Importamos Item para poder usarlo al pintar
import data.Item; 

public class GamePanel extends JPanel {

    private GameLogic game; 
    private Image background;
    private boolean isGameOver = false;

    public GamePanel() {
        setFocusable(true);
        background = new ImageIcon("Borde.png").getImage();
        
        game = new GameLogic(800, 600);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP    -> game.getLarry().setDirectionUp();
                    case KeyEvent.VK_DOWN  -> game.getLarry().setDirectionDown();
                    case KeyEvent.VK_LEFT  -> game.getLarry().setDirectionLeft();
                    case KeyEvent.VK_RIGHT -> game.getLarry().setDirectionRight();
                }
            }
        });

        Timer timer = new Timer(16, e -> {
            if (!isGameOver) {
                game.updateGame(getWidth(), getHeight());
                if (game.checkGameOver()) {
                    isGameOver = true;
                    System.out.println("GAME OVER");
                }
            }
            repaint();
        });
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Fondo
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // 2. DIBUJAR ÍTEM (Corrección del error 'target')
        // Obtenemos el ítem actual (sea Dinamita o Ladrillo)
        Item item = game.getCurrentItem(); 
        
        // Usamos el color que el propio ítem define (Polimorfismo: Verde o Naranja)
        g.setColor(item.getColor()); 
        g.fillRect(item.getX(), item.getY(), item.getSize(), item.getSize());

        // 3. Larry
        g.setColor(Color.RED);
        g.fillRect(game.getLarry().getX(), game.getLarry().getY(), 
                   game.getLarry().getSize(), game.getLarry().getSize());
        
        // 4. Muros
        g.setColor(Color.DARK_GRAY); 
        for (Wall wall : game.getWalls()) {
            g.fillRect(wall.getX(), wall.getY(), wall.getSize(), wall.getSize());
        }

        // 5. Puntaje
        g.setColor(Color.WHITE);
        g.drawString("Puntaje: " + game.getScore(), 20, 20);
        
        if(isGameOver) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", getWidth()/2 - 30, getHeight()/2);
        }
    }
}
