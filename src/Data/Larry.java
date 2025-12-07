package data;

public class Larry extends GameCharacter {

    public Larry(int x, int y, int size) {
        super(x, y, size, 2.0); // Velocidad inicial 2.0
    }

    @Override
    public void updateBehavior() {
        // Aquí podrías poner animaciones específicas de Larry en el futuro
        // O lógica exclusiva de él (como invulnerabilidad temporal)
    }
}