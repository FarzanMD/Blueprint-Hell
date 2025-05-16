package view;

import controller.MouseController;
import controller.PacketManager;
import controller.WireController;
import model.GameModel;
import model.Packet;
import model.SystemNode;
import model.Wire;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameModel model;
    private final WireController wireController;
    private final PacketManager packetManager;

    public GamePanel() {
        setBackground(Color.WHITE);
        model = new GameModel();
        wireController = new WireController();
        packetManager = new PacketManager();

        MouseController mouseController = new MouseController(model, wireController);
        addMouseListener(mouseController);
        addMouseMotionListener(mouseController);

        Timer timer = new Timer(16, e -> {
            packetManager.update(0.01f, model.getSystems(), wireController.getWires());
            repaint();
        });

        timer.start();


        // Real spawning from the first available wire of the start node
        new Timer(2000, e -> {
            if (model.getSystems().isEmpty()) return;

            SystemNode start = model.getSystems().get(0); // Assume system 0 is the start node
            Wire wire = start.findNextAvailableWire(wireController.getWires());
            if (wire != null) {
                packetManager.spawnPacket(Packet.Shape.SQUARE, wire);
            }
        }).start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (SystemNode node : model.getSystems()) {
            node.draw((Graphics2D) g);
        }
        wireController.draw((Graphics2D) g);
        packetManager.draw((Graphics2D) g);
    }
}
