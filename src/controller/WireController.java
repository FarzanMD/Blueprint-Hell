package controller;

import model.Port;
import model.Wire;
import model.SystemNode;
import model.GameModel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class WireController {
    private final List<Wire> wires = new ArrayList<>();
    private Port selectedOutput = null;
    private Point currentMouse = null;
    private Port hoveredPort = null;
    private int MAX_TOTAL_LENGTH = Integer.MAX_VALUE;
    private LevelManager levelManager;
    private boolean bendMode = false;
    private Wire activeWire = null;
    private Point selectedBendPoint = null;
    private Point bendOrigin = null;

    public WireController(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void setMAX_TOTAL_LENGTH(int MAX_TOTAL_LENGTH) {
        this.MAX_TOTAL_LENGTH = MAX_TOTAL_LENGTH;
    }

    public int getMAX_TOTAL_LENGTH() {
        return MAX_TOTAL_LENGTH;
    }

    public int getTotalWireLength() {
        return wires.stream().mapToInt(Wire::getLength).sum();
    }

    public void toggleBendMode() {
        bendMode = !bendMode;
        activeWire = null;
        selectedBendPoint = null;
        bendOrigin = null;
    }

    public boolean isBendMode() {
        return bendMode;
    }

    private boolean portAlreadyUsed(Port port) {
        for (Wire wire : wires) {
            if (wire.getInputPort() == port || wire.getOutputPort() == port) {
                return true;
            }
        }
        return false;
    }

    public void removeWire(Wire wire) {
        wires.remove(wire);
    }

    public void startWire(Port outputPort) {
        this.selectedOutput = outputPort;
    }

    public List<Wire> getWires() {
        return wires;
    }

    public Port getSelectedOutput() {
        return selectedOutput;
    }

    public void setSelectedOutput(Port selectedOutput) {
        this.selectedOutput = selectedOutput;
    }

    public Point getCurrentMouse() {
        return currentMouse;
    }

    public void setCurrentMouse(Point currentMouse) {
        this.currentMouse = currentMouse;
    }

    public Port getHoveredPort() {
        return hoveredPort;
    }

    public void setHoveredPort(Port hoveredPort) {
        this.hoveredPort = hoveredPort;
    }

    public void updateMouse(Point point, Port hovered) {
        this.currentMouse = point;
        this.hoveredPort = hovered;
    }

    public void tryConnect(Port inputPort) {
        if (selectedOutput != null && inputPort != null) {
            if (selectedOutput.getSide() == Port.Side.RIGHT &&
                    inputPort.getSide() == Port.Side.LEFT &&
                    selectedOutput.getType() == inputPort.getType() &&
                    !portAlreadyUsed(selectedOutput) &&
                    !portAlreadyUsed(inputPort)) {

                Wire temp = new Wire(selectedOutput, inputPort);

                if (getTotalWireLength() + temp.getLength() <= MAX_TOTAL_LENGTH) {
                    // validate against systems (should not cross any system)
                    if (isWireValid(temp)) {
                        wires.add(temp);
                    } else {
                        System.out.println("❌ Connection crosses a system—invalid.");
                    }
                } else {
                    System.out.println("⚠️ Wire too long: exceeds max network length!");
                }
            } else {
                System.out.println("❌ Invalid connection or port already used.");
            }
        }

        selectedOutput = null;
        currentMouse = null;
        hoveredPort = null;
    }

    public void draw(java.awt.Graphics2D g) {
        for (Wire wire : wires) {
            wire.draw(g);
        }

        if (!bendMode && selectedOutput != null && currentMouse != null) {
            boolean valid = hoveredPort != null &&
                    hoveredPort.getSide() == Port.Side.LEFT &&
                    hoveredPort.getType() == selectedOutput.getType();

            g.setColor(valid ? Color.BLUE : Color.RED);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawLine(selectedOutput.getX(), selectedOutput.getY(),
                    currentMouse.x, currentMouse.y);
        }

        // If in bend mode and an active wire is selected, draw a highlight
        if (bendMode && activeWire != null && selectedBendPoint != null) {
            g.setColor(new Color(255, 165, 0, 180)); // translucent orange
            g.fillOval(selectedBendPoint.x - 6, selectedBendPoint.y - 6, 12, 12);
        }
    }

    /**
     * Try to find a wire near the given point. This checks all segments (works with bends).
     */
    public Wire findWireNear(Point p) {
        for (Wire wire : wires) {
            List<Line2D> segs = wire.getSegments();
            for (Line2D seg : segs) {
                double dist = seg.ptSegDist(p.x, p.y);
                if (dist < 8) return wire;
            }
        }
        return null;
    }

    // Utility
    private double distanceToSegment(Point p, Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;

        if (dx == 0 && dy == 0) return p.distance(a);

        double t = ((p.x - a.x) * dx + (p.y - a.y) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = a.x + t * dx;
        double projY = a.y + t * dy;

        return p.distance(projX, projY);
    }

    public void clearWires() {
        wires.clear();
    }

    public void tryConnectDirect(Port outputPort, Port inputPort) {
        // Same validation as in tryConnect(...)
        if (outputPort.getSide() == Port.Side.RIGHT &&
                inputPort.getSide() == Port.Side.LEFT &&
                outputPort.getType() == inputPort.getType() &&
                !portAlreadyUsed(outputPort) &&
                !portAlreadyUsed(inputPort)) {

            Wire wire = new Wire(outputPort, inputPort);
            if (getTotalWireLength() + wire.getLength() <= MAX_TOTAL_LENGTH) {
                if (isWireValid(wire)) {
                    wires.add(wire);
                } else {
                    System.out.println("⚠️ Direct wire invalid (crosses a system).");
                }
            } else {
                System.out.println("⚠️ Direct wire too long: exceeds max network length!");
            }
        } else {
            System.out.println("❌ Direct wire invalid or port already used.");
        }
    }

    // ------------------- BEND TOOL API -------------------

    /**
     * Called by MouseController when user clicks in bend mode.
     * Attempts to start a bend at the point. Returns true if a bend creation started.
     *
     * NOTE: coin deduction is intentionally omitted here — integrate via model.getCoinManager() if desired.
     */
    public boolean startBendAt(Point clickPoint) {
        if (!bendMode) return false;

        Wire w = findWireNear(clickPoint);
        if (w == null) return false;
        if (w.getBends().size() >= 3) {
            System.out.println("⚠️ This wire already has 3 bends.");
            return false;
        }

        // create provisional bend point at clicked location
        Point bend = new Point(clickPoint);
        w.addBend(bend);
        activeWire = w;
        selectedBendPoint = bend;
        bendOrigin = new Point(bend);

        return true;
    }

    /**
     * Called while dragging the mouse during bend creation. Clamps the drag to 50px from the origin.
     */
    public void updateBendDrag(Point p) {
        if (activeWire == null || selectedBendPoint == null || bendOrigin == null) return;

        int dx = p.x - bendOrigin.x;
        int dy = p.y - bendOrigin.y;
        double dist = Math.hypot(dx, dy);
        if (dist > 50.0) {
            double ratio = 50.0 / dist;
            dx = (int) Math.round(dx * ratio);
            dy = (int) Math.round(dy * ratio);
        }
        selectedBendPoint.setLocation(bendOrigin.x + dx, bendOrigin.y + dy);
    }

    /**
     * Optional: update hover highlight while in bend mode (not required).
     * We'll find the nearest wire and store it as active for possible quick-add.
     */
    public void updateBendHover(Point p) {
        if (!bendMode) return;
        Wire w = findWireNear(p);
        // if user just hovers and hasn't started a bend, we could highlight w; not needed for basic behavior
        // store it for quick-start if you'd like:
        if (activeWire == null) activeWire = w;
    }

    /**
     * Finalize the bend: validate wire vs systems. If invalid, remove the provisional bend.
     * Returns true if the bend was accepted.
     */
    public boolean finishBend() {
        if (!bendMode || activeWire == null || selectedBendPoint == null) {
            // nothing to do
            activeWire = null;
            selectedBendPoint = null;
            bendOrigin = null;
            return false;
        }

        // validate that after adding this bend, wire does not intersect any system rectangle
        boolean ok = isWireValid(activeWire);
        if (!ok) {
            // revert: remove the provisional bend
            activeWire.removeBend(selectedBendPoint);
            System.out.println("❌ Bend invalid — wire would cross a system. Bend removed.");
            // TODO: optionally refund coin here
        } else {
            // final: keep bend as-is (Wire.draw uses sorted bends at draw time)
            // optionally persist state: levelManager.saveStateToFile(...)
        }

        // clear active bend state
        activeWire = null;
        selectedBendPoint = null;
        bendOrigin = null;
        return ok;
    }

    /**
     * Validate a wire by checking each segment against each system rectangle.
     * A segment may touch the endpoint system (origin or target) but must not cross any other system.
     */
    private boolean isWireValid(Wire w) {
        GameModel model = levelManager.getModel();
        if (model == null) return false; // be conservative

        List<Line2D> segs = w.getSegments();
        for (Line2D seg : segs) {
            for (SystemNode node : model.getSystems()) {
                Rectangle r = new Rectangle(node.getX(), node.getY(), node.getWidth(), node.getHeight());

                // If the segment intersects node bounds
                if (r.intersectsLine(seg)) {
                    // allow if node is the wire's source system or destination system
                    boolean isSource = node.getOutputPorts().contains(w.getOutputPort());
                    boolean isDest   = node.getInputPorts().contains(w.getInputPort());
                    if (!isSource && !isDest) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
