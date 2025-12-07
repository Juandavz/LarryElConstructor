package logic;

import data.Square;
import data.Food;
import data.Obstacle;

import java.util.ArrayList;

public class GameController {

    private Square square;
    private Food food;
    private ArrayList<Obstacle> obstacles;

    private int dx = 0;
    private int dy = 0;

    public GameController(Square square, Food food) {
        this.square = square;
        this.food = food;
        this.obstacles = new ArrayList<>();
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update(int panelWidth, int panelHeight) {
        square.setX(square.getX() + dx);
        square.setY(square.getY() + dy);
    }

    // Colisión simple cuadrado-cuadrado
    public boolean collide(int x1, int y1, int s1, int x2, int y2, int s2) {
        return x1 < x2 + s2 && x1 + s1 > x2 &&
               y1 < y2 + s2 && y1 + s1 > y2;
    }

    public void checkFoodCollision(int panelWidth, int panelHeight) {
        if (collide(square.getX(), square.getY(), square.getSize(),
                    food.getX(), food.getY(), food.getSize())) {

            // 1. Convertir comida en obstáculo
            obstacles.add(new Obstacle(food.getX(), food.getY(), food.getSize()));

            
        }
    }

    public boolean checkObstacleCollision() {
        for (Obstacle o : obstacles) {
            if (collide(square.getX(), square.getY(), square.getSize(),
                        o.getX(), o.getY(), o.getSize())) {
                return true;
            }
        }
        return false;
    }

    public Square getSquare() { return square; }
    public Food getFood() { return food; }
    public ArrayList<Obstacle> getObstacles() { return obstacles; }
}
