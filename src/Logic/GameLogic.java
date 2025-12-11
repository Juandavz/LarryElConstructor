package Logic;

import Data.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    protected SoundPlayer soundPlayer;

    protected Larry player1;
    protected Larry player2; 
    protected boolean isMultiplayer; 
    
    // --- ESTADO DE JUGADORES ---
    protected boolean p1Active = true;
    protected boolean p2Active = true;
    protected int scoreP1 = 0;
    protected int scoreP2 = 0;
    
    protected Item currentItem;
    protected ArrayList<Wall> walls;
    protected Random random = new Random();
    
    protected int winner = 0; 

    private int tickP1 = 0;
    private int tickP2 = 0;

    protected final int TILE_SIZE = 20;
    
    // Márgenes (100px)
    protected final int MARGIN_LEFT = 100;
    protected final int MARGIN_RIGHT = 100;
    protected final int MARGIN_TOP = 100;
    protected final int MARGIN_BOTTOM = 100;

    public GameLogic(int width, int height, boolean twoPlayers) {
        soundPlayer = new SoundPlayer();
        soundPlayer.playLoop("/Sonidos/Juego.wav");
        
        this.isMultiplayer = twoPlayers;
        this.scoreP1 = 0;
        this.scoreP2 = 0;
        
        int startY = snapToGrid(height / 2);
        
        // Posición inicial P1
        int startX1 = twoPlayers ? snapToGrid(MARGIN_LEFT + TILE_SIZE * 2) : snapToGrid(width / 2);
        player1 = new Larry(startX1, startY, TILE_SIZE, Color.RED);
        
        if (isMultiplayer) {
            // El Jugador 2 (Azul) empieza a la derecha mirando a la izquierda
            player2 = new Larry(snapToGrid(width - MARGIN_RIGHT - TILE_SIZE * 3), startY, TILE_SIZE, Color.BLUE);
            player2.setDirectionLeft();
            
            // CORRECCIÓN: El Jugador 1 (Rojo) empieza mirando a la derecha automáticamente
            player1.setDirectionRight();
        } else {
            player2 = null;
            p2Active = false;
            // En modo 1 Jugador no ponemos dirección automática para que empiece quieto 
            // (esto es útil para el Tutorial también)
        }

        walls = new ArrayList<>();
        spawnNewItem(width, height);
    }

    public void updateGame(int width, int height) {
        // --- MOVER P1 (Solo si está vivo) ---
        if (p1Active) {
            tickP1++;
            if (tickP1 >= player1.getMoveDelay()) {
                player1.moveGrid();
                tickP1 = 0;
            }
        }

        // --- MOVER P2 (Solo si existe y está vivo) ---
        if (isMultiplayer && player2 != null && p2Active) {
            tickP2++;
            if (tickP2 >= player2.getMoveDelay()) {
                player2.moveGrid();
                tickP2 = 0;
            }
        }
        
        for (Wall w : walls) w.update();

        // --- COLISIONES (Solo activos) ---
        if (p1Active) checkItemCollision(player1, width, height);
        if (isMultiplayer && p2Active) checkItemCollision(player2, width, height);
    }

    protected void checkItemCollision(Larry player, int w, int h) {
        if (player == null || currentItem == null) return;

        if (player.getBounds().intersects(currentItem.getBounds())) {
            
            int pointsToAdd = 0;
            if (currentItem instanceof TargetBrick) {
                soundPlayer.playOnce("/Sonidos/Coger.wav"); 
                walls.add(new Wall(currentItem.getX(), currentItem.getY(), currentItem.getSize()));
                player.increaseSpeed();
                pointsToAdd = 10;
            } 
            else if (currentItem instanceof Dynamite) {
                soundPlayer.playOnce("/Sonidos/Explosion.wav"); // <-- sonido de explosión
                calculateExplosion();
                pointsToAdd = 5;
            }
            
            if (player == player1) scoreP1 += pointsToAdd;
            else if (player == player2) scoreP2 += pointsToAdd;

            spawnNewItem(w, h);
        }
    }

    public boolean checkGameOver(int w, int h) {
        // Verificar muertes individuales
        if (p1Active && checkPlayerDead(player1, w, h)) {
            p1Active = false; 
        }
        if (isMultiplayer && p2Active && checkPlayerDead(player2, w, h)) {
            p2Active = false; 
        }

        // --- CONDICIONES DE FIN ---
        
        if (!isMultiplayer) {
            // MODO 1 JUGADOR
            if (!p1Active) {
                triggerGameOverSound();
                winner = 3; 
                return true;
            }
        } else {
            // MODO 2 JUGADORES (Acaba cuando ambos mueren)
            if (!p1Active && !p2Active) {
                triggerGameOverSound();
                
                // Ganador por Puntos
                if (scoreP1 > scoreP2) winner = 1; 
                else if (scoreP2 > scoreP1) winner = 2; 
                else winner = 3; 
                
                return true;
            }
        }
        return false;
    }
    
    private void triggerGameOverSound() {
        stopSound();
        soundPlayer.playOnce("/Sonidos/gameOver.wav");
    }
    
    public void stopSound() {
        if(soundPlayer != null) soundPlayer.stop();
    }

    protected boolean checkPlayerDead(Larry p, int w, int h) {
        if (p == null) return false;
        
        if (p.getX() < MARGIN_LEFT || p.getX() > w - MARGIN_RIGHT - TILE_SIZE ||
            p.getY() < MARGIN_TOP || p.getY() > h - MARGIN_BOTTOM - TILE_SIZE) {
            return true;
        }
        for (Wall wall : walls) {
            if (wall.isSolid() && p.getBounds().intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }

    protected void spawnNewItem(int w, int h) {
        int playableW = w - MARGIN_LEFT - MARGIN_RIGHT;
        int playableH = h - MARGIN_TOP - MARGIN_BOTTOM;
        if (playableW <= 0) return;
        
        int cols = playableW / TILE_SIZE;
        int rows = playableH / TILE_SIZE;
        int x, y;
        boolean occupied;
        int attempts = 0;
        
        do {
            occupied = false;
            x = MARGIN_LEFT + (random.nextInt(cols) * TILE_SIZE);
            y = MARGIN_TOP + (random.nextInt(rows) * TILE_SIZE);

            if (p1Active && x == player1.getX() && y == player1.getY()) occupied = true;
            if (isMultiplayer && p2Active && player2 != null) {
                if (x == player2.getX() && y == player2.getY()) occupied = true;
            }

            for (Wall wVal : walls) {
                if (x == wVal.getX() && y == wVal.getY()) { occupied = true; break; }
            }
            attempts++;
            if (attempts > 500) return; 
        } while (occupied);

        if (walls.size() >= 10 && random.nextInt(5) == 0) {
            currentItem = new Dynamite(x, y, TILE_SIZE);
        } else {
            currentItem = new TargetBrick(x, y, TILE_SIZE);
        }
    }
    
    protected void calculateExplosion() {
        if (walls.isEmpty()) return;
        int amount = 2 + random.nextInt(4);
        for (int i = 0; i < amount; i++) {
            if (walls.isEmpty()) break;
            walls.remove(random.nextInt(walls.size()));
        }
    }

    protected int snapToGrid(int value) { return (value / TILE_SIZE) * TILE_SIZE; }

    public int getTileSize() { return TILE_SIZE; }
    public int getMarginLeft() { return MARGIN_LEFT; }
    public int getMarginTop() { return MARGIN_TOP; }
    public Item getCurrentItem() { return currentItem; }
    public ArrayList<Wall> getWalls() { return walls; }
    
    public Larry getPlayer1() { return player1; }
    public Larry getPlayer2() { return player2; }
    public boolean isP1Active() { return p1Active; } 
    public boolean isP2Active() { return p2Active; }
    public boolean isMultiplayer() { return isMultiplayer; }
    public int getWinner() { return winner; }
    
    public int getScoreP1() { return scoreP1; }
    public int getScoreP2() { return scoreP2; }
    
    // Método legacy para compatibilidad si algo lo llama
    public int getScore() { return scoreP1; } 
}