package ui;

import javax.swing.JFrame;

public class GameWindow {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Juego con POO");
        GamePanel panel = new GamePanel();

        frame.add(panel);
        frame.setSize(800, 600); // puedes cambiar el tamaño
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

