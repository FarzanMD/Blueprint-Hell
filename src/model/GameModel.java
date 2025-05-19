package model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final List<SystemNode> systems = new ArrayList<>();
    private final CoinManager coinManager = new CoinManager();



    public GameModel() {
        SystemNode sys1 = new SystemNode(100, 100, 120, 100);
        sys1.addOutputPort(Port.Type.SQUARE);


        SystemNode sys2 = new SystemNode(400, 100, 120, 100);
        sys2.addInputPort(Port.Type.SQUARE);
        sys2.addOutputPort(Port.Type.TRIANGLE);
        sys2.addOutputPort(Port.Type.SQUARE);

        SystemNode sys3 = new SystemNode(700, 100, 120, 100);
        sys3.addInputPort(Port.Type.SQUARE);
        sys3.addInputPort(Port.Type.TRIANGLE);


        systems.add(sys1);
        systems.add(sys2);
        systems.add(sys3);
    }

    public List<SystemNode> getSystems() {
        return systems;
    }

    public void addSystem(SystemNode system) {
        systems.add(system);
    }
    public CoinManager getCoinManager() {
        return coinManager;
    }
}
