package logic;

import data.Square;

public class SquareLogic {

    private Square square;
    // dx y dy siguen siendo int porque las coordenadas del cuadrado probablemente son int
    private int dx = 0, dy = 0; 
    // Velocidad ahora es un double
    private double speed = 2.0; // Usa 2.0 para double, o el valor que desees, ej: 2.5

    public SquareLogic(Square square) {
        this.square = square;
    }

    public void updatePosition() {
        // Esto funciona porque dx/dy ya son ints
        square.setX(square.getX() + dx);
        square.setY(square.getY() + dy);
    }

    // Usamos (int) para convertir el double speed a un entero para dx/dy
    public void moveUp()    { dx = 0; dy = (int)-speed; }
    public void moveDown()  { dx = 0; dy = (int)speed; }
    public void moveLeft()  { dx = (int)-speed; dy = 0; }
    public void moveRight() { dx = (int)speed; dy = 0; }

    public void handleBorderCollision(int w, int h) {
        if (square.getX() < 95 ||
            square.getY() < 95 ||
            square.getX() + square.getSize() > w-95 ||
            square.getY() + square.getSize() > h-95) {

            square.setX(w / 2);
            square.setY(h / 2);
        }
    }

    // 🌟 MÉTODO PARA AUMENTAR LA VELOCIDAD (incremento puede ser double o int) 🌟
    public void increaseSpeed(double increment) {
        this.speed += increment; // Incrementamos el double de a poco (ej: 0.5)
        
        // Reajustar dx/dy inmediatamente usando CASTING a INT
        if (dx != 0) {
            // Asigna el valor INT de la velocidad, manteniendo la dirección (signo)
            dx = (dx > 0) ? (int)speed : (int)-speed;
        }
        if (dy != 0) {
             // Asigna el valor INT de la velocidad, manteniendo la dirección (signo)
            dy = (dy > 0) ? (int)speed : (int)-speed;
        }
        System.out.println("Nueva velocidad (double): " + this.speed + " (actual int move: " + dx + dy + ")");
    }
}
