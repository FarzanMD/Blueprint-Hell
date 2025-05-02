package view;

import javax.swing.*;
import java.awt.*;

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

        startButton.addActionListener(e -> {
            for (Window window : Window.getWindows()) {
                if (window != this && window instanceof JFrame) {
                    ((JFrame) window).setState(Frame.ICONIFIED);
                }
            }
            dispose();
            new GameFrame();
        });

        exitButton.addActionListener(e -> System.exit(0));
        setVisible(true);
    }
}
