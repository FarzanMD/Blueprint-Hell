package model.behavior;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.Wire;

import java.util.ArrayList;
import java.util.List;

/**
 * Default behavior for Phase 1:
 * - Choose the first free output wire (by output port order).
 * - If none free, return empty list (caller will enqueue).
 */
public class DefaultSystemBehavior implements SystemBehavior {
    @Override
    public List<Wire> chooseOutputWires(SystemNode node, Packet incoming, List<Wire> allWires) {
        List<Wire> chosen = new ArrayList<>();
        for (Port out : node.getOutputPorts()) {
            for (Wire w : allWires) {
                if (w.getOutputPort() == out && !w.hasPacket()) {
                    chosen.add(w);
                    return chosen; // only one wire in default behavior
                }
            }
        }
        return chosen; // empty -> no free outputs
    }
}
