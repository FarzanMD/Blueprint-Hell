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
    private final int MAX_TOTAL_LENGTH = 1000;


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

}
