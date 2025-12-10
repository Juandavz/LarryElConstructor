package Data;

import java.awt.Color;

public class Larry extends GameCharacter {
    
    private Color color; // Nuevo atributo de identidad

    public Larry(int x, int y, int size, Color color) {
        // 10 es el delay inicial (velocidad base)
        super(x, y, size, 10); 
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void updateBehavior() {
        // Lógica futura si es necesaria
    }
}