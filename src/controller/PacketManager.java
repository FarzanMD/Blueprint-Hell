package controller;

import model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PacketManager {
    private final List<Packet> packets = new ArrayList<>();
    private final CoinManager coinManager;
    private final GameModel model;
    private int sentSquare = 0;
    private int sentTriangle = 0;
    private int totalSent = 0;
    private int healthyReturned = 0;

    public boolean resultDeclared = false;   // reference to the start system

    public PacketManager(CoinManager coinManager, GameModel model) {
        this.coinManager = coinManager;
        this.model = model;
    }
    public void addPacket(Packet packet) {
        packets.add(packet);
    }
    public void spawnPacket(Packet.Shape shape, Wire wire) {
        if (wire != null && !wire.hasPacket()) {
            List<Port> startInputs = model.getStartNode().getInputPorts();
            Packet packet = new Packet(shape, wire, startInputs);

            //packets.add(new Packet(shape, wire));
            packets.add(packet);
            wire.setHasPacket(true);

            if (shape == Packet.Shape.SQUARE) {
                sentSquare++;
                totalSent++;
            } else {
                sentTriangle++;
                totalSent++;
            }
        }
    }

    public void update(float deltaTime, List<SystemNode> systems, List<Wire> allWires, ShopManager shopManager) {
        for (Packet packet : packets) {
            packet.advance(deltaTime, systems, allWires);
            if (packet.didJustEnterSystem()) {
                coinManager.addCoin();        // 💰 earn 1 coin
                packet.clearJustEnteredFlag();
            }
        }

        // Handle collisions
        // if Airyaman is active we have no collision
        if (!shopManager.isAiryamanActive()) {
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
        }

        //applying Atar
        if (shopManager.isAtarActive()) {
            for (Packet p : packets) {
                p.resetDisplacement(); //TODO
            }
        }


        // Remove dead packets and free their wires
        Iterator<Packet> it = packets.iterator();
        while (it.hasNext()) {
            Packet p = it.next();
            if (p.getHP() <= 0) {
                if (p.getCurrentWire() != null) {
                    p.getCurrentWire().setHasPacket(false);
                }
                it.remove();
            } else if (p.isFinished()) {
                if (p.getCurrentWire() != null) {
                    p.getCurrentWire().setHasPacket(false);
                }

                // 💡 If it finishes at the start system → healthy return
                SystemNode start = model.getStartNode();
                if (start != null && start.getInputPorts().contains(p.getLastInputPort())) {
                    healthyReturned++;

                }

                it.remove();
            }
        }
        // Let systems release queued packets
        for (SystemNode node : systems) {
            Packet released = node.trySendFromQueue(allWires);
            if (released != null) {
                packets.add(released);
            }
        }
        if (totalSent == model.getTotalPacketsWeWillWend()) {
            checkWinCondition();
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

    public void restoreAllPacketHP() {
        for (Packet packet : packets) {
            if (packet.getShape() == Packet.Shape.TRIANGLE) {
                packet.setHp(3);
            }
            if (packet.getShape() == Packet.Shape.SQUARE) {
                packet.setHp(2);
            }
        }
    }

    private void checkWinCondition() {


        if (resultDeclared) {
            System.out.println("tamam");
            return;
        }


        //int total = sentSquare + sentTriangle;

        int goal = model.getTotalPacketsWeWillWend() / 2;
        System.out.println(goal);


        if (healthyReturned > goal) {
            System.out.println("🎉 You Win!");
            resultDeclared = true;
        } else if (healthyReturned + packets.size() + model.getBufferedPacketCount() < goal) {
            // No more possible healthy packets left to reach the goal
            System.out.println("💀 You Lose!");
            resultDeclared = true;
        }
    }
}
