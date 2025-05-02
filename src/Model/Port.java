package Model;

import java.awt.*;

public class Port {
    public enum Type { SQUARE, TRIANGLE }
    public enum Side { LEFT, RIGHT }

    private final Type type;
    private final Side side;
    private int x, y;

    public Port(Type type, Side side, int x, int y) {
        this.type = type;
        this.side = side;
        this.x = x;
        this.y = y;
    }

    public Type getType() { return type; }
    public Side getSide() { return side; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g) {
        switch (type) {
            case SQUARE -> g.fillRect(x - 5, y - 5, 10, 10);
            case TRIANGLE -> {
                int[] xs = {x, x - 6, x + 6};
                int[] ys = side == Side.LEFT
                        ? new int[]{y - 6, y + 6, y + 6}
                        : new int[]{y + 6, y - 6, y - 6};
                g.fillPolygon(xs, ys, 3);
            }
        }
    }
}
