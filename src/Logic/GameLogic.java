package logic;

import data.*; // Importa Larry, Wall, Item, TargetBrick, Dynamite
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {

    private Larry larry;
    private Item currentItem; // El ítem actual (Ladrillo o Dinamita)
    private ArrayList<Wall> walls;
    private Random random = new Random();
    private int score = 0;

    public GameLogic(int width, int height) {
        larry = new Larry(200, 200, 20);
        walls = new ArrayList<>();
        spawnNewItem(width, height);
    }

    public void updateGame(int width, int height) {
        larry.move();
        handleBorderCollision(width, height);

        // LÓGICA DE COLISIÓN
        if (larry.getBounds().intersects(currentItem.getBounds())) {
            
            if (currentItem instanceof TargetBrick) {
                // Si es Ladrillo: Construir muro y subir dificultad
                walls.add(new Wall(currentItem.getX(), currentItem.getY(), currentItem.getSize()));
                larry.increaseSpeed(0.2); 
                score++;
            } 
            else if (currentItem instanceof Dynamite) {
                // Si es Dinamita: Romper muros
                destroyRandomWalls(3);
            }

            spawnNewItem(width, height);
        }
    }

    private void spawnNewItem(int w, int h) {
        int type = random.nextInt(10); 
        int margin = 50;
        int x = margin + random.nextInt(w - margin*2);
        int y = margin + random.nextInt(h - margin*2);

        if (type < 8) { 
            currentItem = new TargetBrick(x, y, 20);
        } else { 
            currentItem = new Dynamite(x, y, 20);
        }
    }

    private void destroyRandomWalls(int amount) {
        if (walls.isEmpty()) return;
        for (int i = 0; i < amount; i++) {
            if (walls.isEmpty()) break;
            walls.remove(random.nextInt(walls.size()));
        }
    }

    private void handleBorderCollision(int w, int h) {
       if (larry.getX() < 0 || larry.getX() > w - larry.getSize() ||
           larry.getY() < 0 || larry.getY() > h - larry.getSize()) {
           larry.setX(w/2); larry.setY(h/2);
       }
    }

    public boolean checkGameOver() {
       for (Wall w : walls) {
           if (larry.getBounds().intersects(w.getBounds())) return true;
       }
       return false;
    }

    // GETTERS (Aquí está el que te faltaba)
    public Item getCurrentItem() { return currentItem; } // <--- ¡AQUÍ ESTÁ!
    public Larry getLarry() { return larry; }
    public ArrayList<Wall> getWalls() { return walls; }
    public int getScore() { return score; }
}