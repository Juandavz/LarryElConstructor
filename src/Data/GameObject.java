package data;

import java.awt.Rectangle; // <--- IMPORTANTE: Necesario para getBounds

public abstract class GameObject {
    protected int x, y, size;

    public GameObject(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getSize() { return size; }
    
    // Método helper para colisiones
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}