package model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class Packet {
    public enum Shape {SQUARE, TRIANGLE}
    private final Shape shape;
    private Wire currentWire;

    // Position & motion
    private float x, y;
    private float vx, vy;
    private float speed;
    private float acceleration = 0f;

    // HP & displacement
    private int hp;
    private float displacement = 0f;
    private static final float MAX_DISPLACEMENT = 15f;
    private static final float BASE_SPEED = 100f;

    // Flags/metadata
    private boolean justEnteredSystem = false;
    private Port lastInputPort;
    private final List<Port> startSystemInputs;

    // Segment walking
    private List<Line2D> segments = new ArrayList<>();
    private int segmentIndex = 0;
    private float segmentProgressPixels = 0f; // distance traveled along current segment in pixels

    public Packet(Shape shape, Wire initialWire, List<Port> startSystemInputs) {
        this.shape = shape;
        this.currentWire = initialWire;
        this.hp = shape == Shape.SQUARE ? 2 : 3;
        this.startSystemInputs = startSystemInputs;

        if (currentWire != null) {
            currentWire.setHasPacket(true);
            enterWire(initialWire);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isFinished() {
        // finished if dead, no wire and not queued; OR reached start inputs
        return hp <= 0 || (lastInputPort != null && startSystemInputs.contains(lastInputPort));
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public int getHP() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void applyHit() {
        hp--;
    }

    public void applyDisplacement(float amount) {
        displacement += amount;
    }

    public float[] getWirePerpendicular() {
        float[] dir = getWireDirection();
        // perpendicular: (-dy, dx)
        return new float[]{-dir[1], dir[0]};
    }

    public Point getPosition() {
        return new Point((int) x, (int) y);
    }

    /**
     * Advance along current wire segments. If the packet finishes the last segment,
     * we set lastInputPort and mark justEnteredSystem = true and clear currentWire (the manager will handle enqueuing).
     */
    public void advance(float delta, List<SystemNode> systems, List<Wire> allWires) {
        if (currentWire == null || hp <= 0) return;

        if (Math.abs(displacement) > MAX_DISPLACEMENT) {
            hp = 0;
            return;
        }

        // update speed by acceleration
        speed += acceleration * delta;

        float remainingMove = speed * delta;

        while (remainingMove > 0 && currentWire != null && segmentIndex < segments.size()) {
            Line2D seg = segments.get(segmentIndex);
            double ax = seg.getX1(), ay = seg.getY1();
            double bx = seg.getX2(), by = seg.getY2();
            double segLen = seg.getP1().distance(seg.getP2());
            if (segLen == 0) {
                // skip degenerate
                segmentIndex++;
                segmentProgressPixels = 0;
                continue;
            }

            // unit direction along this segment
            float dirX = (float) ((bx - ax) / segLen);
            float dirY = (float) ((by - ay) / segLen);

            float remainingOnSegment = (float) (segLen - segmentProgressPixels);

            if (remainingMove >= remainingOnSegment - 1e-6f) {
                // reach end of this segment
                x = (float) bx;
                y = (float) by;
                remainingMove -= remainingOnSegment;
                segmentIndex++;
                segmentProgressPixels = 0f;

                // if we just finished the last segment, we arrived at inputPort
                if (segmentIndex >= segments.size()) {
                    // finalize arrival
                    lastInputPort = currentWire.getInputPort();
                    justEnteredSystem = true;
                    // mark wire free (manager may also set false; keep it safe)
                    currentWire.setHasPacket(false);
                    // detach currentWire: let manager decide next step (enqueuing etc.)
                    currentWire = null;
                    // stop moving further in this tick
                    return;
                }
                // otherwise continue to next segment with leftover move
            } else {
                // move along current segment partially
                x += dirX * remainingMove;
                y += dirY * remainingMove;
                segmentProgressPixels += remainingMove;
                remainingMove = 0f;
            }
        }
    }

    /**
     * Called when this packet is put onto a wire or when a wire is selected as next.
     * Prepares segments and places the packet at the output port.
     */
    public void enterWire(Wire wire) {
        this.currentWire = wire;
        wire.setHasPacket(true);

        // place at output port
        this.x = wire.getOutputPort().getX();
        this.y = wire.getOutputPort().getY();

        // prepare segment list (deep copy)
        this.segments = new ArrayList<>(wire.getSegments());
        this.segmentIndex = 0;
        this.segmentProgressPixels = 0f;

        // compute initial motion parameters based on this wire endpoints (first segment)
        setupWireMotionForSegments(wire);
    }

    public boolean didJustEnterSystem() {
        return justEnteredSystem;
    }

    public void clearJustEnteredFlag() {
        justEnteredSystem = false;
    }

    /** Configure speed / acceleration based on this wire's end port types (same as previous behavior) */
    private void setupWireMotionForSegments(Wire wire) {
        // Use the wire's output and input port types (this is conservative; you may want segment-based types later)
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

        // Set vx/vy as direction of current segment (if exists)
        if (!segments.isEmpty()) {
            Line2D seg = segments.get(0);
            double segLen = seg.getP1().distance(seg.getP2());
            if (segLen != 0) {
                vx = (float) ((seg.getX2() - seg.getX1()) / segLen * speed);
                vy = (float) ((seg.getY2() - seg.getY1()) / segLen * speed);
            } else {
                vx = vy = 0;
            }
        }
    }

    public void draw(Graphics2D g) {
        if ((currentWire == null && !didJustEnterSystem()) || hp <= 0) return;

        float drawX = x;
        float drawY = y;

        if (currentWire != null || segmentIndex < segments.size()) {
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

    /**
     * Adjust packet position along the wire by `distance` pixels.
     * Handles crossing segment boundaries.
     */
    public void adjustAlongWire(float distance) {
        if (currentWire == null) return;
        float remaining = distance;

        while (Math.abs(remaining) > 0.0001f && currentWire != null) {
            if (segmentIndex >= segments.size()) {
                // already at end — nothing to adjust
                return;
            }
            Line2D seg = segments.get(segmentIndex);
            double segLen = seg.getP1().distance(seg.getP2());
            float dirX = 0, dirY = 0;
            if (segLen != 0) {
                dirX = (float) ((seg.getX2() - seg.getX1()) / segLen);
                dirY = (float) ((seg.getY2() - seg.getY1()) / segLen);
            }

            float remainingOnSeg = (float) (segLen - segmentProgressPixels);

            if (remaining >= 0) {
                // move forward
                if (remaining >= remainingOnSeg) {
                    // advance to segment end
                    x = (float) seg.getX2();
                    y = (float) seg.getY2();
                    remaining -= remainingOnSeg;
                    segmentIndex++;
                    segmentProgressPixels = 0f;
                    if (segmentIndex >= segments.size()) {
                        // arrived at input
                        lastInputPort = currentWire.getInputPort();
                        justEnteredSystem = true;
                        currentWire.setHasPacket(false);
                        currentWire = null;
                        return;
                    }
                } else {
                    x += dirX * remaining;
                    y += dirY * remaining;
                    segmentProgressPixels += remaining;
                    remaining = 0f;
                }
            } else {
                // move backward
                float back = -remaining;
                if (back <= segmentProgressPixels) {
                    // move backward within same segment
                    x -= dirX * back;
                    y -= dirY * back;
                    segmentProgressPixels -= back;
                    remaining = 0f;
                } else {
                    // move to previous segment end
                    float toBeginning = segmentProgressPixels;
                    x -= dirX * toBeginning;
                    y -= dirY * toBeginning;
                    remaining += toBeginning; // remaining is negative
                    // step to previous segment
                    if (segmentIndex > 0) {
                        segmentIndex--;
                        Line2D prev = segments.get(segmentIndex);
                        double prevLen = prev.getP1().distance(prev.getP2());
                        segmentProgressPixels = (float) prevLen;
                        x = (float) prev.getX2();
                        y = (float) prev.getY2();
                    } else {
                        // at very start, clamp
                        segmentProgressPixels = 0f;
                        remaining = 0f;
                        return;
                    }
                }
            }
        }
    }

    public float[] getWireDirection() {
        // Direction of current segment (or 0 if none)
        if (currentWire == null && segments.isEmpty()) return new float[]{0, 0};
        if (segmentIndex >= segments.size()) {
            return new float[]{0, 0};
        }
        Line2D seg = segments.get(segmentIndex);
        double segLen = seg.getP1().distance(seg.getP2());
        if (segLen == 0) return new float[]{0, 0};
        return new float[]{(float) ((seg.getX2() - seg.getX1()) / segLen),
                (float) ((seg.getY2() - seg.getY1()) / segLen)};
    }

    public void resetDisplacement() {
        displacement = 0f;
    }

    public Port getLastInputPort() {
        return lastInputPort;
    }
}
