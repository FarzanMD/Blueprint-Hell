package Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SystemNode {
    private int x, y, width, height;
    private final List<Port> inputPorts = new ArrayList<>();
    private final List<Port> outputPorts = new ArrayList<>();

    public SystemNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addInputPort(Port.Type type) {
        int portY = y + 20 + inputPorts.size() * 20;
        Port port = new Port(type, Port.Side.LEFT, x, portY);
        inputPorts.add(port);
    }

    public void addOutputPort(Port.Type type) {
        int portY = y + 20 + outputPorts.size() * 20;
        Port port = new Port(type, Port.Side.RIGHT, x + width, portY);
        outputPorts.add(port);
    }

    public void draw(Graphics2D g) {
        // Draw system rectangle
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        // Draw ports
        g.setColor(Color.DARK_GRAY);
        for (Port port : inputPorts) port.draw(g);
        for (Port port : outputPorts) port.draw(g);
    }
}
