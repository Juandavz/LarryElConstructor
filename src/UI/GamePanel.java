package ui;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList; 
import java.util.Random; 
import javax.swing.ImageIcon;

import data.Square;
import data.Food;
import data.Obstacle;
import logic.SquareLogic;
import logic.FoodLogic;

public class GamePanel extends JPanel {

    private Square square;
    private SquareLogic logic;
    private ArrayList<Obstacle> obstaculosList = new ArrayList<>(); 

    private Food food;
    private FoodLogic foodLogic;
    
    private Image background;
    private Random random = new Random();

    public GamePanel() {
        setFocusable(true);

        background = new ImageIcon("Borde.png").getImage();

        square = new Square(200, 200, 20);
        logic = new SquareLogic(square);

        food = new Food(0, 0, 20);
        foodLogic = new FoodLogic(food);
        
        foodLogic.randomizePosition(500, 400); 

        // GAME LOOP
        Timer timer = new Timer(16, e -> {
            logic.updatePosition();
            logic.handleBorderCollision(getWidth(), getHeight());

            // --- Lógica de Colisión (AQUÍ ESTÁ LA MAGIA) ---
            if (foodLogic.checkCollision(square.getX(), square.getY(), square.getSize())) {
                
                // 1. Crear un NUEVO obstáculo en la posición exacta de la comida actual
                int obsX = food.getX();
                int obsY = food.getY();
                int obsSize = food.getSize();
                Obstacle nuevoObstaculo = new Obstacle(obsX, obsY, obsSize);
                
                // 2. Añadirlo a la lista (para que permanezca)
                obstaculosList.add(nuevoObstaculo);
                
                // 3. Mover la comida a una NUEVA posición aleatoria
                foodLogic.randomizePosition(getWidth(), getHeight());

                // 4. 🌟 Aumentar la velocidad del cuadrado 🌟
                logic.increaseSpeed(0.02); 
            }

            repaint();
        });

        timer.start();

        // TECLAS
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP    -> logic.moveUp();
                    case KeyEvent.VK_DOWN  -> logic.moveDown();
                    case KeyEvent.VK_LEFT  -> logic.moveLeft();
                    case KeyEvent.VK_RIGHT -> logic.moveRight();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // FONDO
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // COMIDA (verde)
        g.setColor(Color.GREEN);
        g.fillRect(food.getX(), food.getY(), food.getSize(), food.getSize());

        // CUADRADO (rojo)
        g.setColor(Color.RED);
        g.fillRect(square.getX(), square.getY(), square.getSize(), square.getSize());
        
        // DIBUJAR TODOS LOS OBSTÁCULOS ALMACENADOS (negro)
        g.setColor(Color.BLACK); 
        for (Obstacle obs : obstaculosList) {
            g.fillRect(obs.getX(), obs.getY(), obs.getSize(), obs.getSize());
        }
    }
}
