package View;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public GamePanel() {
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Placeholder drawing
        g.setColor(Color.WHITE);
        g.drawString("Game is running...", 50, 50);
    }
}
