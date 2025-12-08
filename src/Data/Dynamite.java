package data;
import java.awt.Color;

public class Dynamite extends Item {
    public Dynamite(int x, int y, int size) { super(x, y, size); }
    
    @Override
    public Color getColor() { return Color.ORANGE; } // Ahora es Amarilla
}