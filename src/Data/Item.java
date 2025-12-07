package data;
import java.awt.Color;

public abstract class Item extends GameObject {
    public Item(int x, int y, int size) {
        super(x, y, size);
    }
    // Cada ítem define su color para pintarse diferente
    public abstract Color getColor();
}
