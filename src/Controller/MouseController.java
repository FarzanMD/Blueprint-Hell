package Controller;

import Model.GameModel;
import Model.Port;
import Model.SystemNode;
import Controller.WireController;
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
        if (clicked != null) {
            if (clicked.getSide() == Port.Side.RIGHT) {
                wireController.startWire(clicked);
            } else if (clicked.getSide() == Port.Side.LEFT) {
                wireController.tryConnect(clicked);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        wireController.updateMouse(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        wireController.updateMouse(e.getPoint());
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
