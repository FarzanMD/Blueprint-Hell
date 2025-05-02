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

    public void startWire(Port outputPort) {
        this.selectedOutput = outputPort;
        //System.out.println("Starting wire from: " + outputPort);

    }

    public void updateMouse(Point point) {
        this.currentMouse = point;
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
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
                    new float[]{5}, 0));
            g.drawLine(selectedOutput.getX(), selectedOutput.getY(),
                    currentMouse.x, currentMouse.y);
        }
    }
}
