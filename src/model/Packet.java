package model;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Packet {
    public enum Shape { SQUARE, TRIANGLE }

    private final Shape shape;
    private final Queue<Wire> path = new LinkedList<>();
    private Wire currentWire = null;
    private float progress = 0f; // Progress along the current wire [0.0, 1.0]

    public Packet(Shape shape, Queue<Wire> wirePath) {
        this.shape = shape;
        this.path.addAll(wirePath);
        advanceToNextWire();
    }

    public Shape getShape() {
        return shape;
    }

    public float getProgress() {
        return progress;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public boolean isFinished() {
        return currentWire == null;
    }

    public void advance(float delta) {
        if (currentWire == null) return;

        progress += delta;

        if (progress >= 1.0f) {
            // Packet has reached the end of this wire
            currentWire.setHasPacket(false);
            advanceToNextWire();
        }
    }

    private void advanceToNextWire() {
        progress = 0f;
        currentWire = path.poll();
        if (currentWire != null) {
            currentWire.setHasPacket(true);
        }
    }

    public Point getPosition() {
        if (currentWire == null) return null;

        int x1 = currentWire.getOutputPort().getX();
        int y1 = currentWire.getOutputPort().getY();
        int x2 = currentWire.getInputPort().getX();
        int y2 = currentWire.getInputPort().getY();

        int x = (int) (x1 + (x2 - x1) * progress);
        int y = (int) (y1 + (y2 - y1) * progress);
        return new Point(x, y);
    }

    public void draw(Graphics2D g) {
        Point pos = getPosition();
        if (pos == null) return;

        g.setColor(Color.RED);
        switch (shape) {
            case SQUARE -> g.fillRect(pos.x - 5, pos.y - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {pos.x, pos.x - 6, pos.x + 6};
                int[] ys = {pos.y - 6, pos.y + 6, pos.y + 6};
                g.fillPolygon(xs, ys, 3);
            }
        }
    }
}
