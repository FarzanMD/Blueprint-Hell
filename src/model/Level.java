package model;

import java.util.List;

public class Level {
    public final List<SystemDefinition> systems;
    public final List<WireDefinition> wires;
    public final int squarePacketCount;
    public final int trianglePacketCount;
    public final int maxWireLength;

    public Level(List<SystemDefinition> systems,
                 List<WireDefinition> wires,
                 int squarePacketCount,
                 int trianglePacketCount,
                 int maxWireLength) {
        this.systems = systems;
        this.wires = wires;
        this.squarePacketCount = squarePacketCount;
        this.trianglePacketCount = trianglePacketCount;
        this.maxWireLength = maxWireLength;
    }
}
