package Data;

public abstract class GameCharacter extends GameObject {
    
    // dx y dy ahora representan dirección (-1, 0, 1)
    protected int dx, dy;
    
    // moveDelay: "cuántos frames espera antes de moverse"
    // Menor número = Más rápido
    protected int moveDelay; 

    public GameCharacter(int x, int y, int size, int initialDelay) {
        super(x, y, size);
        this.moveDelay = initialDelay;
    }

    // Mueve al personaje UN bloque completo en la dirección actual
    public void moveGrid() {
        this.x += dx * size;
        this.y += dy * size;
    }

    public void stop() {
        dx = 0; 
        dy = 0;
    }

    public abstract void updateBehavior(); 

    // --- CAMBIO AQUÍ: Eliminamos las restricciones "if" ---
    // Ahora permite cambiar a cualquier dirección inmediatamente, incluso la opuesta.

    public void setDirectionUp() { 
        dx = 0; 
        dy = -1; 
    }

    public void setDirectionDown() { 
        dx = 0; 
        dy = 1; 
    }

    public void setDirectionLeft() { 
        dx = -1; 
        dy = 0; 
    }

    public void setDirectionRight() { 
        dx = 1; 
        dy = 0; 
    }

    // Aumentar velocidad reduciendo el tiempo de espera
    public void increaseSpeed() {
        if (moveDelay > 5) { // Límite para que no sea injugable
            moveDelay--; 
        }
    }
    
    public int getMoveDelay() { return moveDelay; }
}