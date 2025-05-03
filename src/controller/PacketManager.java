package controller;

import model.Packet;
import model.Wire;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();
    private final Random random = new Random();

    public void update(float deltaTime) {
        Iterator<Packet> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            packet.update(deltaTime);

            if (packet.isFinished()) {
                Wire wire = packet.getWire();
                if (wire != null) {
                    wire.setHasPacket(false);
                }

                // Optional: Route to next wire here.
                iterator.remove();
            }
        }
    }

    public void draw(Graphics2D g) {
        for (Packet packet : packets) {
            packet.draw(g);
        }
    }

    public void spawnPacket(Wire wire) {
        if (wire == null || wire.hasPacket()) return;

        Packet.Type type = random.nextBoolean() ? Packet.Type.SQUARE : Packet.Type.TRIANGLE;
        Packet packet = new Packet(wire, type);
        packets.add(packet);
    }

    public void reset() {
        for (Packet packet : packets) {
            if (packet.getWire() != null) {
                packet.getWire().setHasPacket(false);
            }
        }
        packets.clear();
    }

    public List<Packet> getPackets() {
        return packets;
    }
}
