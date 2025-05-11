package controller;

import model.Packet;
import model.Wire;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();

    public void spawnPacket(Packet.Shape shape, List<Wire> path) {
        Queue<Wire> queue = new LinkedList<>(path);
        if (!queue.isEmpty() && !queue.peek().hasPacket()) {
            packets.add(new Packet(shape, queue));
        }
    }

    public void update(float delta) {
        Iterator<Packet> iterator = packets.iterator();
        while (iterator.hasNext()) {
            Packet packet = iterator.next();
            packet.advance(delta);
            if (packet.isFinished()) {
                iterator.remove(); // Packet reached end
            }
        }
    }

    public void draw(Graphics2D g) {
        for (Packet packet : packets) {
            packet.draw(g);
        }
    }
}
