package logic;

import data.Food;

public class FoodLogic {

    private Food food;
    // Define márgenes para el área de juego (en píxeles desde el borde)
    private final int MARGEN_IZQUIERDA = 100;
    private final int MARGEN_SUPERIOR = 100;
    private final int MARGEN_DERECHA = 100;
    private final int MARGEN_INFERIOR = 100;


    public FoodLogic(Food food) {
        this.food = food;
    }

    public void randomizePosition(int width, int height) {
        
        // --- Cálculo Corregido y Restringido ---
        
        // Calcular el ancho y alto del área jugable real
        int areaAncho = width - MARGEN_IZQUIERDA - MARGEN_DERECHA - food.getSize();
        int areaAlto = height - MARGEN_SUPERIOR - MARGEN_INFERIOR - food.getSize();

        // Generar coordenadas aleatorias dentro de esa área
        // Math.random() genera un número entre 0.0 y 1.0
        int newX = (int)(Math.random() * areaAncho) + MARGEN_IZQUIERDA;
        int newY = (int)(Math.random() * areaAlto) + MARGEN_SUPERIOR;

        // Asegurarse de que las coordenadas no sean negativas por si acaso
        if (newX < MARGEN_IZQUIERDA) newX = MARGEN_IZQUIERDA;
        if (newY < MARGEN_SUPERIOR) newY = MARGEN_SUPERIOR;
        
        food.setX(newX);
        food.setY(newY);
    }

    public boolean checkCollision(int sqX, int sqY, int sqSize) {
        return sqX < food.getX() + food.getSize() &&
               sqX + sqSize > food.getX() &&
               sqY < food.getY() + food.getSize() &&
               sqY + sqSize > food.getY();
    }
}
