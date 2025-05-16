package model;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Packet {
    public enum Shape { SQUARE, TRIANGLE }

    private final Shape shape;
    private final Queue<Wire> path = new LinkedList<>();
    private Wire currentWire = null;

    private float x, y;               // current position
    private float vx, vy;             // current velocity
    private float speed;              // current speed in pixels/sec
    private float acceleration = 0f;  // only used by triangle packets
    private int hp;                   // health points

    private static final float BASE_SPEED = 100f; // base speed (pixels/sec)

    public Packet(Shape shape, Queue<Wire> wirePath) {
        this.shape = shape;
        this.path.addAll(wirePath);

        if (shape == Shape.SQUARE) {
            hp = 2;
        } else if (shape == Shape.TRIANGLE) {
            hp = 3;
        }

        advanceToNextWire();
    }

    public Shape getShape() {
        return shape;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public boolean isFinished() {
        return currentWire == null;
    }

    public Point getPosition() {
        return new Point((int) x, (int) y);
    }

    public int getHP() {
        return hp;
    }

    public void applyHit() {
        hp--;
    }

    public void advance(float deltaTime) {
        if (currentWire == null) return;

        speed += acceleration * deltaTime;
        x += vx * deltaTime;
        y += vy * deltaTime;

        // Check if reached or passed the target
        int tx = currentWire.getInputPort().getX();
        int ty = currentWire.getInputPort().getY();

        float dx = tx - x;
        float dy = ty - y;

        if ((vx * dx <= 0) && (vy * dy <= 0)) {
            // Snap to exact destination
            x = tx;
            y = ty;

            currentWire.setHasPacket(false);
            advanceToNextWire();
        }
    }

    private void advanceToNextWire() {
        currentWire = path.poll();
        if (currentWire == null) return;

        currentWire.setHasPacket(true);

        // Reset position
        x = currentWire.getOutputPort().getX();
        y = currentWire.getOutputPort().getY();

        int tx = currentWire.getInputPort().getX();
        int ty = currentWire.getInputPort().getY();

        float dx = tx - x;
        float dy = ty - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float normX = dx / distance;
        float normY = dy / distance;

        // Set speed based on rules
        Port.Type outType = currentWire.getOutputPort().getType();
        Port.Type inType = currentWire.getInputPort().getType();

        boolean bothSquare = outType == Port.Type.SQUARE && inType == Port.Type.SQUARE;
        boolean bothTriangle = outType == Port.Type.TRIANGLE && inType == Port.Type.TRIANGLE;

        if (shape == Shape.SQUARE) {
            if (bothSquare) {
                speed = BASE_SPEED;
            } else if (bothTriangle) {
                speed = BASE_SPEED / 2f;
            } else {
                speed = BASE_SPEED; // neutral case
            }
            acceleration = 0;
        } else if (shape == Shape.TRIANGLE) {
            if (bothTriangle) {
                speed = BASE_SPEED;
                acceleration = 0;
            } else if (bothSquare) {
                speed = BASE_SPEED * 0.5f; // start slow
                acceleration = BASE_SPEED * 0.8f; // gains speed quickly
            } else {
                speed = BASE_SPEED;
                acceleration = 0;
            }
        }

        // Set velocity
        vx = normX * speed;
        vy = normY * speed;
    }

    public void draw(Graphics2D g) {
        if (currentWire == null) return;

        g.setColor(Color.RED);
        switch (shape) {
            case SQUARE -> g.fillRect((int) x - 5, (int) y - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {(int) x, (int) x - 6, (int) x + 6};
                int[] ys = {(int) y - 6, (int) y + 6, (int) y + 6};
                g.fillPolygon(xs, ys, 3);
            }
        }
    }
}
