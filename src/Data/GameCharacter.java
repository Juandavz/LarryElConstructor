package data;

public abstract class GameCharacter extends GameObject {
    
    protected double speed;
    protected int dx, dy;

    public GameCharacter(int x, int y, int size, double speed) {
        super(x, y, size);
        this.speed = speed;
    }

    // Lógica compartida: Tanto Larry como los enemigos se mueven igual
    public void move() {
        this.x += dx;
        this.y += dy;
    }

    public void stop() {
        dx = 0; 
        dy = 0;
    }

    // Métodos abstractos: Obligamos a que Larry y Enemigo definan cómo se comportan
    // Por ejemplo, Larry se mueve con teclas, el Enemigo con IA.
    public abstract void updateBehavior(); 

    // Setters de dirección para usar desde el teclado o IA
    public void setDirectionUp()    { dx = 0; dy = (int)-speed; }
    public void setDirectionDown()  { dx = 0; dy = (int)speed; }
    public void setDirectionLeft()  { dx = (int)-speed; dy = 0; }
    public void setDirectionRight() { dx = (int)speed; dy = 0; }

    public void increaseSpeed(double increment) {
        this.speed += increment;
        // Actualizar vector actual si se está moviendo
        if (dx != 0) dx = (dx > 0) ? (int)speed : (int)-speed;
        if (dy != 0) dy = (dy > 0) ? (int)speed : (int)-speed;
    }
}