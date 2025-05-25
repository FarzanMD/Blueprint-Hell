package model;

import controller.LevelManager;
import controller.WireController;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final List<SystemNode> systems = new ArrayList<>();
    private final CoinManager coinManager = new CoinManager();
    private WireController wireController;
    private final LevelManager levelManager;
    private int maxWireLength;
    private int goalSquare, goalTriangle;



    public GameModel() {


        // 2) Load the hard-coded (or JSON) level via LevelManager
       this.levelManager = new LevelManager(this);
       this.wireController = new WireController(levelManager);
        try {
            levelManager.loadLevelFromFile("src/save.json");
            // → save.json must live in your working directory
        } catch (Exception ex) {
            ex.printStackTrace();
            // Fallback: if load fails, you could set up a default level here
        }
    }

    public GameModel(String path) {


        // 2) Load the hard-coded (or JSON) level via LevelManager
       this.levelManager = new LevelManager(this);
       this.wireController = new WireController(levelManager);
        try {
            levelManager.loadLevelFromFile(path);
            // → save.json must live in your working directory
        } catch (Exception ex) {
            ex.printStackTrace();
            // Fallback: if load fails, you could set up a default level here
        }
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
    public void clear() {
        systems.clear();
        wireController.clearWires();
    }

    public WireController getWireController() { return wireController; }

    public void setMaxWireLength(int len) {
        this.maxWireLength = len;
        wireController.setMAX_TOTAL_LENGTH(len);
    }

    public void setPacketGoals(int squares, int triangles) {
        this.goalSquare = squares;
        this.goalTriangle = triangles;
    }

    public List<SystemDefinition> exportSystemDefinitions() {
        List<SystemDefinition> defs = new ArrayList<>();
        for (SystemNode n : systems) {
            defs.add(new SystemDefinition(
                    n.getX(), n.getY(), n.getWidth(), n.getHeight(),
                    n.getInputPortTypes(), n.getOutputPortTypes()
            ));
        }
        return defs;
    }

    // In model/GameModel

    public List<WireDefinition> exportWireDefinitions() {
        List<WireDefinition> defs = new ArrayList<>();
        List<SystemNode> syss = getSystems();

        for (Wire wire : wireController.getWires()) {
            Port out = wire.getOutputPort();
            Port in  = wire.getInputPort();

            // find the system index and port index for the output
            int fromSys = -1, fromPortIdx = -1;
            for (int i = 0; i < syss.size(); i++) {
                List<Port> outs = syss.get(i).getOutputPorts();
                if (outs.contains(out)) {
                    fromSys = i;
                    fromPortIdx = outs.indexOf(out);
                    break;
                }
            }

            // find the system index and port index for the input
            int toSys = -1, toPortIdx = -1;
            for (int i = 0; i < syss.size(); i++) {
                List<Port> ins = syss.get(i).getInputPorts();
                if (ins.contains(in)) {
                    toSys = i;
                    toPortIdx = ins.indexOf(in);
                    break;
                }
            }

            if (fromSys >= 0 && toSys >= 0) {
                defs.add(new WireDefinition(fromSys, fromPortIdx, toSys, toPortIdx));
            }
        }
        return defs;
    }

    public void setWireController(WireController wireController) {
        this.wireController = wireController;
    }
}
