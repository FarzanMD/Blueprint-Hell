package view;

import controller.MouseController;
import controller.PacketManager;
import controller.ShopManager;
import controller.WireController;
import model.GameModel;
import model.Packet;
import model.SystemNode;
import model.Wire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameModel model;
    private final WireController wireController;
    private final PacketManager packetManager;
    private boolean isRunning = false;
    private Timer gameTimer;
    private final ShopManager shopManager;


    public void pauseGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public void resumeGame() {
        if (gameTimer != null) {
            gameTimer.start();
        }
    }


    public GamePanel() {
        setBackground(Color.WHITE);
        model = new GameModel();
        wireController = new WireController();
        packetManager = new PacketManager(model.getCoinManager());

        setFocusable(true);
        requestFocusInWindow();

        MouseController mouseController = new MouseController(model, wireController);
        addMouseListener(mouseController);
        addMouseMotionListener(mouseController);


        shopManager = new ShopManager(model.getCoinManager());

        // Spacebar toggles run/pause
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (allSystemsAreValid()) {
                        isRunning = !isRunning;
                        System.out.println("System is now " + (isRunning ? "RUNNING" : "PAUSED"));
                    } else {
                        System.out.println("Cannot start — not all systems are valid!");
                    }
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    pauseGame(); // you should define this
                    int coins = model.getCoinManager().getCoins();
                    new ShopWindow(
                            (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this),
                            coins,
                            e1 -> shopManager.tryBuyAtar(),
                            e2 -> shopManager.tryBuyAiryaman(),
                            e3 -> shopManager.tryBuyAnahita(packetManager)
                    );
                    resumeGame(); // resumes after shop closes
                }
            }
        });
        setFocusable(true);


        gameTimer = new Timer(16, e -> {
            shopManager.update();
            if (isRunning) {
                packetManager.update(0.01f, model.getSystems(), wireController.getWires(), shopManager);
            }
            repaint();
        });
        gameTimer.start();

        // Packet spawning from start node (only while running)
        new Timer(2000, e -> {
            if (!isRunning) return;
            List<SystemNode> systems = model.getSystems();
            if (systems.isEmpty()) return;

            SystemNode start = systems.get(0);
            Wire wire = start.findNextAvailableWire(wireController.getWires());
            if (wire != null) {
                packetManager.spawnPacket(Packet.Shape.TRIANGLE, wire);
            }
        }).start();


    }

    private boolean allSystemsAreValid() {
        List<Wire> wires = wireController.getWires();
        for (SystemNode node : model.getSystems()) {
            if (!node.isFullyConnected(wires)) return false;
        }
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Wire> wires = wireController.getWires();

        for (SystemNode node : model.getSystems()) {
            node.draw((Graphics2D) g, wires);
        }

        wireController.draw((Graphics2D) g);
        packetManager.draw((Graphics2D) g);

        // Optional debug display
        g.setColor(Color.BLACK);
        g.drawString("Status: " + (isRunning ? "RUNNING" : "PAUSED"), 10, 20);

        //g.setColor(Color.BLACK);
        g.setFont(new Font("Noto Emoji", Font.BOLD, 16));
        g.drawString("\uD83E\uDE99"+":" + model.getCoinManager().getCoins(), 10, 40);

    }
}
