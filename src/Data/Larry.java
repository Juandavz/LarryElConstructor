package data;

public class Larry extends GameCharacter {

    public Larry(int x, int y, int size) {
        // 10 es el delay inicial (se moverá cada 10 frames, aprox 6 veces por segundo)
        super(x, y, size, 10); 
    }

    @Override
    public void updateBehavior() {
        // Lógica futura
    }
}