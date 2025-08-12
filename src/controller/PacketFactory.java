package controller;

import model.Packet;
import model.Port;
import model.Wire;

import java.util.List;

/**
 * Small factory that constructs Packet instances with the correct
 * start-system input ports injected (used by PacketManager).
 */
public class PacketFactory {
    private final List<Port> startInputs;

    public PacketFactory(List<Port> startInputs) {
        this.startInputs = startInputs;
    }

    public Packet create(Packet.Shape shape, Wire wire) {
        return new Packet(shape, wire, startInputs);
    }
}
