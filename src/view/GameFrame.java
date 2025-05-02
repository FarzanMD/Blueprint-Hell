package view;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Blueprint Hell - Game");
        setUndecorated(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setVisible(true);
    }
}
