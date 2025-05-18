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
        for (Packet packet : packets) {
            packet.advance(deltaTime, systems, allWires);
        }

        // Handle collisions
        for (int i = 0; i < packets.size(); i++) {
            Packet p1 = packets.get(i);
            Point pos1 = p1.getPosition();

            for (int j = i + 1; j < packets.size(); j++) {
                Packet p2 = packets.get(j);
                Point pos2 = p2.getPosition();

                if (pos1 != null && pos2 != null && pos1.distance(pos2) < 8) {
                    applyMutualImpact(p1, p2);
                }
            }
        }

        // Remove dead packets and free their wires
        Iterator<Packet> cleanup = packets.iterator();
        while (cleanup.hasNext()) {
            Packet p = cleanup.next();
            if (p.getHP() <= 0 || p.isFinished()) {
                if (p.getCurrentWire() != null) {
                    p.getCurrentWire().setHasPacket(false);
                }
                cleanup.remove();
            }
        }

        // Let systems release queued packets
        for (SystemNode node : systems) {
            Packet released = node.trySendFromQueue(allWires);
            if (released != null) {
                packets.add(released);
            }
        }
    }

    private void applyMutualImpact(Packet p1, Packet p2) {
        if (p1.getCurrentWire() == null || p2.getCurrentWire() == null) return;

        Point pos1 = p1.getPosition();
        Point pos2 = p2.getPosition();

        float dx = pos2.x - pos1.x;
        float dy = pos2.y - pos1.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        float ux = dx / dist;
        float uy = dy / dist;

        // --- Perpendicular vectors for visual displacement ---
        float[] perp1 = p1.getWirePerpendicular();
        float[] perp2 = p2.getWirePerpendicular();

        float dot1 = ux * perp1[0] + uy * perp1[1];
        float dot2 = -ux * perp2[0] + -uy * perp2[1];

        p1.applyDisplacement(-Math.signum(dot1) * 3f);
        p2.applyDisplacement(-Math.signum(dot2) * 3f);

        // --- Tangent (along-wire) projection to determine which packet is ahead ---
        float[] dir = p1.getWireDirection(); // Both packets should be on the same wire type
        float along1 = (pos1.x - p1.getCurrentWire().getOutputPort().getX()) * dir[0] +
                (pos1.y - p1.getCurrentWire().getOutputPort().getY()) * dir[1];

        float along2 = (pos2.x - p2.getCurrentWire().getOutputPort().getX()) * dir[0] +
                (pos2.y - p2.getCurrentWire().getOutputPort().getY()) * dir[1];

        float pushAmount = 10f;

        if (along1 > along2) {
            p1.adjustAlongWire(+pushAmount);
            p2.adjustAlongWire(-pushAmount);
        } else {
            p1.adjustAlongWire(-pushAmount);
            p2.adjustAlongWire(+pushAmount);
        }

        // --- Apply 1 HP damage each ---
        p1.applyHit();
        p2.applyHit();
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
