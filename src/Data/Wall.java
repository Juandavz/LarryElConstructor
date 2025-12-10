package Data;

import java.awt.Color;

public class Wall extends GameObject {

    private boolean isSolid = false; // Comienza siendo inofensivo
    private int solidificationTimer = 0; // Contador de tiempo
    private final int TIME_TO_SOLIDIFY = 60; // Ciclos para solidificar (aprox 1 segundo a 60FPS)

    public Wall(int x, int y, int size) {
        super(x, y, size);
    }

    // Este método se llamará en cada frame del juego
    public void update() {
        if (!isSolid) {
            solidificationTimer++;
            if (solidificationTimer >= TIME_TO_SOLIDIFY) {
                isSolid = true; // ¡Ahora ya mata!
            }
        }
    }

    public boolean isSolid() {
        return isSolid;
    }

    // Color: Gris oscuro si es sólido, Gris claro si se está "secando"
    public Color getColor() {
        return isSolid ? Color.DARK_GRAY : new Color(200, 200, 200, 150);
    }
}