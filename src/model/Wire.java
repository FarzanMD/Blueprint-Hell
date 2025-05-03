package model;

import java.awt.*;

public class Wire {
    private final Port outputPort;
    private final Port inputPort;
    private boolean hasPacket = false;

    public Wire(Port outputPort, Port inputPort) {
        this.outputPort = outputPort;
        this.inputPort = inputPort;
    }

    public Port getOutputPort() {
        return outputPort;
    }

    public Port getInputPort() {
        return inputPort;
    }

    public boolean hasPacket() {
        return hasPacket;
    }

    public void setHasPacket(boolean hasPacket) {
        this.hasPacket = hasPacket;
    }

    public void draw(Graphics2D g) {
        if (hasPacket) {
            g.setColor(Color.RED); // Indicate wire is active
        } else {
            g.setColor(Color.BLUE);
        }
        g.setStroke(new BasicStroke(2));
        g.drawLine(outputPort.getX(), outputPort.getY(), inputPort.getX(), inputPort.getY());
    }
}
