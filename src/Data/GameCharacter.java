package data;

public abstract class GameCharacter extends GameObject {
    
    // dx y dy ahora representan dirección (-1, 0, 1), no velocidad en píxeles
    protected int dx, dy;
    
    // speed ahora representará "cuántos frames espera antes de moverse" (Delay)
    // Menor número = Más rápido
    protected int moveDelay; 

    public GameCharacter(int x, int y, int size, int initialDelay) {
        super(x, y, size);
        this.moveDelay = initialDelay;
    }

    // Este método mueve al personaje UN bloque completo en la dirección actual
    public void moveGrid() {
        this.x += dx * size;
        this.y += dy * size;
    }

    public void stop() {
        dx = 0; 
        dy = 0;
    }

    public abstract void updateBehavior(); 

    // Setters de dirección (Solo cambian la orientación, no mueven)
    // Evitamos girar 180 grados sobre sí mismo (ej: si va a la derecha, no puede ir a la izquierda)
    public void setDirectionUp()    { if(dy != 1) { dx = 0; dy = -1; } }
    public void setDirectionDown()  { if(dy != -1) { dx = 0; dy = 1; } }
    public void setDirectionLeft()  { if(dx != 1) { dx = -1; dy = 0; } }
    public void setDirectionRight() { if(dx != -1) { dx = 1; dy = 0; } }

    // Aumentar velocidad ahora significa reducir el tiempo de espera
    public void increaseSpeed() {
        if (moveDelay > 5) { // Límite para que no sea injugable
            moveDelay--; 
        }
    }
    
    public int getMoveDelay() { return moveDelay; }
}