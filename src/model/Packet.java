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

    private float displacement = 0f;
    private static final float MAX_DISPLACEMENT = 15f;
    private static final float BASE_SPEED = 100f;

    public Packet(Shape shape, Wire initialWire) {
        this.shape = shape;
        this.currentWire = initialWire;
        this.hp = shape == Shape.SQUARE ? 2 : 3;

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

    public void applyDisplacement(float amount) {
        displacement += amount;
    }

    public float[] getWirePerpendicular() {
        if (currentWire == null) return new float[]{0, 0};

        int x1 = currentWire.getOutputPort().getX();
        int y1 = currentWire.getOutputPort().getY();
        int x2 = currentWire.getInputPort().getX();
        int y2 = currentWire.getInputPort().getY();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return new float[]{0, 0};

        return new float[]{-dy / len, dx / len};
    }

    public Point getPosition() {
        return new Point((int) x, (int) y);
    }

    public void advance(float delta, List<SystemNode> systems, List<Wire> allWires) {
        if (currentWire == null || hp <= 0) return;

        if (Math.abs(displacement) > MAX_DISPLACEMENT) {
            hp = 0;
            return;
        }

        speed += acceleration * delta;

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

        dx = tx - x;
        dy = ty - y;

        if ((vx * dx <= 0) && (vy * dy <= 0)) {
            x = tx;
            y = ty;
            currentWire.setHasPacket(false);

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
                        return;
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

        float drawX = x;
        float drawY = y;

        if (currentWire != null) {
            float[] perp = getWirePerpendicular();
            drawX += perp[0] * displacement;
            drawY += perp[1] * displacement;
        }

        g.setColor(Color.RED);
        switch (shape) {
            case SQUARE -> g.fillRect((int) drawX - 5, (int) drawY - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {(int) drawX, (int) drawX - 6, (int) drawX + 6};
                int[] ys = {(int) drawY - 6, (int) drawY + 6, (int) drawY + 6};
                g.fillPolygon(xs, ys, 3);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(String.valueOf(hp), (int) drawX - 3, (int) drawY - 8);
    }

    public void adjustAlongWire(float distance) {
        if (currentWire == null) return;

        float[] dir = getWireDirection();
        x += dir[0] * distance;
        y += dir[1] * distance;
    }

    public float[] getWireDirection() {
        if (currentWire == null) return new float[]{0, 0};
        int x1 = currentWire.getOutputPort().getX();
        int y1 = currentWire.getOutputPort().getY();
        int x2 = currentWire.getInputPort().getX();
        int y2 = currentWire.getInputPort().getY();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return new float[]{0, 0};

        return new float[]{dx / len, dy / len};
    }

}
