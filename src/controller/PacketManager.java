package controller;

import model.Packet;
import model.SystemNode;
import model.Wire;
import model.Port;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

    public void spawnPacket(Packet.Shape shape, List<Wire> path) {
        Queue<Wire> queue = new LinkedList<>(path);
        if (!queue.isEmpty() && !queue.peek().hasPacket()) {
            Packet packet = new Packet(shape, queue);
            packets.add(packet);
        }
    }

    public void update(float deltaTime, List<SystemNode> systems, List<Wire> allWires) {
        List<Packet> newPackets = new ArrayList<>();

        Iterator<Packet> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            packet.advance(deltaTime);

            if (packet.isFinished()) {
                Wire wire = packet.getCurrentWire();
                if (wire != null) {
                    Port inputPort = wire.getInputPort();

                    for (SystemNode node : systems) {
                        if (node.getInputPorts().contains(inputPort)) {
                            // Split packet at this system
                            List<Packet> generated = node.processIncomingPacket(packet, allWires);
                            newPackets.addAll(generated);
                            break;
                        }
                    }
                }

                iterator.remove(); // Remove finished packet
            }
        }

        packets.addAll(newPackets);
    }

    public void draw(Graphics2D g) {
        for (Packet packet : packets) {
            packet.draw(g);
        }
    }

    public List<Packet> getPackets() {
        return packets;
    }
}
