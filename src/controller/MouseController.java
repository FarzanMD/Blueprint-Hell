package controller;

import model.GameModel;
import model.Port;
import model.SystemNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseController extends MouseAdapter {
    private final GameModel model;
    private final WireController wireController;

    public MouseController(GameModel model, WireController wireController) {
        this.model = model;
        this.wireController = wireController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Port clicked = findPortAt(e.getPoint());
//        System.out.println("Mouse pressed at: " + e.getPoint());
//        System.out.println("Port clicked: " + clicked);

        if (clicked != null) {
            if (clicked.getSide() == Port.Side.RIGHT) {
                wireController.startWire(clicked);
                Component c = (Component) e.getComponent();
                c.repaint();
            } else if (clicked.getSide() == Port.Side.LEFT) {
                wireController.tryConnect(clicked);
                Component c = (Component) e.getComponent();
                c.repaint();

            }
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
        e.getComponent().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
        e.getComponent().repaint();
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
