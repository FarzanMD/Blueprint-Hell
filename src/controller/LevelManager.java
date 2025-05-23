package controller;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private final GameModel model;
    private Level currentLevel;

    public LevelManager(GameModel model) {
        this.model = model;
    }

    /** Load a level from a JSON file (org.json). */
    public void loadLevelFromFile(String path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path)) {
            JSONTokener tok = new JSONTokener(fis);
            JSONObject obj = new JSONObject(tok);
            Level lvl = parseLevel(obj);
            loadLevel(lvl);
        }
    }

    /** Parse a JSONObject into a Level. */
    private Level parseLevel(JSONObject o) {
        // systems
        JSONArray sysArr = o.getJSONArray("systems");
        List<SystemDefinition> systems = new ArrayList<>();
        for (int i = 0; i < sysArr.length(); i++) {
            JSONObject so = sysArr.getJSONObject(i);
            int x = so.getInt("x"), y = so.getInt("y");
            int w = so.getInt("width"), h = so.getInt("height");

            JSONArray inArr = so.getJSONArray("inputTypes");
            List<Port.Type> ins = new ArrayList<>();
            for (int j = 0; j < inArr.length(); j++) {
                ins.add(Port.Type.valueOf(inArr.getString(j)));
            }

            JSONArray outArr = so.getJSONArray("outputTypes");
            List<Port.Type> outs = new ArrayList<>();
            for (int j = 0; j < outArr.length(); j++) {
                outs.add(Port.Type.valueOf(outArr.getString(j)));
            }

            systems.add(new SystemDefinition(x, y, w, h, ins, outs));
        }

        // wires
        JSONArray wArr = o.getJSONArray("wires");
        List<WireDefinition> wires = new ArrayList<>();
        for (int i = 0; i < wArr.length(); i++) {
            JSONObject wo = wArr.getJSONObject(i);
            wires.add(new WireDefinition(
                    wo.getInt("fromSystem"),
                    wo.getInt("fromPortIndex"),
                    wo.getInt("toSystem"),
                    wo.getInt("toPortIndex")
            ));
        }

        int sq = o.getInt("squarePacketCount");
        int tr = o.getInt("trianglePacketCount");
        int maxLen = o.getInt("maxWireLength");

        return new Level(systems, wires, sq, tr, maxLen);
    }

    /** Load a Level into the GameModel. */
    public void loadLevel(Level lvl) {
        this.currentLevel = lvl;
        model.clear();
        WireController wc = model.getWireController();

        // 1) systems
        for (SystemDefinition sd : lvl.systems) {
            SystemNode node = new SystemNode(sd.x, sd.y, sd.width, sd.height);
            for (Port.Type t : sd.inputTypes)  node.addInputPort(t);
            for (Port.Type t : sd.outputTypes) node.addOutputPort(t);
            model.addSystem(node);
        }

        // 2) wires
        List<SystemNode> syss = model.getSystems();
        for (WireDefinition wd : lvl.wires) {
            SystemNode from = syss.get(wd.fromSystem);
            SystemNode to   = syss.get(wd.toSystem);
            Port out = from.getOutputPorts().get(wd.fromPortIndex);
            Port in  = to  .getInputPorts() .get(wd.toPortIndex);
            wc.tryConnectDirect(out, in);
        }

        // 3) settings
        model.setMaxWireLength(lvl.maxWireLength);
        model.setPacketGoals(lvl.squarePacketCount, lvl.trianglePacketCount);
    }

    /** Save the *current* network (systems + wires) back to JSON. */
    public void saveStateToFile(String path) throws Exception {
        JSONObject o = new JSONObject();

        // systems
        JSONArray sysArr = new JSONArray();
        for (SystemDefinition sd : model.exportSystemDefinitions()) {
            JSONObject so = new JSONObject();
            so.put("x", sd.x);
            so.put("y", sd.y);
            so.put("width", sd.width);
            so.put("height", sd.height);

            JSONArray inArr = new JSONArray();
            sd.inputTypes.forEach(t -> inArr.put(t.name()));
            so.put("inputTypes", inArr);

            JSONArray outArr = new JSONArray();
            sd.outputTypes.forEach(t -> outArr.put(t.name()));
            so.put("outputTypes", outArr);

            sysArr.put(so);
        }
        o.put("systems", sysArr);

        // wires
        JSONArray wArr = new JSONArray();
        for (WireDefinition wd : model.exportWireDefinitions()) {
            JSONObject wo = new JSONObject();
            wo.put("fromSystem", wd.fromSystem);
            wo.put("fromPortIndex", wd.fromPortIndex);
            wo.put("toSystem", wd.toSystem);
            wo.put("toPortIndex", wd.toPortIndex);
            wArr.put(wo);
        }
        o.put("wires", wArr);

        // packet & wire limits from the loaded level
        o.put("squarePacketCount", currentLevel.squarePacketCount);
        o.put("trianglePacketCount", currentLevel.trianglePacketCount);
        o.put("maxWireLength", currentLevel.maxWireLength);

        // write out
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(o.toString(2).getBytes(StandardCharsets.UTF_8));
        }
    }
}
