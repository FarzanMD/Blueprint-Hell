package controller;

import model.Packet;
import model.SystemNode;
import model.Wire;
import model.Port;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

    public void spawnPacket(Packet.Shape shape, Wire wire) {
        if (wire != null && !wire.hasPacket()) {
            packets.add(new Packet(shape, wire));
            wire.setHasPacket(true);
        }
    }

    public void update(float deltaTime, List<SystemNode> systems, List<Wire> allWires) {
        Iterator<Packet> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            packet.advance(deltaTime, systems, allWires);

            if (packet.isFinished()) {
                iterator.remove();
            }
        }
        // Let systems try to release held packets
        for (SystemNode node : systems) {
            Packet released = node.trySendFromQueue(allWires);
            if (released != null) {
                packets.add(released); // 💡 THIS is the missing part
            }
        }


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
