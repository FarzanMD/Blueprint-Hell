package View;


import Model.GameModel;
import Model.Port;
import Model.SystemNode;
import Model.WireManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private final GameModel model;
    private final WireManager wireManager;

    public GamePanel() {
        setBackground(Color.WHITE);
        model = new GameModel();
        wireManager = new WireManager();

        // Mouse interaction
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Port clicked = findPortAt(e.getPoint());

                if (clicked != null) {
                    if (clicked.getSide() == Port.Side.RIGHT) {
                        wireManager.startWire(clicked);
                    } else if (clicked.getSide() == Port.Side.LEFT) {
                        wireManager.tryConnect(clicked);
                        repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                wireManager.updateMousePosition(e.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                wireManager.updateMousePosition(e.getPoint());
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (SystemNode node : model.getSystems()) {
            node.draw((Graphics2D) g);
        }
        wireManager.draw((Graphics2D) g);
    }

    private Port findPortAt(Point point) {
        for (SystemNode node : model.getSystems()) {
            for (Port p : node.getInputPorts()) {
                if (isPointNear(point, p.getX(), p.getY())) return p;
            }
            for (Port p : node.getOutputPorts()) {
                if (isPointNear(point, p.getX(), p.getY())) return p;
            }
        }
        return null;
    }

    private boolean isPointNear(Point p, int x, int y) {
        int size = 10;
        return (Math.abs(p.x - x) <= size && Math.abs(p.y - y) <= size);
    }
}
