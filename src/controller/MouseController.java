package controller;

import model.GameModel;
import model.Port;
import model.SystemNode;
import model.Wire;

import javax.swing.*;
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

        // --- BEND MODE: start a bend on the nearest wire (delegated) ---
        if (wireController.isBendMode()) {
            // try to start bend at clicked point (WireController will handle coin, max-bends, etc.)
            boolean started = wireController.startBendAt(p);
            if (!started) {
                // optional: give user feedback
                Toolkit.getDefaultToolkit().beep();
            }
            e.getComponent().repaint();
            return;
        }

        // --- normal mode: maybe start dragging a system ---
        for (SystemNode node : model.getSystems()) {
            if (node.contains(p)) {
                draggingNode = node;
                lastMousePos = p;
                break;
            }
        }

        // Existing wire logic (start or finish wire)
        Port clicked = findPortAt(p);
        if (clicked != null) {
            if (clicked.getSide() == Port.Side.RIGHT) {
                wireController.startWire(clicked);
            } else if (clicked.getSide() == Port.Side.LEFT) {
                wireController.tryConnect(clicked);
            }
        }

        // Right-click deletes wire (only in normal mode)
        if (SwingUtilities.isRightMouseButton(e)) {
            Wire hovered = wireController.findWireNear(e.getPoint());
            if (hovered != null) {
                wireController.removeWire(hovered);
                e.getComponent().repaint();
            }
            return;
        }

        e.getComponent().repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // always update hover info for normal wire drawing/preview
        if (!wireController.isBendMode()) {
            wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
        } else {
            // in bend mode we may want to show nearest wire highlight — delegate to wireController
            wireController.updateBendHover(e.getPoint());
        }
        e.getComponent().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // If bending, update bend drag (delegated)
        if (wireController.isBendMode()) {
            wireController.updateBendDrag(e.getPoint());
            e.getComponent().repaint();
            return;
        }

        // Normal dragging of systems
        if (draggingNode != null && lastMousePos != null) {
            int dx = e.getX() - lastMousePos.x;
            int dy = e.getY() - lastMousePos.y;
            draggingNode.setPosition(draggingNode.getX() + dx, draggingNode.getY() + dy);
            lastMousePos = e.getPoint();
            e.getComponent().repaint();
        }

        // Update wire preview even while dragging
        wireController.updateMouse(e.getPoint(), findPortAt(e.getPoint()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // finalize bend if in bend mode
        if (wireController.isBendMode()) {
            wireController.finishBend();
            e.getComponent().repaint();
            return;
        }

        // otherwise clear dragging state
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
