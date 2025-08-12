package model.behavior;

import model.Packet;
import model.Port;
import model.SystemNode;
import model.Wire;

import java.util.ArrayList;
import java.util.List;

/**
 * Split behavior: send one packet into every free output wire of the node.
 */
public class SplitBehavior implements SystemBehavior {
    @Override
    public List<Wire> chooseOutputWires(SystemNode node, Packet incoming, List<Wire> allWires) {
        List<Wire> chosen = new ArrayList<>();
        for (Port out : node.getOutputPorts()) {
            for (Wire w : allWires) {
                if (w.getOutputPort() == out && !w.hasPacket()) {
                    chosen.add(w);
                    break;
                }
            }
        }
        return chosen;
    }
}
