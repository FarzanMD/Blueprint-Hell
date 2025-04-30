package View;

import Model.Port;
import Model.SystemNode;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final SystemNode testNode;

    public GamePanel() {
        setBackground(Color.WHITE);

        testNode = new SystemNode(200, 150, 120, 100);
        testNode.addInputPort(Port.Type.SQUARE);
        testNode.addInputPort(Port.Type.TRIANGLE);
        testNode.addOutputPort(Port.Type.SQUARE);
        testNode.addOutputPort(Port.Type.TRIANGLE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        testNode.draw((Graphics2D) g);
    }
}
