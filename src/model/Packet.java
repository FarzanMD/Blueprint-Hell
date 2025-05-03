package model;

import java.awt.*;

public class Packet {
    public enum Type { SQUARE, TRIANGLE }

    private Wire wire;
    private Type type;
    private float progress = 0.0f;
    private static final float SPEED = 100f; // pixels per second

    public Packet(Wire wire, Type type) {
        this.wire = wire;
        this.type = type;
        if (this.wire != null) {
            this.wire.setHasPacket(true);
        }
    }

    public void update(float deltaTime) {
        if (wire == null) return;

        float distance = getDistance();
        progress += SPEED * deltaTime / distance;
        if (progress >= 1.0f) {
            progress = 1.0f;
            if (wire != null) {
                wire.setHasPacket(false);
            }
        }
    }


    public boolean isFinished() {
        return progress >= 1.0f;
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
        switch (type) {
            case SQUARE -> g.fillRect(px - 5, py - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {px, px - 6, px + 6};
                int[] ys = {py - 6, py + 6, py + 6};
                g.fillPolygon(xs, ys, 3);
            }
        }
    }

    private float getDistance() {
        if (wire == null) return 1f;
        int x1 = wire.getOutputPort().getX();
        int y1 = wire.getOutputPort().getY();
        int x2 = wire.getInputPort().getX();
        int y2 = wire.getInputPort().getY();
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void setWire(Wire newWire) {
        if (this.wire != null) {
            this.wire.setHasPacket(false);
        }
        this.wire = newWire;
        this.progress = 0.0f;
        if (this.wire != null) {
            this.wire.setHasPacket(true);
        }
    }

    public Wire getWire() {
        return wire;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
