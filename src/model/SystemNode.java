package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        inputPorts.add(new Port(type, Port.Side.LEFT, x, portY));
    }

    public void addOutputPort(Port.Type type) {
        int portY = y + 20 + outputPorts.size() * 20;
        outputPorts.add(new Port(type, Port.Side.RIGHT, x + width, portY));
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        g.setColor(Color.DARK_GRAY);
        for (Port port : inputPorts) port.draw(g);
        for (Port port : outputPorts) port.draw(g);
    }

    public List<Port> getInputPorts() {
        return inputPorts;
    }

    public List<Port> getOutputPorts() {
        return outputPorts;
    }
    public List<Packet> processIncomingPacket(Packet incoming, List<Wire> allWires) {
        List<Packet> outPackets = new ArrayList<>();

        for (Port outputPort : outputPorts) {
            for (Wire wire : allWires) {
                if (wire.getOutputPort() == outputPort && !wire.hasPacket()) {
                    Queue<Wire> path = new LinkedList<>();
                    path.add(wire);

                    Packet.Shape shape = Packet.Shape.valueOf(outputPort.getType().name());
                    Packet newPacket = new Packet(shape, path);
                    wire.setHasPacket(true);

                    outPackets.add(newPacket);
                    break; // Only one wire per output port
                }
            }
        }

        return outPackets;
    }


}
