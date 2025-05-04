package controller;

import model.Packet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

    public void update(float deltaTime) {
        Iterator<Packet> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            packet.advance(deltaTime);

            if (packet.isFinished()) {
                iterator.remove();
            }
        }
    }

    public void draw(Graphics2D g) {
        for (Packet packet : packets) {
            packet.draw(g);
        }
    }
}
