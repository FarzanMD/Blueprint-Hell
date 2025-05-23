package controller;

import model.Port;
import model.Wire;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WireController {
    private final List<Wire> wires = new ArrayList<>();
    private Port selectedOutput = null;
    private Point currentMouse = null;
    private Port hoveredPort = null;
    private int MAX_TOTAL_LENGTH = Integer.MAX_VALUE;
    private LevelManager levelManager;

    public WireController(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void setMAX_TOTAL_LENGTH(int MAX_TOTAL_LENGTH) {
        this.MAX_TOTAL_LENGTH = MAX_TOTAL_LENGTH;
    }

    public int getTotalWireLength() {
        return wires.stream().mapToInt(Wire::getLength).sum();
    }

    private boolean portAlreadyUsed(Port port) {
        for (Wire wire : wires) {
            if (wire.getInputPort() == port || wire.getOutputPort() == port) {
                return true;
            }
        }
        return false;
    }
    public void removeWire(Wire wire) {
        wires.remove(wire);
    }



    public void startWire(Port outputPort) {
        this.selectedOutput = outputPort;
        //System.out.println("Starting wire from: " + outputPort);

    }

    public List<Wire> getWires() {
        return wires;
    }


    public Port getSelectedOutput() {
        return selectedOutput;
    }

    public void setSelectedOutput(Port selectedOutput) {
        this.selectedOutput = selectedOutput;
    }

    public Point getCurrentMouse() {
        return currentMouse;
    }

    public void setCurrentMouse(Point currentMouse) {
        this.currentMouse = currentMouse;
    }

    public Port getHoveredPort() {
        return hoveredPort;
    }

    public void setHoveredPort(Port hoveredPort) {
        this.hoveredPort = hoveredPort;
    }

    public void updateMouse(Point point, Port hovered) {
        this.currentMouse = point;
        this.hoveredPort = hovered;
    }


    public void tryConnect(Port inputPort) {
        if (selectedOutput != null && inputPort != null) {
            if (selectedOutput.getSide() == Port.Side.RIGHT &&
                    inputPort.getSide() == Port.Side.LEFT &&
                    selectedOutput.getType() == inputPort.getType() &&
                    !portAlreadyUsed(selectedOutput) &&
                    !portAlreadyUsed(inputPort)) {

                Wire temp = new Wire(selectedOutput, inputPort);

                if (getTotalWireLength() + temp.getLength() <= MAX_TOTAL_LENGTH) {
                    wires.add(temp);
                } else {
                    System.out.println("⚠️ Wire too long: exceeds max network length!");
                }
            } else {
                System.out.println("❌ Invalid connection or port already used.");
            }
        }

        selectedOutput = null;
        currentMouse = null;
        hoveredPort = null;


    }

    public void draw(Graphics2D g) {
        for (Wire wire : wires) {
            wire.draw(g);
        }

        if (selectedOutput != null && currentMouse != null) {
            boolean valid = hoveredPort != null &&
                    hoveredPort.getSide() == Port.Side.LEFT &&
                    hoveredPort.getType() == selectedOutput.getType();

            g.setColor(valid ? Color.BLUE : Color.RED);
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(selectedOutput.getX(), selectedOutput.getY(),
                    currentMouse.x, currentMouse.y);
        }
    }
    public Wire findWireNear(Point p) {
        for (Wire wire : wires) {
            Point a = new Point(wire.getOutputPort().getX(), wire.getOutputPort().getY());
            Point b = new Point(wire.getInputPort().getX(), wire.getInputPort().getY());

            // Check perpendicular distance from point to line segment
            if (distanceToSegment(p, a, b) < 6) {
                return wire;
            }
        }
        return null;
    }

    // Utility
    private double distanceToSegment(Point p, Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;

        if (dx == 0 && dy == 0) return p.distance(a);

        double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = a.x + t * dx;
        double projY = a.y + t * dy;

        return p.distance(projX, projY);
    }


    public void clearWires() {
        wires.clear();
    }
    public void tryConnectDirect(Port outputPort, Port inputPort) {
        // Same validation as in tryConnect(...)
        if (outputPort.getSide() == Port.Side.RIGHT &&
                inputPort.getSide()  == Port.Side.LEFT  &&
                outputPort.getType() == inputPort.getType() &&
                !portAlreadyUsed(outputPort) &&
                !portAlreadyUsed(inputPort)) {

            Wire wire = new Wire(outputPort, inputPort);
            if (getTotalWireLength() + wire.getLength() <= MAX_TOTAL_LENGTH) {
                wires.add(wire);
            } else {
                System.out.println("⚠️ Direct wire too long: exceeds max network length!");
            }
        } else {
            System.out.println("❌ Direct wire invalid or port already used.");
        }
    }
}
