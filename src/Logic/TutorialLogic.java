package Logic;

import Data.*;

public class TutorialLogic extends GameLogic {

    public TutorialLogic(int width, int height) {
        super(width, height, false); // 1 Jugador
        walls.clear();      
        currentItem = null; 
        this.scoreP1 = 0; // Reiniciar puntaje tutorial
    }

    @Override
    protected void spawnNewItem(int w, int h) {
        this.currentItem = null; // Item recogido -> desaparece
    }

    // El Tutorial usa la lógica de muerte normal de GameLogic (NO GodMode)
    // Así el usuario aprende que morir es malo.

    public void forceSpawnBrick(int x, int y) {
        int gridX = snapToGrid(x);
        int gridY = snapToGrid(y);
        this.currentItem = new TargetBrick(gridX, gridY, getTileSize());
    }
    
    public void forceSpawnDynamite(int x, int y) {
        int gridX = snapToGrid(x);
        int gridY = snapToGrid(y);
        this.currentItem = new Dynamite(gridX, gridY, getTileSize());
    }
    
    public boolean hasItem() {
        return currentItem != null;
    }
}