package Model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final List<SystemNode> systems = new ArrayList<>();

    public GameModel() {
        SystemNode sys1 = new SystemNode(100, 100, 120, 100);
        sys1.addOutputPort(Port.Type.SQUARE);
        sys1.addOutputPort(Port.Type.TRIANGLE);

        SystemNode sys2 = new SystemNode(400, 100, 120, 100);
        sys2.addInputPort(Port.Type.SQUARE);
        sys2.addInputPort(Port.Type.TRIANGLE);

        systems.add(sys1);
        systems.add(sys2);
    }

    public List<SystemNode> getSystems() {
        return systems;
    }

    public void addSystem(SystemNode system) {
        systems.add(system);
    }
}
