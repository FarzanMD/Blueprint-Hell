package model.behavior;

import model.Packet;
import model.SystemNode;
import model.Wire;

import java.util.List;

/**
 * Strategy to decide which output wires a system should send packets to
 * when a packet arrives at that system.
 *
 * Implementations must return only wires that are safe for immediate use
 * (i.e. free wires, not already occupied). If none are available they may
 * return an empty list — the node will enqueue the packet.
 */
public interface SystemBehavior {
    /**
     * Choose zero or more output wires for the arriving packet.
     *
     * @param node     the system node receiving the packet
     * @param incoming the arriving packet (may be used to inspect shape/hp)
     * @param allWires the full list of wires in the network
     * @return list of wires to send new packets into (empty -> enqueue)
     */
    List<Wire> chooseOutputWires(SystemNode node, Packet incoming, List<Wire> allWires);
}
