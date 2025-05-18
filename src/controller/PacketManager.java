package controller;

import model.Packet;
import model.SystemNode;
import model.Wire;

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
        }

        // Collision detection
        for (int i = 0; i < packets.size(); i++) {
            Packet p1 = packets.get(i);
            Point pos1 = p1.getPosition();

            for (int j = i + 1; j < packets.size(); j++) {
                Packet p2 = packets.get(j);
                Point pos2 = p2.getPosition();

                if (pos1 != null && pos2 != null && pos1.distance(pos2) < 12) {
                    p1.applyHit();
                    p2.applyHit();
                }
            }
        }

        // Remove destroyed packets
        packets.removeIf(p -> p.getHP() <= 0 || p.isFinished());

        // Systems try to release held packets
        for (SystemNode node : systems) {
            Packet released = node.trySendFromQueue(allWires);
            if (released != null) {
                packets.add(released);
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
