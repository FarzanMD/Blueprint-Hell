package View;

import Model.GameModel;
import Model.SystemNode;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameModel model;

    public GamePanel() {
        setBackground(Color.WHITE);
        model = new GameModel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (SystemNode node : model.getSystems()) {
            node.draw((Graphics2D) g);
        }
    }
}
