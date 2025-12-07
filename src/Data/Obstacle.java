// data/Obstacle.java (Asegúrate de que se vea así)
package data;

public class Obstacle {
    private int x, y, size;

    public Obstacle(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    // Añade estos métodos para poder mover el obstáculo
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
