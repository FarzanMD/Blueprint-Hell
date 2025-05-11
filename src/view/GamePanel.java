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
            packetManager.update(0.01f); // Simulate delta time
            repaint();
        });
        timer.start();

        // TEST: spawn a packet every 2 seconds if any wire exists
        new Timer(2000, e -> {
            List<Wire> wires = wireController.getWires();
            if (wires.size() >= 2) {
                packetManager.spawnPacket(Packet.Shape.SQUARE, List.of(wires.get(0), wires.get(1)));
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
