package model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;

public class Wire {
    private final Port outputPort;
    private final Port inputPort;
    private boolean hasPacket = false;
    private final ArrayList<Point> bends = new ArrayList<>();


    public Wire(Port outputPort, Port inputPort) {
        this.outputPort = outputPort;
        this.inputPort = inputPort;
    }

    /*
        public int getLength() {
            int dx = inputPort.getX() - outputPort.getX();
            int dy = inputPort.getY() - outputPort.getY();
            return (int) Math.sqrt(dx * dx + dy * dy);
        }

     */
    public int getLength() {
        ArrayList<Point> chain = getPointChain();
        double total = 0.0;
        for (int i = 0; i < chain.size() - 1; i++) {
            Point a = chain.get(i);
            Point b = chain.get(i+1);
            total += a.distance(b);
        }
        return (int) Math.round(total);
    }

    public Port getOutputPort() {
        return outputPort;
    }

    public Port getInputPort() {
        return inputPort;
    }

    public boolean hasPacket() {
        return hasPacket;
    }

    public void setHasPacket(boolean hasPacket) {
        this.hasPacket = hasPacket;
    }


    public void addBend(Point p) {
        if (bends.size() < 3) bends.add(p);
    }

    public void removeBend(Point p) {
        bends.remove(p);
    }

    public ArrayList<Point> getBends() {
        return bends;
    }

    // Returns a *sorted copy* of bends in the natural path order from output -> input
    public ArrayList<Point> getSortedBends() {
        Point out = new Point(outputPort.getX(), outputPort.getY());
        Point in  = new Point(inputPort.getX(), inputPort.getY());
        double dx = in.x - out.x;
        double dy = in.y - out.y;
        double denom = dx*dx + dy*dy;
        ArrayList<Point> copy = new ArrayList<>(bends);
        copy.sort(Comparator.comparingDouble(b -> {
            // project bend onto the output->input vector to get parameter t
            double bx = b.x - out.x;
            double by = b.y - out.y;
            double t = (denom == 0) ? 0.0 : (bx*dx + by*dy) / denom;
            // clamp so off-line points still sort meaningfully
            if (t < 0) return 0.0;
            if (t > 1) return 1.0;
            return t;
        }));
        return copy;
    }
    public ArrayList<Point> getPointChain() {
        ArrayList<Point> chain = new ArrayList<>();
        chain.add(new Point(outputPort.getX(), outputPort.getY()));
        chain.addAll(getSortedBends());
        chain.add(new Point(inputPort.getX(), inputPort.getY()));
        return chain;
    }
    public ArrayList<Line2D> getSegments() {
        ArrayList<Point> chain = getPointChain();
        ArrayList<Line2D> segs = new ArrayList<>();
        for (int i = 0; i < chain.size() - 1; i++) {
            Point a = chain.get(i);
            Point b = chain.get(i+1);
            segs.add(new Line2D.Double(a.x, a.y, b.x, b.y));
        }
        return segs;
    }

    public void draw(Graphics2D g) {
        Color prevColor = g.getColor();

        // color based on whether the wire currently has a packet
        if (hasPacket) g.setColor(Color.RED);
        else g.setColor(Color.BLUE);

        g.setStroke(new BasicStroke(2));

        ArrayList<Point> chain = getPointChain();
        for (int i = 0; i < chain.size() - 1; i++) {
            Point a = chain.get(i);
            Point b = chain.get(i + 1);
            g.drawLine(a.x, a.y, b.x, b.y);
        }

        // draw bend handles (small filled circle). Use a distinct color so user sees them.
        g.setColor(Color.MAGENTA);
        for (Point bend : getSortedBends()) {
            g.fillOval(bend.x - 5, bend.y - 5, 10, 10);
        }

        // restore color
        g.setColor(prevColor);
    }
}
