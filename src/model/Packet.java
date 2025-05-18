package model;

import java.awt.*;
import java.util.List;

public class Packet {
    public enum Shape {SQUARE, TRIANGLE}

    private final Shape shape;
    private Wire currentWire;

    private float x, y;
    private float vx, vy;
    private float speed;
    private float acceleration = 0f;
    private int hp;

    private static final float BASE_SPEED = 100f;

    public Packet(Shape shape, Wire initialWire) {
        this.shape = shape;
        this.currentWire = initialWire;

        if (shape == Shape.SQUARE) {
            hp = 2;
        } else {
            hp = 3;
        }

        if (currentWire != null) {
            currentWire.setHasPacket(true);
            setupWireMotion(currentWire);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isFinished() {
        return currentWire == null || hp <= 0;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public int getHP() {
        return hp;
    }

    public void applyHit() {
        hp--;
    }

    public Point getPosition() {
        return new Point((int) x, (int) y);
    }

    public void advance(float delta, List<SystemNode> systems, List<Wire> allWires) {
        if (currentWire == null || hp <= 0) return;

        speed += acceleration * delta;

        // Recalculate direction based on live port position
        int tx = currentWire.getInputPort().getX();
        int ty = currentWire.getInputPort().getY();

        float dx = tx - x;
        float dy = ty - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            float normX = dx / distance;
            float normY = dy / distance;
            vx = normX * speed;
            vy = normY * speed;
        }

        x += vx * delta;
        y += vy * delta;

        // Re-check distance in case we’ve arrived or overshot
        dx = tx - x;
        dy = ty - y;

        if ((vx * dx <= 0) && (vy * dy <= 0)) {
            x = tx;
            y = ty;
            currentWire.setHasPacket(false);

            // Move to next wire if available
            Port inputPort = currentWire.getInputPort();
            currentWire = null;

            for (SystemNode node : systems) {
                if (node.getInputPorts().contains(inputPort)) {
                    Wire next = node.findNextAvailableWire(allWires);
                    if (next != null) {
                        next.setHasPacket(true);
                        currentWire = next;
                        setupWireMotion(next);
                    } else {
                        if (node.canAcceptPacket()) {
                            node.enqueuePacket(this);
                        }
                        return; // Packet held
                    }
                    break;
                }
            }

            if (currentWire == null) return;
        }
    }

    public void enterWire(Wire wire) {
        this.currentWire = wire;
        wire.setHasPacket(true);
        this.x = wire.getOutputPort().getX();
        this.y = wire.getOutputPort().getY();
        setupWireMotion(wire);
    }

    private void setupWireMotion(Wire wire) {
        x = wire.getOutputPort().getX();
        y = wire.getOutputPort().getY();

        int tx = wire.getInputPort().getX();
        int ty = wire.getInputPort().getY();

        float dx = tx - x;
        float dy = ty - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float normX = dx / distance;
        float normY = dy / distance;

        Port.Type outType = wire.getOutputPort().getType();
        Port.Type inType = wire.getInputPort().getType();

        boolean bothSquare = outType == Port.Type.SQUARE && inType == Port.Type.SQUARE;
        boolean bothTriangle = outType == Port.Type.TRIANGLE && inType == Port.Type.TRIANGLE;

        if (shape == Shape.SQUARE) {
            speed = bothSquare ? BASE_SPEED : (bothTriangle ? BASE_SPEED / 2f : BASE_SPEED);
            acceleration = 0;
        } else {
            if (bothTriangle) {
                speed = BASE_SPEED;
                acceleration = 0;
            } else if (bothSquare) {
                speed = BASE_SPEED * 0.5f;
                acceleration = BASE_SPEED * 0.8f;
            } else {
                speed = BASE_SPEED;
                acceleration = 0;
            }
        }

        vx = normX * speed;
        vy = normY * speed;
    }

    public void draw(Graphics2D g) {
        if (currentWire == null || hp <= 0) return;

        // Draw body
        g.setColor(Color.RED);
        switch (shape) {
            case SQUARE -> g.fillRect((int) x - 5, (int) y - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {(int) x, (int) x - 6, (int) x + 6};
                int[] ys = {(int) y - 6, (int) y + 6, (int) y + 6};
                g.fillPolygon(xs, ys, 3);
            }
        }

        // Draw HP text above packet
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(String.valueOf(hp), (int) x - 3, (int) y - 8);
    }
}
