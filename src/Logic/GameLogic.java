package Logic;

import Data.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    private SoundPlayer soundPlayer; //Musica


    private Larry player1;
    private Larry player2; // Puede ser null si es 1 jugador
    private boolean isMultiplayer; // Bandera para saber el modo
    
    private Item currentItem;
    private ArrayList<Wall> walls;
    private Random random = new Random();
    
    // 0 = Nadie/Jugando, 1 = Gana P1, 2 = Gana P2, 3 = Empate/Derrota Solo
    private int winner = 0; 

    private int tickP1 = 0;
    private int tickP2 = 0;

    // --- CONSTANTES DE DISEÑO ---
    private final int TILE_SIZE = 20;
    
    // Márgenes ajustados a 100 para coincidir con la imagen "Borde.png"
    private final int MARGIN_LEFT = 100;
    private final int MARGIN_RIGHT = 100;
    private final int MARGIN_TOP = 100;
    private final int MARGIN_BOTTOM = 100;
    

    // Constructor que recibe el modo de juego
    public GameLogic(int width, int height, boolean twoPlayers) {
        
        //musica para la pantalla de juego 
        soundPlayer = new SoundPlayer();
        soundPlayer.playLoop("/Sonidos/Juego.wav");
        this.isMultiplayer = twoPlayers;
        int startY = snapToGrid(height / 2);
        
        // El Jugador 1 siempre existe (Rojo)
        // En 1 jugador empieza en el centro, en 2 jugadores empieza a la izquierda (ajustado al margen)
        int startX1 = twoPlayers ? snapToGrid(MARGIN_LEFT + TILE_SIZE * 2) : snapToGrid(width / 2);
        player1 = new Larry(startX1, startY, TILE_SIZE, Color.RED);
        
        if (isMultiplayer) {
            // El Jugador 2 solo existe en modo multijugador (Azul)
            // Empieza a la derecha, respetando el margen derecho
            player2 = new Larry(snapToGrid(width - MARGIN_RIGHT - TILE_SIZE * 3), startY, TILE_SIZE, Color.BLUE);
            player2.setDirectionLeft();
        } else {
            player2 = null;
        }

        walls = new ArrayList<>();
        spawnNewItem(width, height);
    }

    public void updateGame(int width, int height) {
        // --- 1. MOVER JUGADOR 1 ---
        tickP1++;
        if (tickP1 >= player1.getMoveDelay()) {
            player1.moveGrid();
            tickP1 = 0;
        }

        // --- 2. MOVER JUGADOR 2 (Solo si existe) ---
        if (isMultiplayer && player2 != null) {
            tickP2++;
            if (tickP2 >= player2.getMoveDelay()) {
                player2.moveGrid();
                tickP2 = 0;
            }
        }
        
        // Actualizar muros (secado)
        for (Wall w : walls) w.update();

        // --- 3. COLISIONES CON ÍTEMS ---
        checkItemCollision(player1, width, height);
        if (isMultiplayer) {
            checkItemCollision(player2, width, height);
        }
    }

    private void checkItemCollision(Larry player, int w, int h) {
        if (player == null) return;

        if (player.getBounds().intersects(currentItem.getBounds())) {
            
            if (currentItem instanceof TargetBrick) {
                walls.add(new Wall(currentItem.getX(), currentItem.getY(), currentItem.getSize()));
                player.increaseSpeed(); 
            } 
            else if (currentItem instanceof Dynamite) {
                calculateExplosion();
            }
            spawnNewItem(w, h);
        }
    }

    public boolean checkGameOver(int w, int h) {
        boolean p1Dead = checkPlayerDead(player1, w, h);
        
        if (!isMultiplayer) {
            // Lógica para 1 Jugador (Solo importa si P1 muere)
            if (p1Dead) {
                soundPlayer.stop(); //detener musica
                soundPlayer.playOnce("/Sonidos/gameOver.wav");//sonido de muerte
                winner = 3; // Código de derrota estándar
                return true;
            }
            return false;
        } else {
            // Lógica para 2 Jugadores (Competitivo)
            boolean p2Dead = checkPlayerDead(player2, w, h);

            // Choque de cabezas
            if (player1.getX() == player2.getX() && player1.getY() == player2.getY()) {
                soundPlayer.stop();
                soundPlayer.playOnce("/Sonidos/gameOver.wav");
                winner = 3; // Empate
                return true;
            }

            if (p1Dead && p2Dead) {
                soundPlayer.stop();
                soundPlayer.playOnce("/Sonidos/gameOver.wav");
                winner = 3; // Empate
                return true;
            } else if (p1Dead) {
                soundPlayer.stop();
                soundPlayer.playOnce("/Sonidos/gameOver.wav");
                winner = 2; // Gana Azul
                return true;
            } else if (p2Dead) {
                soundPlayer.stop();
                soundPlayer.playOnce("/Sonidos/gameOver.wav");
                winner = 1; // Gana Rojo
                return true;
            }
            return false;
        }
    }

    private boolean checkPlayerDead(Larry p, int w, int h) {
        if (p == null) return false;
        
        // 1. Bordes (Ahora usa los márgenes de 100px)
        if (p.getX() < MARGIN_LEFT || p.getX() > w - MARGIN_RIGHT - TILE_SIZE ||
            p.getY() < MARGIN_TOP || p.getY() > h - MARGIN_BOTTOM - TILE_SIZE) {
            return true;
        }
        // 2. Muros
        for (Wall wall : walls) {
            if (wall.isSolid() && p.getBounds().intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }
    

    private void spawnNewItem(int w, int h) {
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

            // Verificar colisión P1
            if (x == player1.getX() && y == player1.getY()) occupied = true;
            
            // Verificar colisión P2 (si existe)
            if (isMultiplayer && player2 != null) {
                if (x == player2.getX() && y == player2.getY()) occupied = true;
            }

            for (Wall wVal : walls) {
                if (x == wVal.getX() && y == wVal.getY()) { occupied = true; break; }
            }
            
            attempts++;
            if (attempts > 500) return; // Evitar loop infinito

        } while (occupied);

        if (walls.size() >= 10 && random.nextInt(5) == 0) {
            currentItem = new Dynamite(x, y, TILE_SIZE);
        } else {
            currentItem = new TargetBrick(x, y, TILE_SIZE);
        }
    }
    
    private void calculateExplosion() {
        if (walls.isEmpty()) return;
        int amount = 2 + random.nextInt(4);
        for (int i = 0; i < amount; i++) {
            if (walls.isEmpty()) break;
            walls.remove(random.nextInt(walls.size()));
        }
    }

    private int snapToGrid(int value) { return (value / TILE_SIZE) * TILE_SIZE; }

    // Getters
    public int getTileSize() { return TILE_SIZE; }
    public int getMarginLeft() { return MARGIN_LEFT; }
    public int getMarginTop() { return MARGIN_TOP; }
    public Item getCurrentItem() { return currentItem; }
    public ArrayList<Wall> getWalls() { return walls; }
    
    public Larry getPlayer1() { return player1; }
    public Larry getPlayer2() { return player2; }
    public boolean isMultiplayer() { return isMultiplayer; }
    public int getWinner() { return winner; }
    public int getScore() { return walls.size(); }
}