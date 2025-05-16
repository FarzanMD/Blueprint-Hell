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
    private SystemNode draggingNode = null;
    private Point lastMousePos = null;


    public MouseController(GameModel model, WireController wireController) {
        this.model = model;
        this.wireController = wireController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        for (SystemNode node : model.getSystems()) {
            if (node.contains(p)) {
                draggingNode = node;
                lastMousePos = p;
                break;
            }
        }

        // Existing wire logic...
        Port clicked = findPortAt(p);
        if (clicked != null) {
            if (clicked.getSide() == Port.Side.RIGHT) {
                wireController.startWire(clicked);
            } else if (clicked.getSide() == Port.Side.LEFT) {
                wireController.tryConnect(clicked);
            }
        }

        e.getComponent().repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
        e.getComponent().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggingNode != null && lastMousePos != null) {
            int dx = e.getX() - lastMousePos.x;
            int dy = e.getY() - lastMousePos.y;
            draggingNode.setPosition(draggingNode.getX() + dx, draggingNode.getY() + dy);
            lastMousePos = e.getPoint();
            e.getComponent().repaint();
        }

        wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        draggingNode = null;
        lastMousePos = null;
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
