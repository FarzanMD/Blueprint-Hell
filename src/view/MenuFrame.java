package view;

import model.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

        // inside MenuFrame constructor, replace settingsButton listener:

        settingsButton.addActionListener(e -> {

            new SettingsDialog(
                    this,
                    MusicPlayer.getInstance().getVolume(),         // supplier
                    ev -> {
                        int vol = ((JSlider)ev.getSource()).getValue();
                        MusicPlayer.getInstance().setVolume(vol);   // apply change
                    }
            );
        });


        levelsButton.addActionListener(e -> {
            List<String> names = List.of("src/level1.json", "src/level2.json");
            new LevelSelectDialog(
                    this,
                    names,
                    filename -> {
                        // reload the game from scratch with chosen level
                        // e.g.:
                        dispose();               // close menu
                        // iconify all windows if needed
                        // Re-init GameFrame with fresh model+level:
                        SwingUtilities.invokeLater(() -> {
                            GameFrame gf = new GameFrame(filename);
                            gf.setVisible(true);
                        });
                    }
            );
        });


        setVisible(true);
    }
}
