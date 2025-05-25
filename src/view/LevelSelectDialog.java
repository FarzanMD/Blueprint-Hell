package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class LevelSelectDialog extends JDialog {
    public LevelSelectDialog(
            JFrame parent,
            List<String> levelNames,
            Consumer<String> onLevelChosen
    ) {
        super(parent, "Select Level", true);
        setSize(300, 60 + 40*levelNames.size());
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(levelNames.size(), 1, 5, 5));
        for (String name : levelNames) {
            JButton btn = new JButton(name);
            btn.addActionListener(e -> {
                dispose();
                onLevelChosen.accept(name);
            });
            panel.add(btn);
        }
        add(panel, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }
}
