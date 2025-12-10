package Data;
import java.awt.Color;

public class TargetBrick extends Item {
    public TargetBrick(int x, int y, int size) { super(x, y, size); }
    
    @Override
    public Color getColor() { return Color.GREEN; } // Verde para reparar
}