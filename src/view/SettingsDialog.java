package view;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SettingsDialog extends JDialog {
    public SettingsDialog(
            JFrame parent,
            int initialVolume,
            ChangeListener onVolumeChange
    ) {
        super(parent, "Settings", true);
        setSize(350, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        JLabel label = new JLabel("Music Volume:", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        add(label, BorderLayout.NORTH);

        JSlider slider = new JSlider(0, 100, initialVolume);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(onVolumeChange);
        add(slider, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(close);
        add(btnPanel, BorderLayout.SOUTH);

        setResizable(false);
        setVisible(true);
    }
}
