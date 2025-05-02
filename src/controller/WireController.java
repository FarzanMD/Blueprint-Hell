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


    public void startWire(Port outputPort) {
        this.selectedOutput = outputPort;
        //System.out.println("Starting wire from: " + outputPort);

    }

    public void updateMouse(Point point, Port hovered) {
        this.currentMouse = point;
        this.hoveredPort = hovered;
    }


    public void tryConnect(Port inputPort) {
        if (selectedOutput != null && inputPort != null) {
            if (selectedOutput.getSide() == Port.Side.RIGHT &&
                    inputPort.getSide() == Port.Side.LEFT &&
                    selectedOutput.getType() == inputPort.getType()) {
                wires.add(new Wire(selectedOutput, inputPort));
            }
        }
        selectedOutput = null;
        currentMouse = null;
        //System.out.println("Trying to connect: " + selectedOutput + " → " + inputPort);

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
