package model.behavior;

import model.Packet;
import model.SystemNode;
import model.Wire;

import java.util.List;

/**
 * Merger: Wait until the node has received all bitpackets for a bulkId,
 * then reassemble into a single bulk packet and forward it.
 */
public class MergerBehavior implements SystemBehavior {
    @Override
    public List<WireAction> chooseOutputActions(SystemNode node, Packet incoming, List<Wire> allWires) {
        // If incoming is not a bitpacket -> default
        if (incoming == null || incoming.getBitIndex() < 0) {
            return new DefaultSystemBehavior().chooseOutputActions(node, incoming, allWires);
        }

        // Check node buffer: are all bits present for this bulkId?
        String targetId = incoming.getBulkId();
        int totalBits = incoming.getTotalBits();

        // Count bits in buffer belonging to targetId
        int count = 0;
        for (Packet p : node.getBufferSnapshot()) {
            if (p.getBulkId() != null && p.getBulkId().equals(targetId)) count++;
        }
        // include this incoming bit (not yet enqueued)
        if (count + 1 < totalBits) return List.of();

        // we have all bits — pick first available output wire and spawn a single bulk packet
        for (Wire w : allWires) {
            if (!w.hasPacket() && !w.isDestroyed() && node.getOutputPorts().contains(w.getOutputPort())) {
                // spawn a bulk packet placeholder; PacketManager will convert to bulk metadata on spawn
                return List.of(new WireAction(w, Packet.Shape.SMALL, false, false, false, targetId, -1, totalBits, incoming.getBulkColor(), false));
            }
        }
        return List.of();
    }
}
