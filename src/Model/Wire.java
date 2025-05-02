package Model;

import java.awt.*;

public class Wire {
    private final Port outputPort;
    private final Port inputPort;

    public Wire(Port outputPort, Port inputPort) {
        this.outputPort = outputPort;
        this.inputPort = inputPort;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2));
        g.drawLine(outputPort.getX(), outputPort.getY(), inputPort.getX(), inputPort.getY());
    }
}
