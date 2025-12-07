package data;

// Al estar en el mismo paquete "data", no necesita importar GameObject.
// Si te da error aquí, es porque GameObject.java no tiene "package data;" al inicio.

public class Wall extends GameObject {

    public Wall(int x, int y, int size) {
        super(x, y, size); // Llama al constructor del padre
    }
}