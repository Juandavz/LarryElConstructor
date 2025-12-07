package ui;

import javax.swing.JFrame;

public class GameWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Larry el Constructor - Proyecto POO");
        GamePanel panel = new GamePanel();

        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}