package view;

import controller.MouseController;
import controller.WireController;
import model.GameModel;
import model.SystemNode;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameModel model;
    private final WireController wireController;

    public GamePanel() {
        setBackground(Color.WHITE);
        model = new GameModel();
        wireController = new WireController();

        MouseController mouseController = new MouseController(model, wireController);
        addMouseListener(mouseController);
        addMouseMotionListener(mouseController);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (SystemNode node : model.getSystems()) {
            node.draw((Graphics2D) g);
        }
        wireController.draw((Graphics2D) g);
    }
}
