package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends JFrame {
    public MenuFrame() {
        setTitle("Blueprint Hell - Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton startButton = new JButton("Start");
        JButton levelsButton = new JButton("Levels");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");

        panel.add(startButton);
        panel.add(levelsButton);
        panel.add(settingsButton);
        panel.add(exitButton);
        add(panel);

        // ACTIONS
        startButton.addActionListener(e -> {
            // Close menu
            dispose();

            // Minimize all other windows (future improvement)
            // Stubbed for now

            // Launch game
            new GameFrame();
        });

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}
