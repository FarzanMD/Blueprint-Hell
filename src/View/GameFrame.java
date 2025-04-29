package View;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Blueprint Hell - Game");
        setUndecorated(true); // Removes title bar (prevents moving/resizing/minimizing)
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add game panel here
        add(new GamePanel());

        setVisible(true);
    }
}

