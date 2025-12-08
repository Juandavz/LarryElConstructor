package logic;

import data.*;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {

    private Larry larry;
    private Item currentItem;
    private ArrayList<Wall> walls;
    private Random random = new Random();
    private int score = 0;
    
    // Control de tiempo para movimiento "Snake"
    private int tickCounter = 0;

    // Constantes
    private final int TILE_SIZE = 20;
    private final int MARGIN_LEFT = 100;
    private final int MARGIN_RIGHT = 100;
    private final int MARGIN_TOP = 100;
    private final int MARGIN_BOTTOM = 100;

    public GameLogic(int width, int height) {
        int startX = snapToGrid(width / 2);
        int startY = snapToGrid(height / 2);
        
        larry = new Larry(startX, startY, TILE_SIZE);
        walls = new ArrayList<>();
        spawnNewItem(width, height);
    }

    public void updateGame(int width, int height) {
        // --- 1. LÓGICA DE MOVIMIENTO DISCRETO (TIPO SNAKE) ---
        tickCounter++;
        if (tickCounter >= larry.getMoveDelay()) {
            larry.moveGrid(); // Mueve una casilla completa
            tickCounter = 0;  // Reinicia contador
        }
        
        // Actualizar muros (secado)
        for (Wall w : walls) w.update();

        // --- 2. COLISIÓN CON ÍTEM ---
        if (larry.getBounds().intersects(currentItem.getBounds())) {
            
            if (currentItem instanceof TargetBrick) {
                walls.add(new Wall(currentItem.getX(), currentItem.getY(), currentItem.getSize()));
                larry.increaseSpeed(); // Hace que el delay sea menor (más rápido)
                score++;
            } 
            else if (currentItem instanceof Dynamite) {
                calculateExplosion();
            }

            spawnNewItem(width, height);
        }
    }

    // Lógica de Explosión con porcentajes exactos
    private void calculateExplosion() {
        if (walls.isEmpty()) return;

        int rand = random.nextInt(100); // 0 a 99
        int wallsDestruir;

        if (rand < 30)       wallsDestruir = 2; // 0-29 (30%)
        else if (rand < 60)  wallsDestruir = 3; // 30-59 (30%)
        else if (rand < 85)  wallsDestruir = 4; // 60-84 (25%)
        else                 wallsDestruir = 5; // 85-99 (15%)

        destroyRandomWalls(wallsDestruir);
    }

    private void destroyRandomWalls(int amount) {
        for (int i = 0; i < amount; i++) {
            if (walls.isEmpty()) break;
            walls.remove(random.nextInt(walls.size()));
        }
    }

    private void spawnNewItem(int w, int h) {
        // Calcular posición en cuadrícula
        int playableWidth = w - MARGIN_LEFT - MARGIN_RIGHT;
        int playableHeight = h - MARGIN_TOP - MARGIN_BOTTOM;
        int cols = playableWidth / TILE_SIZE;
        int rows = playableHeight / TILE_SIZE;

        int x = MARGIN_LEFT + (random.nextInt(cols) * TILE_SIZE);
        int y = MARGIN_TOP + (random.nextInt(rows) * TILE_SIZE);

        // --- LÓGICA DE APARICIÓN (PROBABILIDAD 1/5) ---
        // Condición: Mínimo 10 muros Y el dado cae en 0 (1 entre 5 posibilidades: 0,1,2,3,4)
        if (walls.size() >= 10 && random.nextInt(5) == 0) {
            currentItem = new Dynamite(x, y, TILE_SIZE);
        } else {
            currentItem = new TargetBrick(x, y, TILE_SIZE);
        }
    }

    // Verifica Game Over (Bordes o Muros Sólidos)
    public boolean checkGameOver(int w, int h) {
        // 1. Colisión con Bordes (Ahora mata)
        if (larry.getX() < MARGIN_LEFT || 
            larry.getX() > w - MARGIN_RIGHT - TILE_SIZE ||
            larry.getY() < MARGIN_TOP || 
            larry.getY() > h - MARGIN_BOTTOM - TILE_SIZE) {
            return true;
        }

        // 2. Colisión con Muros Sólidos
        for (Wall wall : walls) {
            if (wall.isSolid() && larry.getBounds().intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private int snapToGrid(int value) {
        return (value / TILE_SIZE) * TILE_SIZE;
    }

    // Getters
    public int getTileSize() { return TILE_SIZE; }
    public int getMarginLeft() { return MARGIN_LEFT; }
    public int getMarginTop() { return MARGIN_TOP; }
    public Item getCurrentItem() { return currentItem; }
    public Larry getLarry() { return larry; }
    public ArrayList<Wall> getWalls() { return walls; }
    public int getScore() { return score; }
}