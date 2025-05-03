package model;

import java.awt.*;

public class Packet {
    private Wire wire;
    private float progress; // 0.0 to 1.0

    public Packet(Wire wire) {
        setWire(wire); // Use setter to handle flags
        this.progress = 0f;
    }

    public void update(float deltaTime) {
        progress += deltaTime;
        if (progress >= 1f) {
            progress = 1f;
            if (wire != null) {
                wire.setHasPacket(false); // Packet has reached end
            }
        }
    }

    public void setWire(Wire newWire) {
        if (this.wire != null) {
            this.wire.setHasPacket(false); // Clear old wire
        }
        this.wire = newWire;
        this.progress = 0f;
        if (newWire != null) {
            newWire.setHasPacket(true); // Mark new wire
        }
    }

    public void draw(Graphics2D g) {
        if (wire == null) return;

        int x1 = wire.getOutputPort().getX();
        int y1 = wire.getOutputPort().getY();
        int x2 = wire.getInputPort().getX();
        int y2 = wire.getInputPort().getY();

        int px = (int) (x1 + (x2 - x1) * progress);
        int py = (int) (y1 + (y2 - y1) * progress);

        g.setColor(Color.RED);
        g.fillOval(px - 5, py - 5, 10, 10);
    }

    public boolean isFinished() {
        return progress >= 1f;
    }

    public Wire getWire() {
        return wire;
    }
}
