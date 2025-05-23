package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class SystemNode {
    private int x, y, width, height;
    private final List<Port> inputPorts = new ArrayList<>();
    private final List<Port> outputPorts = new ArrayList<>();
    private final Queue<Packet> buffer = new LinkedList<>();
    public boolean isStartNode = false;

    public boolean isStartNode() {
        return isStartNode;
    }

    public void setStartNode(boolean startNode) {
        isStartNode = startNode;
    }

    public boolean canAcceptPacket() {
        return buffer.size() < 5;
    }

    public void enqueuePacket(Packet p) {
        buffer.add(p);
    }

    public Packet trySendFromQueue(List<Wire> allWires) {
        if (buffer.isEmpty()) return null;

        Wire freeWire = findNextAvailableWire(allWires);
        if (freeWire != null) {
            Packet p = buffer.poll();
            p.enterWire(freeWire);
            return p;
        }

        return null;
    }



    public SystemNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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

    public void draw(Graphics2D g, List<Wire> wires) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        g.setColor(Color.DARK_GRAY);
        for (Port port : inputPorts) port.draw(g);
        for (Port port : outputPorts) port.draw(g);

        // Valid system indicator (center box)
        int boxWidth = 40;
        int boxHeight = 20;
        int boxX = x + (width - boxWidth) / 2;
        int boxY = y-30 + (height - boxHeight) / 2;

        if (isFullyConnected(wires)) {
            g.setColor(Color.BLUE);
            g.fillRect(boxX, boxY, boxWidth, boxHeight);
        } else {
            g.setColor(Color.BLACK);
            g.drawRect(boxX, boxY, boxWidth, boxHeight);
        }

        //locate start node
        if (isStartNode){
            g.drawString("Start System" , x+ width/2-30,y+ height/2 +30 );

        }


        // Draw buffered packets
        int iconSize = 10;
        int spacing = 5;
        int startX = x + (width - (iconSize + spacing) * buffer.size()) / 2;
        int iconY = y - iconSize - 5; // Draw above the system

        int i = 0;
        for (Packet packet : buffer) {
            int px = startX + i * (iconSize + spacing);
            int py = iconY;

            g.setColor(Color.RED);
            switch (packet.getShape()) {
                case SQUARE -> g.fillRect(px, py, iconSize, iconSize);
                case TRIANGLE -> {
                    int[] xs = {px + iconSize / 2, px, px + iconSize};
                    int[] ys = {py, py + iconSize, py + iconSize};
                    g.fillPolygon(xs, ys, 3);
                }
            }
            i++;
        }


    }


    public List<Port> getInputPorts() {
        return inputPorts;
    }

    public void setPosition(int newX, int newY) {
        int dx = newX - this.x;
        int dy = newY - this.y;

        this.x = newX;
        this.y = newY;

        for (int i = 0; i < inputPorts.size(); i++) {
            Port port = inputPorts.get(i);
            port.setPosition(x, y + 20 + i * 20);
        }

        for (int i = 0; i < outputPorts.size(); i++) {
            Port port = outputPorts.get(i);
            port.setPosition(x + width, y + 20 + i * 20);
        }
    }

    public boolean contains(Point p) {
        return new Rectangle(x, y, width, height).contains(p);
    }



    public List<Port> getOutputPorts() {
        return outputPorts;
    }
    /*
    public List<Packet> processIncomingPacket(Packet incoming, List<Wire> allWires) {
        List<Packet> outPackets = new ArrayList<>();

        for (Port outputPort : outputPorts) {
            for (Wire wire : allWires) {
                if (wire.getOutputPort() == outputPort && !wire.hasPacket()) {
                    // Only use wires that are not occupied
                    Queue<Wire> path = new LinkedList<>();
                    path.add(wire);

                    Packet.Shape shape = Packet.Shape.valueOf(outputPort.getType().name());
                    Packet newPacket = new Packet(shape, path);
                    outPackets.add(newPacket);
                    wire.setHasPacket(true); // Reserve the wire for this new packet
                    break; // move to next output port
                }
            }
        }

        return outPackets;
    }
     */
    public Wire findNextAvailableWire(List<Wire> allWires) {
        for (Port outputPort : outputPorts) {
            for (Wire wire : allWires) {
                if (wire.getOutputPort() == outputPort && !wire.hasPacket()) {
                    return wire;
                }
            }
        }
        return null; // No free wire
    }

    public boolean isFullyConnected(List<Wire> wires) {
        for (Port port : inputPorts) {
            if (wires.stream().noneMatch(w -> w.getInputPort() == port)) {
                return false;
            }
        }
        for (Port port : outputPorts) {
            if (wires.stream().noneMatch(w -> w.getOutputPort() == port)) {
                return false;
            }
        }
        return true;
    }

    /** Return list of input port Types */
    public List<Port.Type> getInputPortTypes() {
        return inputPorts.stream()
                .map(Port::getType)
                .collect(Collectors.toList());
    }

    /** Return list of output port Types */
    public List<Port.Type> getOutputPortTypes() {
        return outputPorts.stream()
                .map(Port::getType)
                .collect(Collectors.toList());
    }

}
